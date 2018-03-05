package org.inspirecenter.indoorpositioningsystem.model;

/**
 * Created by Nearchos Paspallis on 08/07/2014.
 *
 */
public class Measurement {

    private String macAddress;
    private int ssid; // stored in decibel

    public String getMacAddress() {
        return macAddress;
    }

    public double getLevelAsRatio() {
        return Math.pow(10, ssid /10d);
    }

    public int getLevelAsDecibel() {
        return ssid;
    }
}