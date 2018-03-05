package org.inspirecenter.indoorpositioningsystem.algorithms;

import org.inspirecenter.indoorpositioningsystem.model.Coordinates;
import org.inspirecenter.indoorpositioningsystem.model.Measurement;
import org.inspirecenter.indoorpositioningsystem.model.Training;

import java.util.*;

/**
 * @author npaspallis
 * Created on 24/09/2014.
 */
public class TriangulationOfBestMatchesLocationEstimationAlgorithm implements LocationEstimationAlgorithm {

    public static final int DEFAULT_MAX_MATCHES = 3;

    private final int maxMatches;

    public TriangulationOfBestMatchesLocationEstimationAlgorithm()
    {
        this(DEFAULT_MAX_MATCHES);
    }

    public TriangulationOfBestMatchesLocationEstimationAlgorithm(final int maxMatches)
    {
        this.maxMatches = maxMatches;
    }

    @Override
    public String getName()
    {
        return "Triangulation of best matches(" + maxMatches + ") algorithm";
    }

    @Override
    public Coordinates estimateLocation(final List<Training> trainings, final Training scannedTraining) {
        // this approach simply computes the 'matching' for all training points, and selects the best match
        // todo handle floor

        final List<Measurement> scannedMeasurements = scannedTraining.getMeasurements();
        final HashMap<Training,Double> trainingsToScores = new HashMap<Training, Double>();
        // compute the matching score for all training points
        for(final Training training : trainings) {
            trainingsToScores.put(training, getScore(training, scannedMeasurements));
        }

        // sort the map, according to score values - descending
        final SortedSet<Map.Entry<Training,Double>> sortedEntries = new TreeSet<Map.Entry<Training,Double>> (
                new Comparator<Map.Entry<Training,Double>>() {
                    @Override public int compare(Map.Entry<Training,Double> e1, Map.Entry<Training,Double> e2) {
                        return e2.getValue().compareTo(e1.getValue()); // sort descending
                    }
                }
        );
        sortedEntries.addAll(trainingsToScores.entrySet()); // add and sort (descending) at the same time

        final int maxNumberOfEntries = Math.min(maxMatches, sortedEntries.size());

        // first iteration - compute the total score
        Iterator<Map.Entry<Training,Double>> sortedEntriesIterator = sortedEntries.iterator();
        double totalScore = 0d;
        for(int i = 0; i < maxNumberOfEntries; i++) {
            totalScore += sortedEntriesIterator.next().getValue();
        }
        // second iteration - compute the lat,lng
        sortedEntriesIterator = sortedEntries.iterator();
        double lat = 0d;
        double lng = 0d;
        for(int i = 0; i < maxNumberOfEntries; i++) {
            final Map.Entry<Training,Double> entry = sortedEntriesIterator.next();
            final Training training = entry.getKey();
            final double score = entry.getValue();
            double weight = score / totalScore;
            lat += training.getLat() * weight;
            lng += training.getLng() * weight;
        }

        return new Coordinates(lat, lng);
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