package org.inspirecenter.indoorpositioningsystem.algorithms;

import org.inspirecenter.indoorpositioningsystem.model.Measurement;
import org.inspirecenter.indoorpositioningsystem.model.Training;

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
    public double getScore(Training training, List<Measurement> scannedMeasurements)
    {
        // initially, assume all fingerprints have not been checked
        final HashMap<String,Double> uncheckedTrainingMeasurements = new HashMap<>();
        for(final Measurement measurement : training.getMeasurements())
        {
            uncheckedTrainingMeasurements.put(measurement.getMacAddress(), measurement.getLevelAsRatio());
        }

        double sum = 0d;
        for(final Measurement scannedMeasurement : scannedMeasurements)
        {
            // android reports level as a range from -100 (very poor) to 0 (excellent)
            // i convert it to 0 (very poor) to 1 excellent
            final double scannedLevel = (scannedMeasurement.getLevelAsRatio() + 100d) / 100d;
            final double trainingLevel;
            if(uncheckedTrainingMeasurements.containsKey(scannedMeasurement.getMacAddress()))
            {
                trainingLevel = (uncheckedTrainingMeasurements.remove(scannedMeasurement.getMacAddress()) + 100d) / 100d;
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

        final int maxSize = Math.max(scannedMeasurements.size(), training.getMeasurements().size());

        return 1 - Math.sqrt(sum / maxSize);
    }
}
