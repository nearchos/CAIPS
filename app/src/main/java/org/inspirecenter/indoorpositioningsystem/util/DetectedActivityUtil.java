package org.inspirecenter.indoorpositioningsystem.util;

import com.google.android.gms.location.DetectedActivity;

/**
 * @author Nearchos
 *         Created: 04-Jan-17
 */

public class DetectedActivityUtil {

    public static String getType(final DetectedActivity detectedActivity) {
        switch (detectedActivity.getType()) {
            case 0:
                return "IN_VEHICLE";
            case 1:
                return "ON_BICYCLE";
            case 2:
                return "ON_FOOT";
            case 3:
                return "STILL";
            case 4:
                return "UNKNOWN";
            case 5:
                return "TILTING";
            case 6:
            case 7:
                return "WALKING";
            case 8:
                return "RUNNING";
            default:
                return Integer.toString(detectedActivity.getType());
        }
    }

    public static String toString(final DetectedActivity detectedActivity) {
        return getType(detectedActivity) + " at " + detectedActivity.getConfidence() + "%";
    }
}