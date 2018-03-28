package org.inspirecenter.indoorpositioningsystem.model;

public final class Coordinates {

    private final double lat;
    private final double lng;

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
        return "(" + lat + "," + lng + ")";
    }
}