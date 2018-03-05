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
public interface LocationEstimationAlgorithm {

    public String getName();

    public Coordinates estimateLocation(final List<Training> trainings, final Training scannedTraining);

    /**
     * A value in the range 0..1, where 0 is no match, and 1 is perfect match.
     *
     * @param training the training set containing the checked scanned measurements
     * @param scannedMeasurements the actual scanned measurements to compare to
     * @return a value in the range 0..1, where 0 is no match, and 1 is perfect match.
     */
    public double getScore(final Training training, final List<Measurement> scannedMeasurements);
}