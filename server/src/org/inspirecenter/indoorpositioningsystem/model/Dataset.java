package org.inspirecenter.indoorpositioningsystem.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.List;

@Entity
public final class Dataset {

    @Id
    private long id;

    @Index
    private String createdBy;

    private int formatVersion;

    private String description;

    private List<MeasurementEntry> measurementEntries;

    private Dataset(String createdBy, int formatVersion, String description, List<MeasurementEntry> measurementEntries) {
        this.createdBy = createdBy;
        this.formatVersion = formatVersion;
        this.description = description;
        this.measurementEntries = measurementEntries;
    }
    public Dataset(long id, String createdBy, int formatVersion, String description, List<MeasurementEntry> measurementEntries) {
        this(createdBy, formatVersion, description, measurementEntries);
        this.id = id;
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

    public int getNumberOfMeasurements() {
        return measurementEntries == null ? 0 : measurementEntries.size();
    }

    public List<MeasurementEntry> getMeasurementEntries() {
        return measurementEntries;
    }
}