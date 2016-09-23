package org.inspirecenter.indoorpositioningsystem.data;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author Nearchos Paspallis
 * Created on 16/06/2014.
 */
public class Floor implements Serializable {
    private final String uuid;
    private final String name;
    private final long seq;
    private final String locationUUID; // FK
    private final String imageUrl;
    private final double topLeftLat;
    private final double topLeftLng;
    private final double bottomRightLat;
    private final double bottomRightLng;

    public Floor(final String uuid, final String name, final long seq, final String locationUUID, final String imageUrl,
                 final double topLeftLat, final double topLeftLng, final double bottomRightLat, final double bottomRightLng)
    {
        this.uuid = uuid;
        this.name = name;
        this.seq = seq;
        this.locationUUID = locationUUID;
        this.imageUrl = imageUrl;
        this.topLeftLat = topLeftLat;
        this.topLeftLng = topLeftLng;
        this.bottomRightLat = bottomRightLat;
        this.bottomRightLng = bottomRightLng;
    }

    public String getUuid()
    {
        return uuid;
    }

    public String getName()
    {
        return name;
    }

    public long getSeq() { return seq; }

    public String getLocationUUID() { return locationUUID; }

    public String getImageUrl() { return imageUrl; }

    public double getTopLeftLat() { return topLeftLat; }

    public double getTopLeftLng() { return topLeftLng; }

    public double getBottomRightLat() { return bottomRightLat; }

    public double getBottomRightLng() { return bottomRightLng; }

    @Override
    public String toString() {
        return String.format(Locale.US, "[%2d] %s", seq, name);
    }

    public String toFullString() {
        return "Floor{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", seq=" + seq +
                ", locationUUID='" + locationUUID + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", topLeftLat=" + topLeftLat +
                ", topLeftLng=" + topLeftLng +
                ", bottomRightLat=" + bottomRightLat +
                ", bottomRightLng=" + bottomRightLng +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Floor floor = (Floor) o;

        return uuid.equals(floor.uuid);

    }

    @Override
    public int hashCode()
    {
        return uuid.hashCode();
    }
}