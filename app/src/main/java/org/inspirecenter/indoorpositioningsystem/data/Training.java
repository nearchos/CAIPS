package org.inspirecenter.indoorpositioningsystem.data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Nearchos Paspallis
 * Created on 23/06/2014.
 */
public class Training implements Serializable
{
    private final String uuid;
    private final String createdBy;
    private final String locationUUID;
    private final String floorUUID;
    private final long timestamp;
    private final String radiomapAsJSON;
    private final String contextAsJSON;
    private final double lat; // actual position lat
    private final double lng; // actual position lng

    public Training(final String uuid, final String createdBy, final String locationUUID,
                    final String floorUUID, final long timestamp, final String radiomapAsJSON,
                    final String contextAsJSON, final double lat, final double lng) {
        this.uuid = uuid;
        this.createdBy = createdBy;
        this.locationUUID = locationUUID;
        this.floorUUID = floorUUID;
        this.timestamp = timestamp;
        this.radiomapAsJSON = radiomapAsJSON;
        this.contextAsJSON = contextAsJSON;
        this.lat = lat;
        this.lng = lng;
    }

    public String getUUID() { return uuid; }

    public String getCreatedBy() { return createdBy; }

    public String getLocationUuid() { return locationUUID; }

    public String getFloorUuid() { return floorUUID; }

    public long getTimestamp() { return timestamp; }

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US);

    public String getTimestampAsString() { return SIMPLE_DATE_FORMAT.format(new Date(timestamp)); }

    public String getRadiomapAsJSON() { return radiomapAsJSON; }

    public String getContextAsJSON() { return contextAsJSON; }

    public double getLat() { return lat; }

    public double getLng() { return lng; }

    public String getCoordinatesAsString() {
        return String.format(Locale.US, "%.6f, %.6f", lat, lng);
    }

    @Override public String toString()
    {
        return uuid.substring(uuid.length() - 8);
    }
}