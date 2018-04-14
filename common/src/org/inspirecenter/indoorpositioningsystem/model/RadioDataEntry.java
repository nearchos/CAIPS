package org.inspirecenter.indoorpositioningsystem.model;

import java.io.Serializable;

public final class RadioDataEntry implements Serializable {

    private String macAddress;
    private int ssid; // stored in decibel
    private int frequency; // stored in decibel

    public RadioDataEntry() { /* empty constructor needed by Objectify */ }

    public RadioDataEntry(final String macAddress, final int ssid, final int frequency) {
        this.macAddress = macAddress;
        this.ssid = ssid;
        this.frequency = frequency;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public double getLevelAsRatio() {
        return Math.pow(10, ssid /10d);
    }

    public int getLevelAsDecibel() {
        return ssid;
    }

    public int getFrequency() {
        return frequency;
    }
}