package org.inspirecenter.indoorpositioningsystem.model;

import java.io.Serializable;
import java.util.List;

public final class Dataset implements Serializable {

    private Long id = null;

    private String createdBy;

    private long timestamp;

    private int formatVersion;

    private String description;

    private List<MeasurementEntry> measurementEntries;

    public Dataset(final DatasetMetadata datasetMetadata, final List<MeasurementEntry> measurementEntries) {
        this(datasetMetadata.getId(), datasetMetadata.getCreatedBy(), datasetMetadata.getTimestamp(),
                datasetMetadata.getFormatVersion(), datasetMetadata.getDescription(), measurementEntries);
    }

    public Dataset(final Long id, String createdBy, long timestamp, int formatVersion, String description, final List<MeasurementEntry> measurementEntries) {
        this(createdBy, formatVersion, formatVersion, description, measurementEntries);
        this.id = id;
    }

    public Dataset(String createdBy, long timestamp, int formatVersion, String description, final List<MeasurementEntry> measurementEntries) {
        this.createdBy = createdBy;
        this.timestamp = timestamp;
        this.formatVersion = formatVersion;
        this.description = description;
        this.measurementEntries = measurementEntries;
    }

    private Dataset(String createdBy, int formatVersion, String description, final List<MeasurementEntry> measurementEntries) {
        this(createdBy, System.currentTimeMillis(), formatVersion, description, measurementEntries);
    }

    public Long getId() {
        return id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getFormatVersion() {
        return formatVersion;
    }

    public String getDescription() {
        return description;
    }

    public int getNumberOfMeasurements() {
        return measurementEntries.size();
    }

    public List<MeasurementEntry> getMeasurementEntries() {
        return measurementEntries;
    }

    public MeasurementEntry getMeasurementEntry(final int index) {
        return measurementEntries.get(index);
    }
}