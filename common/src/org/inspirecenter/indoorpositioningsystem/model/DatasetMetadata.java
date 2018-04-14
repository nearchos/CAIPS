package org.inspirecenter.indoorpositioningsystem.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.io.Serializable;

@Entity
public final class DatasetMetadata implements Serializable {

    @Id
    private Long id;

    @Index
    private String createdBy;

    @Index
    private long timestamp;

    private int formatVersion;

    private String description;

    public DatasetMetadata() { /* empty constructor needed by Objectify */ }

    public DatasetMetadata(final Dataset dataset) {
        this.id = dataset.getId();
        this.createdBy = dataset.getCreatedBy();
        this.timestamp = dataset.getTimestamp();
        this.formatVersion = dataset.getFormatVersion();
        this.description = dataset.getDescription();
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
}