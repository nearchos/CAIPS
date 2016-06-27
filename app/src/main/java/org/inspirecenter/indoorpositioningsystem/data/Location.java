package org.inspirecenter.indoorpositioningsystem.data;

import java.io.Serializable;

/**
 * @author Nearchos Paspallis
 * Created on 16/06/2014.
 */
public class Location implements Serializable
{
    private final String uuid;
    private final String name;
    private final String createdBy;
    private final long timestamp;

    public Location(final String uuid, final String name, final String createdBy, final long timestamp)
    {
        this.uuid = uuid;
        this.name = name;
        this.createdBy = createdBy;
        this.timestamp = timestamp;
    }

    public String getUuid()
    {
        return uuid;
    }

    public String getName()
    {
        return name;
    }

    public String getCreatedBy()
    {
        return createdBy;
    }

    public long getTimestamp()
    {
        return timestamp;
    }
}
