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
public class BruteForceSimilarBatteryLevelLocationEstimationAlgorithm implements LocationEstimationAlgorithm
{
    public static final double DEFAULT_SIMILARITY_MARGIN = 0.1; // 10%

    private double similarityMargin;

    public BruteForceSimilarBatteryLevelLocationEstimationAlgorithm() {
        this(DEFAULT_SIMILARITY_MARGIN);
    }

    public BruteForceSimilarBatteryLevelLocationEstimationAlgorithm(final double similarityMargin) {
        this.similarityMargin = similarityMargin;
    }

    @Override
    public String getName() {
        return "Brute force - Similar battery level(" + similarityMargin + ") - algorithm";
    }

    @Override
    public Coordinates estimateLocation(final List<Training> trainings, final Training scannedTraining) {
        // this approach computes the 'matching' for the training points that come from the SAME DEVICE TYPE only, and selects the best match
        // todo handle floor

        final double batteryLevel = (Double) scannedTraining.getContext().get("batteryLevel");
        final double batteryScale = (Double) scannedTraining.getContext().get("batteryScale");
        final double batteryPercentage = batteryLevel / batteryScale;

        final List<Measurement> scannedMeasurements = scannedTraining.getMeasurements();
        final HashMap<Training,Double> trainingsToScores = new HashMap<Training, Double>();
        // compute the matching score for all training points
        for(final Training training : trainings) {
            final double trainingBatteryLevel = (Double) training.getContext().get("batteryLevel");
            final double trainingBatteryScale = (Double) training.getContext().get("batteryScale");
            final double trainingBatteryPercentage = trainingBatteryLevel / trainingBatteryScale;

            if(Math.abs(trainingBatteryPercentage - batteryPercentage) < similarityMargin) { // only consider similar battery percentage' trainings
                trainingsToScores.put(training, getScore(training, scannedMeasurements));
            }
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

    @Override
    public double getScore(Training training, List<Measurement> scannedMeasurements) {
        // initially, assume all fingerprints have not been checked
        final HashMap<String,Double> uncheckedTrainingMeasurements = new HashMap<>();
        for(final Measurement measurement : training.getMeasurements()) {
            uncheckedTrainingMeasurements.put(measurement.getMacAddress(), measurement.getLevelAsRatio());
        }

        double sum = 0d;
        for(final Measurement scannedMeasurement : scannedMeasurements) {
            // android reports level as a range from -100 (very poor) to 0 (excellent)
            // i convert it to 0 (very poor) to 1 excellent
            final double scannedLevel = (scannedMeasurement.getLevelAsRatio() + 100d) / 100d;
            final double trainingLevel;
            if(uncheckedTrainingMeasurements.containsKey(scannedMeasurement.getMacAddress())) {
                trainingLevel = (uncheckedTrainingMeasurements.remove(scannedMeasurement.getMacAddress()) + 100d) / 100d;
            } else {
                trainingLevel = 0d;
            }
            sum += Math.pow(scannedLevel - trainingLevel, 2d);
        }

        // now, account for all unchecked fingerprints
        for(final String trainingMeasurement : uncheckedTrainingMeasurements.keySet()) {
            sum += Math.pow((uncheckedTrainingMeasurements.get(trainingMeasurement) + 100d)/100d, 2d);
        }

        final int maxSize = Math.max(scannedMeasurements.size(), training.getMeasurements().size());

        return 1 - Math.sqrt(sum / maxSize);
    }
}