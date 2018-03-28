package org.inspirecenter.indoorpositioningsystem.model;

public final class RadioDataEntry {

    private final String macAddress;
    private final int ssid; // stored in decibel

    public RadioDataEntry(final String macAddress, final int ssid) {
        this.macAddress = macAddress;
        this.ssid = ssid;
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
}