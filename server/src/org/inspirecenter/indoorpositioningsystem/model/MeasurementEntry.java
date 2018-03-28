package org.inspirecenter.indoorpositioningsystem.model;

import java.util.List;
import java.util.Map;

public final class MeasurementEntry {

    private final String uuid;
    private final String floorUUID;
    private final String createdBy;
    private final long timestamp;
    private final Coordinates coordinates;
    private final Map<String,String> context;
    private final List<RadioDataEntry> radioDataEntries;

    public MeasurementEntry(final String uuid, final String floorUUID, final String createdBy, final long timestamp,
                            final Coordinates coordinates, final Map<String, String> context,
                            final List<RadioDataEntry> radioDataEntries) {
        this.uuid = uuid;
        this.floorUUID = floorUUID;
        this.createdBy = createdBy;
        this.timestamp = timestamp;
        this.coordinates = coordinates;
        this.context = context;
        this.radioDataEntries = radioDataEntries;
    }

    public String getUuid() {
        return uuid;
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

    public Map<String, String> getContext() {
        return context;
    }

    public List<RadioDataEntry> getRadioDataEntries() {
        return radioDataEntries;
    }
}