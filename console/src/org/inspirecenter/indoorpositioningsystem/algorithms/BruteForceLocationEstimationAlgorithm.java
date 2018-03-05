package org.inspirecenter.indoorpositioningsystem.algorithms;

import org.inspirecenter.indoorpositioningsystem.model.MeasurementEntry;
import org.inspirecenter.indoorpositioningsystem.model.RadioScanEntry;

import java.util.HashMap;
import java.util.List;

/**
 * @author npaspallis
 * Created on 24/09/2014.
 */
public class BruteForceLocationEstimationAlgorithm extends AbstractLocationEstimationAlgorithm
{
    @Override
    public String getName()
    {
        return "Brute force algorithm";
    }

    @Override
    public double getScore(MeasurementEntry measurementEntry, List<RadioScanEntry> scannedRadioScanEntries)
    {
        // initially, assume all fingerprints have not been checked
        final HashMap<String,Double> uncheckedTrainingMeasurements = new HashMap<>();
        for(final RadioScanEntry radioScanEntry : measurementEntry.getRadioScans())
        {
            uncheckedTrainingMeasurements.put(radioScanEntry.getMacAddress(), radioScanEntry.getLevelAsRatio());
        }

        double sum = 0d;
        for(final RadioScanEntry scannedRadioScanEntry : scannedRadioScanEntries)
        {
            // android reports level as a range from -100 (very poor) to 0 (excellent)
            // i convert it to 0 (very poor) to 1 excellent
            final double scannedLevel = (scannedRadioScanEntry.getLevelAsRatio() + 100d) / 100d;
            final double trainingLevel;
            if(uncheckedTrainingMeasurements.containsKey(scannedRadioScanEntry.getMacAddress()))
            {
                trainingLevel = (uncheckedTrainingMeasurements.remove(scannedRadioScanEntry.getMacAddress()) + 100d) / 100d;
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

        final int maxSize = Math.max(scannedRadioScanEntries.size(), measurementEntry.getRadioScans().size());

        return 1 - Math.sqrt(sum / maxSize);
    }
}
