package org.inspirecenter.indoorpositioningsystem.algorithms;

import org.inspirecenter.indoorpositioningsystem.model.Coordinates;
import org.inspirecenter.indoorpositioningsystem.model.Measurement;
import org.inspirecenter.indoorpositioningsystem.model.Training;

import java.util.HashMap;
import java.util.List;

/**
 * @author npaspallis
 * Created on 24/09/2014.
 */
public abstract class AbstractLocationEstimationAlgorithm implements LocationEstimationAlgorithm {

    public Coordinates estimateLocation(final List<Training> trainings, final Training scannedTraining) {
        // this approach simply computes the 'matching' for all training points, and selects the best match
        // todo handle floor

        final List<Measurement> scannedMeasurements = scannedTraining.getMeasurements();
        final HashMap<Training, Double> trainingsToScores = new HashMap<>();

        // compute the matching score for all training points
        for(final Training training : trainings) {
            trainingsToScores.put(training, getScore(training, scannedMeasurements));
        }

        // select the one with the best match (i.e. highest score)
        double maxScore = 0d;
        Training bestMatchTraining = trainings.get(0);
        for(final Training training : trainingsToScores.keySet()) {
            final double currentScore = trainingsToScores.get(training);
            if(currentScore > maxScore) {
                maxScore = currentScore;
                bestMatchTraining = training;
            }
        }

        return new Coordinates(bestMatchTraining.getLat(), bestMatchTraining.getLng());
    }
}