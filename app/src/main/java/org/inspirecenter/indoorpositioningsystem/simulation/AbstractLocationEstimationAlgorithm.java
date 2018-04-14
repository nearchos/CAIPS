package org.inspirecenter.indoorpositioningsystem.simulation;

import org.inspirecenter.indoorpositioningsystem.model.ContextEntry;
import org.inspirecenter.indoorpositioningsystem.model.ContextValue;
import org.inspirecenter.indoorpositioningsystem.model.Coordinates;
import org.inspirecenter.indoorpositioningsystem.model.MeasurementEntry;
import org.inspirecenter.indoorpositioningsystem.model.RadioDataEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author npaspallis
 * Created on 24/09/2014.
 */
public abstract class AbstractLocationEstimationAlgorithm implements LocationEstimationAlgorithm {

    public Coordinates estimateLocation(final List<MeasurementEntry> measurementEntries, final MeasurementEntry scannedMeasurementEntry) {
        // this approach simply computes the 'matching' for all training points, and selects the best match
        // todo handle floor

        final List<RadioDataEntry> scannedRadioScanEntries = scannedMeasurementEntry.getRadioDataEntries();
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

        return bestMatchMeasurementEntry.getCoordinates();
    }

    public static Map<String,ContextValue[]> getContextMap(final MeasurementEntry measurementEntry) {
        final Map<String,ContextValue[]> contextMap = new HashMap<>();
        final List<ContextEntry> contextEntries = measurementEntry.getContextEntries();
        for(final ContextEntry contextEntry : contextEntries) {
            contextMap.put(contextEntry.getName(), contextEntry.getContextValues());
        }
        return contextMap;
    }

    public static ContextEntry getContextEntry(final MeasurementEntry measurementEntry, final String contextEntryName) {
        final Map<String,ContextValue[]> contextMap = new HashMap<>();
        final List<ContextEntry> contextEntries = measurementEntry.getContextEntries();
        for(final ContextEntry contextEntry : contextEntries) {
            if(contextEntry.getName().equals(contextEntryName)) {
                return contextEntry;
            }
        }
        return null; // todo perhaps throw exception?
    }

    public static ContextValue getContextValue(final MeasurementEntry measurementEntry, final String contextEntryName, final String contextValueLabel) {
        final Map<String,ContextValue[]> contextMap = new HashMap<>();
        final List<ContextEntry> contextEntries = measurementEntry.getContextEntries();
        for(final ContextEntry contextEntry : contextEntries) {
            if(contextEntry.getName().equals(contextEntryName)) {
                final ContextValue [] contextValues = contextEntry.getContextValues();
                for(final ContextValue contextValue : contextValues) {
                    if(contextValue.getLabel().equals(contextValueLabel)) {
                        return contextValue;
                    }
                }
                return null; // todo perhaps throw exception?
            }
        }
        return null; // todo perhaps throw exception?
    }

    @Override
    public String toString() {
        return getName();
    }
}