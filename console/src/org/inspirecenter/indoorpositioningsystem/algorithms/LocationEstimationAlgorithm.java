package org.inspirecenter.indoorpositioningsystem.algorithms;

import org.inspirecenter.indoorpositioningsystem.model.Coordinates;
import org.inspirecenter.indoorpositioningsystem.model.MeasurementEntry;
import org.inspirecenter.indoorpositioningsystem.model.RadioScanEntry;

import java.util.List;

/**
 * @author npaspallis
 * Created on 24/09/2014.
 */
public interface LocationEstimationAlgorithm {

    public String getName();

    public Coordinates estimateLocation(final List<MeasurementEntry> measurementEntries, final MeasurementEntry scannedMeasurementEntry);

    /**
     * A value in the range 0..1, where 0 is no match, and 1 is perfect match.
     *
     * @param measurementEntry the measurementEntry set containing the checked scanned measurements
     * @param scannedRadioScanEntries the actual scanned measurements to compare to
     * @return a value in the range 0..1, where 0 is no match, and 1 is perfect match.
     */
    public double getScore(final MeasurementEntry measurementEntry, final List<RadioScanEntry> scannedRadioScanEntries);
}