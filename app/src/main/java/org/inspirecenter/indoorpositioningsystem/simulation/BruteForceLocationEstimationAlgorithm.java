package org.inspirecenter.indoorpositioningsystem.simulation;

import org.inspirecenter.indoorpositioningsystem.model.MeasurementEntry;
import org.inspirecenter.indoorpositioningsystem.model.RadioDataEntry;

import java.util.HashMap;
import java.util.List;

/**
 * @author npaspallis
 * Created on 24/09/2014.
 */
public class BruteForceLocationEstimationAlgorithm extends AbstractLocationEstimationAlgorithm implements LocationEstimationAlgorithm
{
    @Override
    public String getName()
    {
        return "Brute force algorithm";
    }

    @Override
    public double getScore(MeasurementEntry measurementEntry, List<RadioDataEntry> scannedRadioScanEntries)
    {
        // initially, assume all fingerprints have not been checked
        final HashMap<String,Double> uncheckedTrainingMeasurements = new HashMap<>();
        for(final RadioDataEntry RadioDataEntry : measurementEntry.getRadioDataEntries())
        {
            uncheckedTrainingMeasurements.put(RadioDataEntry.getMacAddress(), RadioDataEntry.getLevelAsRatio());
        }

        double sum = 0d;
        for(final RadioDataEntry scannedRadioDataEntry : scannedRadioScanEntries)
        {
            // android reports level as a range from -100 (very poor) to 0 (excellent)
            // i convert it to 0 (very poor) to 1 excellent
            final double scannedLevel = (scannedRadioDataEntry.getLevelAsRatio() + 100d) / 100d;
            final double trainingLevel;
            if(uncheckedTrainingMeasurements.containsKey(scannedRadioDataEntry.getMacAddress()))
            {
                trainingLevel = (uncheckedTrainingMeasurements.remove(scannedRadioDataEntry.getMacAddress()) + 100d) / 100d;
            }
            else
            {
                trainingLevel = 0d;
            }
            sum += Math.pow(scannedLevel - trainingLevel, 2d);
        }

        // now, account for all unchecked fingerprints
        for(final String trainingMeasurement : uncheckedTrainingMeasurements.keySet())
        {
            sum += Math.pow((uncheckedTrainingMeasurements.get(trainingMeasurement) + 100d)/100d, 2d);
        }

        final int maxSize = Math.max(scannedRadioScanEntries.size(), measurementEntry.getRadioDataEntries().size());

        return 1 - Math.sqrt(sum / maxSize);
    }
}
