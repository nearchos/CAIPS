package org.inspirecenter.indoorpositioningsystem.model;

import java.util.List;

/**
 * Created by Nearchos Paspallis on 08/07/2014.
 *
 */
public class Dataset {

    private String status;
    private String locationUUID;
    private List<MeasurementEntry> measurements;

    public String getStatus() {
        return status;
    }

    public String getLocationUUID()
    {
        return locationUUID;
    }

    public List<MeasurementEntry> getMeasurements()
    {
        return measurements;
    }

    @Override
    public String toString() {
        return String.format("status:%s, locationUUID:%s, numOfTrainings:%d", status, locationUUID, measurements.size());
    }
}