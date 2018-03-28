package org.inspirecenter.indoorpositioningsystem.model;

public final class DatasetMetadata {

    private long id;

    private String createdBy;

    private int formatVersion;

    private String description;

    private DatasetMetadata(long id, String createdBy, int formatVersion, String description) {
        this.id = id;
        this.createdBy = createdBy;
        this.formatVersion = formatVersion;
        this.description = description;
    }

    DatasetMetadata(final Dataset dataset) {
        this(dataset.getId(), dataset.getCreatedBy(), dataset.getFormatVersion(), dataset.getDescription());
    }

    public long getId() {
        return id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public int getFormatVersion() {
        return formatVersion;
    }

    public String getDescription() {
        return description;
    }
}