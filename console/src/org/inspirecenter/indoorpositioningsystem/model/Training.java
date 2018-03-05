package org.inspirecenter.indoorpositioningsystem.model;

import java.util.List;
import java.util.Map;

/**
 * Created by Nearchos Paspallis on 08/07/2014.
 *
 */
public class Training {

    private String uuid;
    private String createdBy;
    private long timestamp;
    private String floorUUID;
    private double lat;
    private double lng;
    private Map<String,Object> context;
    private List<Measurement> measurements;

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

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    @Override
    public String toString() {
        return String.format("uuid: %s\nfloorUUID: %s\ncreatedBy: %s\ntimestamp: %d\ncoordinates: %f,%f\ncontext: %s\nnumOfMeasurements: %d\n\n",uuid, floorUUID, createdBy, timestamp, lat, lng, context.toString(), measurements.size());
    }
}