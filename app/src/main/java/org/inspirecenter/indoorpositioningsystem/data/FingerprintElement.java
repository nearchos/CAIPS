package org.inspirecenter.indoorpositioningsystem.data;

import java.util.Locale;

/**
 * @author Nearchos
 *         Created: 25-Apr-16
 */
public class FingerprintElement {

    private String ssid;
    private int levelAsDecibel;
    private int frequency;

    public FingerprintElement(String ssid, int levelAsDecibel, int frequency) {
        this.ssid = ssid;
        this.levelAsDecibel = levelAsDecibel;
        this.frequency = frequency;
    }

    public String getSsid() {
        return ssid;
    }

    public int getLevelAsDecibel() {
        return levelAsDecibel;
    }

    public double getLevelAsRatio() {
        return Math.pow(10, levelAsDecibel /10d);
    }

    public int getFrequency() {
        return frequency;
    }

    public String toJsonObject() {
        return "{ \"ssid\": \"" + ssid + "\", \"level\": " + levelAsDecibel + ", \"frequency\": " + frequency + " }";
    }

    public static FingerprintElement fromJsonObject(final String jsonObject) {
        throw new RuntimeException("NoSuchMethodException"); // todo
    }

    public String getDetails() {
        return levelAsDecibel + "dB (" + String.format(Locale.US, "%.3e", getLevelAsRatio())+ ") at " + frequency + "Mhz";
    }

    @Override
    public String toString() {
        return "FingerprintElement{" +
                "ssid='" + ssid + '\'' +
                ", levelAsDecibel=" + levelAsDecibel +
                ", frequency=" + frequency +
                '}';
    }
}