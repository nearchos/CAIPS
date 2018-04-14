package org.inspirecenter.indoorpositioningsystem.simulation;

import org.inspirecenter.indoorpositioningsystem.model.ContextEntry;
import org.inspirecenter.indoorpositioningsystem.model.ContextValue;
import org.inspirecenter.indoorpositioningsystem.model.Coordinates;
import org.inspirecenter.indoorpositioningsystem.model.MeasurementEntry;
import org.inspirecenter.indoorpositioningsystem.model.RadioDataEntry;

import java.util.HashMap;
import java.util.List;

/**
 * @author npaspallis
 * Created on 24/09/2014.
 */
public class BruteForceSameDeviceLocationEstimationAlgorithm
        extends AbstractLocationEstimationAlgorithm
        implements LocationEstimationAlgorithm {
    @Override
    public String getName()
    {
        return "Brute force - Same device - algorithm";
    }

    @Override
    public Coordinates estimateLocation(final List<MeasurementEntry> measurementEntries, final MeasurementEntry scannedMeasurementEntry)
    {
        // this approach computes the 'matching' for the training points that come from the SAME DEVICE TYPE only, and selects the best match
        // todo handle floor

        final ContextEntry scannedContextEntry = getContextEntry(scannedMeasurementEntry, "model");
        final String scannedModel = scannedContextEntry == null ? "unknown" : scannedContextEntry.getContextValue(0).getValue().toString();

        final List<RadioDataEntry> scannedRadioScanEntries = scannedMeasurementEntry.getRadioDataEntries();
        final HashMap<MeasurementEntry,Double> trainingsToScores = new HashMap<MeasurementEntry, Double>();
        // compute the matching score for all training points
        for(final MeasurementEntry measurementEntry : measurementEntries)
        {
            final ContextEntry contextEntry = getContextEntry(measurementEntry, "model");
            final String model = contextEntry == null ? "unknown" : contextEntry.getContextValue(0).getValue().toString();
            if(scannedModel.equals(model)) // only consider same device' measurementEntries
            {
                trainingsToScores.put(measurementEntry, getScore(measurementEntry, scannedRadioScanEntries));
            }
        }

        // select the one with the best match (i.e. highest score)
        double maxScore = 0d;
        MeasurementEntry bestMatchMeasurementEntry = measurementEntries.get(0);
        for(final MeasurementEntry measurementEntry : trainingsToScores.keySet())
        {
            final double currentScore = trainingsToScores.get(measurementEntry);
            if(currentScore > maxScore)
            {
                maxScore = currentScore;
                bestMatchMeasurementEntry = measurementEntry;
            }
        }

        return bestMatchMeasurementEntry.getCoordinates();
    }

    @Override
    public double getScore(MeasurementEntry measurementEntry, List<RadioDataEntry> scannedRadioScanEntries) {
        // initially, assume all fingerprints have not been checked
        final HashMap<String,Double> uncheckedTrainingMeasurements = new HashMap<>();
        for(final RadioDataEntry RadioDataEntry : measurementEntry.getRadioDataEntries()) {
            uncheckedTrainingMeasurements.put(RadioDataEntry.getMacAddress(), RadioDataEntry.getLevelAsRatio());
        }

        double sum = 0d;
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
            sum += Math.pow(scannedLevel - trainingLevel, 2d);
        }

        // now, account for all unchecked fingerprints
        for(final String trainingMeasurement : uncheckedTrainingMeasurements.keySet()) {
            sum += Math.pow((uncheckedTrainingMeasurements.get(trainingMeasurement) + 100d)/100d, 2d);
        }

        final int maxSize = Math.max(scannedRadioScanEntries.size(), measurementEntry.getRadioDataEntries().size());

        return 1 - Math.sqrt(sum / maxSize);
    }
}