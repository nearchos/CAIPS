package org.inspirecenter.indoorpositioningsystem.model;

/**
 * Created by Nearchos Paspallis on 08/07/2014.
 *
 */
public class Coordinates {

    private double lat;
    private double lng;

    public Coordinates(final double lat, final double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    @Override
    public String toString() {
        return "( " + lat + ", " + lng + " )";
    }
}