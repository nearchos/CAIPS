package org.inspirecenter.indoorpositioningsystem.algorithms;

import org.inspirecenter.indoorpositioningsystem.model.Coordinates;
import org.inspirecenter.indoorpositioningsystem.model.RadioScanEntry;
import org.inspirecenter.indoorpositioningsystem.model.MeasurementEntry;

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
    public Coordinates estimateLocation(final List<MeasurementEntry> measurementEntries, final MeasurementEntry scannedMeasurementEntry) {
        // this approach computes the 'matching' for the training points that come from the SAME DEVICE TYPE only, and selects the best match
        // todo handle floor

        final double batteryLevel = (Double) scannedMeasurementEntry.getContext().get("batteryLevel");
        final double batteryScale = (Double) scannedMeasurementEntry.getContext().get("batteryScale");
        final double batteryPercentage = batteryLevel / batteryScale;

        final List<RadioScanEntry> scannedRadioScanEntries = scannedMeasurementEntry.getRadioScans();
        final HashMap<MeasurementEntry,Double> trainingsToScores = new HashMap<MeasurementEntry, Double>();
        // compute the matching score for all training points
        for(final MeasurementEntry measurementEntry : measurementEntries) {
            final double trainingBatteryLevel = (Double) measurementEntry.getContext().get("batteryLevel");
            final double trainingBatteryScale = (Double) measurementEntry.getContext().get("batteryScale");
            final double trainingBatteryPercentage = trainingBatteryLevel / trainingBatteryScale;

            if(Math.abs(trainingBatteryPercentage - batteryPercentage) < similarityMargin) { // only consider similar battery percentage' measurementEntries
                trainingsToScores.put(measurementEntry, getScore(measurementEntry, scannedRadioScanEntries));
            }
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
    public double getScore(MeasurementEntry measurementEntry, List<RadioScanEntry> scannedRadioScanEntries) {
        // initially, assume all fingerprints have not been checked
        final HashMap<String,Double> uncheckedTrainingMeasurements = new HashMap<>();
        for(final RadioScanEntry radioScanEntry : measurementEntry.getRadioScans()) {
            uncheckedTrainingMeasurements.put(radioScanEntry.getMacAddress(), radioScanEntry.getLevelAsRatio());
        }

        double sum = 0d;
        for(final RadioScanEntry scannedRadioScanEntry : scannedRadioScanEntries) {
            // android reports level as a range from -100 (very poor) to 0 (excellent)
            // i convert it to 0 (very poor) to 1 excellent
            final double scannedLevel = (scannedRadioScanEntry.getLevelAsRatio() + 100d) / 100d;
            final double trainingLevel;
            if(uncheckedTrainingMeasurements.containsKey(scannedRadioScanEntry.getMacAddress())) {
                trainingLevel = (uncheckedTrainingMeasurements.remove(scannedRadioScanEntry.getMacAddress()) + 100d) / 100d;
            } else {
                trainingLevel = 0d;
            }
            sum += Math.pow(scannedLevel - trainingLevel, 2d);
        }

        // now, account for all unchecked fingerprints
        for(final String trainingMeasurement : uncheckedTrainingMeasurements.keySet()) {
            sum += Math.pow((uncheckedTrainingMeasurements.get(trainingMeasurement) + 100d)/100d, 2d);
        }

        final int maxSize = Math.max(scannedRadioScanEntries.size(), measurementEntry.getRadioScans().size());

        return 1 - Math.sqrt(sum / maxSize);
    }

    @Override
    public String toString() {
        return getName();
    }
}