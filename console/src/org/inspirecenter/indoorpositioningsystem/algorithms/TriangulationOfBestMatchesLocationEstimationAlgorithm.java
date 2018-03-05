package org.inspirecenter.indoorpositioningsystem.algorithms;

import org.inspirecenter.indoorpositioningsystem.model.Coordinates;
import org.inspirecenter.indoorpositioningsystem.model.MeasurementEntry;
import org.inspirecenter.indoorpositioningsystem.model.RadioScanEntry;

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
    public Coordinates estimateLocation(final List<MeasurementEntry> measurementEntries, final MeasurementEntry scannedMeasurementEntry) {
        // this approach simply computes the 'matching' for all training points, and selects the best match
        // todo handle floor

        final List<RadioScanEntry> scannedRadioScanEntries = scannedMeasurementEntry.getRadioScans();
        final HashMap<MeasurementEntry,Double> trainingsToScores = new HashMap<MeasurementEntry, Double>();
        // compute the matching score for all training points
        for(final MeasurementEntry measurementEntry : measurementEntries) {
            trainingsToScores.put(measurementEntry, getScore(measurementEntry, scannedRadioScanEntries));
        }

        // sort the map, according to score values - descending
        final SortedSet<Map.Entry<MeasurementEntry,Double>> sortedEntries = new TreeSet<Map.Entry<MeasurementEntry,Double>> (
                new Comparator<Map.Entry<MeasurementEntry,Double>>() {
                    @Override public int compare(Map.Entry<MeasurementEntry,Double> e1, Map.Entry<MeasurementEntry,Double> e2) {
                        return e2.getValue().compareTo(e1.getValue()); // sort descending
                    }
                }
        );
        sortedEntries.addAll(trainingsToScores.entrySet()); // add and sort (descending) at the same time

        final int maxNumberOfEntries = Math.min(maxMatches, sortedEntries.size());

        // first iteration - compute the total score
        Iterator<Map.Entry<MeasurementEntry,Double>> sortedEntriesIterator = sortedEntries.iterator();
        double totalScore = 0d;
        for(int i = 0; i < maxNumberOfEntries; i++) {
            totalScore += sortedEntriesIterator.next().getValue();
        }
        // second iteration - compute the lat,lng
        sortedEntriesIterator = sortedEntries.iterator();
        double lat = 0d;
        double lng = 0d;
        for(int i = 0; i < maxNumberOfEntries; i++) {
            final Map.Entry<MeasurementEntry,Double> entry = sortedEntriesIterator.next();
            final MeasurementEntry measurementEntry = entry.getKey();
            final double score = entry.getValue();
            double weight = score / totalScore;
            lat += measurementEntry.getLat() * weight;
            lng += measurementEntry.getLng() * weight;
        }

        return new Coordinates(lat, lng);
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