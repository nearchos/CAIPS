package org.inspirecenter.indoorpositioningsystem.simulation;

/**
 * @author Nearchos
 * Created: 13-Apr-18
 */
public enum Algorithm {

    BRUTE_FORCE("Brute Force Location Estimation", BruteForceLocationEstimationAlgorithm.class),
    BRUTE_FORCE_SAME_DEVICE_LOCATION("Brute Force Same-Device Location Estimation", BruteForceSameDeviceLocationEstimationAlgorithm.class),
    BRUTE_FORCE_SIMILAR_BATTERY("Brute Force Similar-Battery Location Estimation", BruteForceSimilarBatteryLevelLocationEstimationAlgorithm.class),
    BRUTE_FORCE_TOP_MATCHES_ONLY("Brute Force Top-Matches-Only Location Estimation", BruteForceTopMatchesOnlyLocationEstimationAlgorithm.class),
    TRIANGULATION_OF_BEST_MATCHES("Triangulation of Best-Matches Location Estimation", TriangulationOfBestMatchesLocationEstimationAlgorithm.class);

    private final String name;
    private final Class<? extends LocationEstimationAlgorithm> locationEstimationAlgorithm;

    Algorithm(final String name, final Class<? extends LocationEstimationAlgorithm> locationEstimationAlgorithm) {
        this.name = name;
        this.locationEstimationAlgorithm = locationEstimationAlgorithm;
    }

    public String getName() {
        return name;
    }

    public Class<? extends LocationEstimationAlgorithm> getLocationEstimationAlgorithm() {
        return locationEstimationAlgorithm;
    }

    @Override
    public String toString() {
        return name;
    }
}