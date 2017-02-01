package org.inspirecenter.indoorpositioningsystem.ui;

import android.content.Context;

/**
 * @author Nearchos
 *         Created: 27-Dec-16
 */

public enum ContextType {

    INSTALLATION_UUID("device-installation-uuid"),
    DEVICE_MANUFACTURER("device-manufacturer"),
    DEVICE_MODEL("device-model"),
    BATTERY_LEVEL("battery-level"),
    CHARGING_STATE("charging-state"),
    CONNECTED_WIFI_MAC_ADDRESS("connected-wifi-mac-address"),
    CONNECTED_WIFI_SIGNAL_STRENGTH("connected-wifi-signal-strength"),
    USER_ACTIVITY("user-activity"),
    LUMINOSITY("luminosity"),
    AMBIENT_TEMPERATURE("ambient-temperature"),
    AMBIENT_AIR_PRESSURE("ambient-air-pressure"),
    RELATIVE_HUMIDITY("relative-humidity"),
    LOCATION("location"),
    ACCELERATION("acceleration"),
    MAGNETIC_FIELD("magnetic-field"),
    GRAVITY("gravity"),
    GYROSCOPE("gyroscope"),
    ROTATION_VECTOR("rotation-vector");

    private String name;

    ContextType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    @Override
    public String toString() {
        return name;
    }
}