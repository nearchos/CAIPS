package org.inspirecenter.indoorpositioningsystem.simulation;

import org.inspirecenter.indoorpositioningsystem.model.RadioDataEntry;
import org.inspirecenter.indoorpositioningsystem.model.MeasurementEntry;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * @author npaspallis
 * Created on 24/09/2014.
 */
public class BruteForceTopMatchesOnlyLocationEstimationAlgorithm extends AbstractLocationEstimationAlgorithm {

    public static final int DEFAULT_MAX_TOP_MATCHES = 3;

    private final int maxTopMatches;

    public BruteForceTopMatchesOnlyLocationEstimationAlgorithm()
    {
        this(DEFAULT_MAX_TOP_MATCHES);
    }

    public BruteForceTopMatchesOnlyLocationEstimationAlgorithm(final int maxTopMatches) {
        this.maxTopMatches = maxTopMatches;
    }

    @Override
    public String getName()
    {
        return "Brute force - Top matches only(" + maxTopMatches + ") - algorithm";
    }

    @Override
    public double getScore(MeasurementEntry measurementEntry, List<RadioDataEntry> scannedRadioScanEntries) {
        // initially, assume all fingerprints have not been checked
        final HashMap<String,Double> uncheckedTrainingMeasurements = new HashMap<>();
        for(final RadioDataEntry RadioDataEntry : measurementEntry.getRadioDataEntries()) {
            uncheckedTrainingMeasurements.put(RadioDataEntry.getMacAddress(), RadioDataEntry.getLevelAsRatio());
        }

        final Vector<Double> differences = new Vector<Double>();
        for(final RadioDataEntry scannedRadioDataEntry : scannedRadioScanEntries) {
            // android reports level as a range from -100 (very poor) to 0 (excellent)
            // i convert it to 0 (very poor) to 1 excellent
            final double scannedLevel = (scannedRadioDataEntry.getLevelAsRatio() + 100d) / 100d;
            final double trainingLevel;
            if(uncheckedTrainingMeasurements.containsKey(scannedRadioDataEntry.getMacAddress())) {
                trainingLevel = (uncheckedTrainingMeasurements.remove(scannedRadioDataEntry.getMacAddress()) + 100d) / 100d;
            } else {
                trainingLevel = 0d;
            }
            differences.add(Math.pow(scannedLevel - trainingLevel, 2d));
        }

        // now, account for all unchecked fingerprints
        for(final String trainingMeasurement : uncheckedTrainingMeasurements.keySet()) {
            differences.add(Math.pow((uncheckedTrainingMeasurements.get(trainingMeasurement) + 100d) / 100d, 2d));
        }

        // now sort differences (ascending order implies best matches first)
        Collections.sort(differences);

        final int maxSize = Math.min(differences.size(), maxTopMatches);
        double sum = 0d;
        for(int i = 0; i < maxSize; i++) sum += differences.get(i);

        return 1 - Math.sqrt(sum / maxSize);
    }
}
