package org.inspirecenter.indoorpositioningsystem.model;

import java.io.Serializable;

public final class Coordinates implements Serializable {

    private double lat;
    private double lng;

    public Coordinates() { /* empty constructor needed by Objectify */ }

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

    /**
     * Simply estimates the distance between this and the 'other' coordinates. Delegates the call to
     * {@link #estimateDistance(double, double, double, double)}.
     * @param otherCoordinates the coordinates to compute the distance to
     * @return the distance between this and the 'other' coordinates, in meters
     */
    public double distanceTo(final Coordinates otherCoordinates) {
        return 1000d * estimateDistance(lat, lng, otherCoordinates.lat, otherCoordinates.lng);
    }

    /**
     * Simply estimates the distance between two points (lat1,lng1) and (lat2,lng2).
     * Algorithm copied from: http://www.movable-type.co.uk/scripts/latlong.html
     * @param lat1 the latitude of point 1
     * @param lng1 the longitude of point 1
     * @param lat2 the latitude of point 2
     * @param lng2 the longitude of point 2
     * @return the distance between the two points, in kilometers
     */
    public static double estimateDistance(double lat1, double lng1, double lat2, double lng2) {
        double R = 6371d; // earth diameter in Km
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double deltaPhi = Math.toRadians(lat2-lat1);
        double deltaLambda = Math.toRadians(lng2-lng1);

        double a = Math.sin(deltaPhi/2) * Math.sin(deltaPhi/2) + Math.cos(phi1) * Math.cos(phi2) *
                Math.sin(deltaLambda/2) * Math.sin(deltaLambda/2);
        double  c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return R * c;
    }}