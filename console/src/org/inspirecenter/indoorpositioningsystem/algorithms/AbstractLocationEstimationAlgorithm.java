package org.inspirecenter.indoorpositioningsystem.algorithms;

import org.inspirecenter.indoorpositioningsystem.model.Coordinates;
import org.inspirecenter.indoorpositioningsystem.model.MeasurementEntry;
import org.inspirecenter.indoorpositioningsystem.model.RadioScanEntry;

import java.util.HashMap;
import java.util.List;

/**
 * @author npaspallis
 * Created on 24/09/2014.
 */
public abstract class AbstractLocationEstimationAlgorithm implements LocationEstimationAlgorithm {

    public Coordinates estimateLocation(final List<MeasurementEntry> measurementEntries, final MeasurementEntry scannedMeasurementEntry) {
        // this approach simply computes the 'matching' for all training points, and selects the best match
        // todo handle floor

        final List<RadioScanEntry> scannedRadioScanEntries = scannedMeasurementEntry.getRadioScans();
        final HashMap<MeasurementEntry, Double> trainingsToScores = new HashMap<>();

        // compute the matching score for all training points
        for(final MeasurementEntry measurementEntry : measurementEntries) {
            trainingsToScores.put(measurementEntry, getScore(measurementEntry, scannedRadioScanEntries));
        }

        // select the one with the best match (i.e. highest score)
        double maxScore = 0d;
        MeasurementEntry bestMatchMeasurementEntry = measurementEntries.get(0);
        for(final MeasurementEntry measurementEntry : trainingsToScores.keySet()) {
            final double currentScore = trainingsToScores.get(measurementEntry);
            if(currentScore > maxScore) {
                maxScore = currentScore;
                bestMatchMeasurementEntry = measurementEntry;
            }
        }

        return new Coordinates(bestMatchMeasurementEntry.getLat(), bestMatchMeasurementEntry.getLng());
    }

    @Override
    public String toString() {
        return getName();
    }
}