package org.inspirecenter.indoorpositioningsystem.model;

import java.util.List;

/**
 * Created by Nearchos Paspallis on 08/07/2014.
 *
 */
public class Data {

    private String status;
    private String locationUUID;
    private List<Training> trainings;

    public String getStatus()
    {
        return status;
    }

    public String getLocationUUID()
    {
        return locationUUID;
    }

    public List<Training> getTrainings()
    {
        return trainings;
    }

    @Override
    public String toString() {
        return String.format("status:%s, locationUUID:%s, numOfTrainings:%d", status, locationUUID, trainings.size());
    }
}