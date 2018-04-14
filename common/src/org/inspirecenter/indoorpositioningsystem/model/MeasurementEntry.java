package org.inspirecenter.indoorpositioningsystem.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.io.Serializable;
import java.util.List;

@Entity
public final class MeasurementEntry implements Serializable {

    @Id
    private String uuid;

    @Index
    private Long datasetId;

    private String floorUUID;
    private String createdBy;
    private long timestamp;
    private Coordinates coordinates;
    private List<ContextEntry> contextEntries;
    private List<RadioDataEntry> radioDataEntries;

    public MeasurementEntry() { /* empty constructor needed by Objectify */ }

    public MeasurementEntry(final String uuid, final Long datasetId, final String floorUUID, final String createdBy,
                            final long timestamp, final Coordinates coordinates, List<ContextEntry> contextEntries,
                            final List<RadioDataEntry> radioDataEntries) {
        this.uuid = uuid;
        this.datasetId = datasetId;
        this.floorUUID = floorUUID;
        this.createdBy = createdBy;
        this.timestamp = timestamp;
        this.coordinates = coordinates;
        this.contextEntries = contextEntries;
        this.radioDataEntries = radioDataEntries;
    }

    public String getUuid() {
        return uuid;
    }

    public Long getDatasetId() {
        return datasetId;
    }

    public String getFloorUUID() {
        return floorUUID;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public List<ContextEntry> getContextEntries() {
        return contextEntries;
    }

    public List<RadioDataEntry> getRadioDataEntries() {
        return radioDataEntries;
    }
}