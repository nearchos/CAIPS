package org.inspirecenter.indoorpositioningsystem;

import com.google.gson.Gson;
import org.inspirecenter.indoorpositioningsystem.algorithms.*;
import org.inspirecenter.indoorpositioningsystem.model.Coordinates;
import org.inspirecenter.indoorpositioningsystem.model.Data;
import org.inspirecenter.indoorpositioningsystem.model.Training;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * Simulates the WiFi-based indoors location algorithm.
 *
 * Created by Nearchos Paspallis on 08/07/2014.
 */
public class Simulate
{
    /*
     * Note:
     * Vertical distances
     * - basement - ground floor    3.30m
     * - ground floor - first floor 4.50m
     */
    public static final double TEST_SAMPLE_RATIO    = 0.1d; // 10% of the samples will be used for testing
    public static final String INPUT_FILE_NAME      = "data.txt";
    public static final int LOOPS = 100; // how many times to repeat the experiment
    public static final LocationEstimationAlgorithm[] algorithms = {
            new BruteForceLocationEstimationAlgorithm(),
            new BruteForceSameDeviceLocationEstimationAlgorithm(),
            new BruteForceSimilarBatteryLevelLocationEstimationAlgorithm(),
            new BruteForceTopMatchesOnlyLocationEstimationAlgorithm(),
            new BruteForceTopMatchesOnlyLocationEstimationAlgorithm(10),
            new BruteForceTopMatchesOnlyLocationEstimationAlgorithm(100),
            new TriangulationOfBestMatchesLocationEstimationAlgorithm(),
            new TriangulationOfBestMatchesLocationEstimationAlgorithm(10),
            new TriangulationOfBestMatchesLocationEstimationAlgorithm(100)
    };

    public static void main(String [] args) throws IOException {
        // define input file
        final File inputFile = new File(INPUT_FILE_NAME);
        if(!inputFile.exists()) {
            System.out.println("File does not exist: " + inputFile.getAbsolutePath());
            System.exit(-1);
        }

        // read data from file
        final Data data = new Gson().fromJson(new String(Files.readAllBytes(inputFile.toPath())), Data.class);
        System.out.println("Status: " + data.getStatus());
        final List<Training> trainings = data.getTrainings();
        System.out.println("Read " + trainings.size() + " trainings!");

        for(final LocationEstimationAlgorithm locationEstimationAlgorithm : algorithms) {
            System.out.printf("Running %-50.50s ...", locationEstimationAlgorithm.getName());
            final SimulationResult simulationResult = simulate(trainings, locationEstimationAlgorithm, LOOPS);
            System.out.println(" -> " + simulationResult);
        }

    }

    static private SimulationResult simulate(final List<Training> data, final LocationEstimationAlgorithm locationEstimationAlgorithm, final int loops)
    {
        double averageMinDistance = 0d;
        double averageMaxDistance = 0d;
        double averageMeanDistance = 0d;
        double averageVarianceDistance = 0d;
        for(int i = 0; i < loops; i++) {
            SimulationResult simulationResult = simulate(data, locationEstimationAlgorithm);
            averageMinDistance += simulationResult.getMinDistance();
            averageMaxDistance += simulationResult.getMaxDistance();
            averageMeanDistance += simulationResult.getStandardDeviationResult().getMean();
            averageVarianceDistance += simulationResult.getStandardDeviationResult().getVariance();
        }

        return new SimulationResult(averageMinDistance/loops, averageMaxDistance/loops, new StandardDeviationResult(averageMeanDistance/loops, averageVarianceDistance/loops));
    }

    static private SimulationResult simulate(final List<Training> data, final LocationEstimationAlgorithm locationEstimationAlgorithm)
    {
        // make a copy of the input data
        final List<Training> trainings = new ArrayList<Training>(data);

        // split the data to training/testing sets
        final Set<Training> testingSet = new HashSet<Training>();
        int testingSetSize = (int) (trainings.size() * TEST_SAMPLE_RATIO);
        for(int i = 0; i < testingSetSize; i++) {
            // select random element from trainings
            int randomIndex = new Random().nextInt(trainings.size());
            testingSet.add(trainings.remove(randomIndex));
        }
//        System.out.println("org.inspirecenter.indoorpositioningsystem.model.Training size: " + trainings.size() + ", Testing size: " + testingSet.size());

        // start the simulation
        double maxDistance = Double.MIN_VALUE;
        double minDistance = Double.MAX_VALUE;
        final Vector<Double> distances = new Vector<Double>();
        for(final Training testingTraining : testingSet) {
            final Coordinates estimatedCoordinates = locationEstimationAlgorithm.estimateLocation(trainings, testingTraining);
            final double distance = estimateDistance(testingTraining, estimatedCoordinates);
            if(maxDistance < distance) maxDistance = distance;
            if(minDistance > distance) minDistance = distance;
            distances.add(distance);
        }

        return new SimulationResult(minDistance, maxDistance, computeStandardDeviation(distances));
    }

    /**
     * Simply estimates the distance between two coordinates. Delegates the call to
     * {@link #estimateDistance(double, double, double, double)}.
     * @param training the actual training containing the 'true' coordinates
     * @param coordinates the estimated coordinates
     * @return the distance between the 'true' and the estimated coordinates, in meters
     */
    static private double estimateDistance(final Training training, final Coordinates coordinates) {
        double lat1 = training.getLat();
        double lng1 = training.getLng();
        double lat2 = coordinates.getLat();
        double lng2 = coordinates.getLng();

        return 1000d * estimateDistance(lat1, lng1, lat2, lng2);
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
    static private double estimateDistance(double lat1, double lng1, double lat2, double lng2) {
        double R = 6371d; // earth diameter in Km
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double deltaPhi = Math.toRadians(lat2-lat1);
        double deltaLambda = Math.toRadians(lng2-lng1);

        double a = Math.sin(deltaPhi/2) * Math.sin(deltaPhi/2) + Math.cos(phi1) * Math.cos(phi2) *
                        Math.sin(deltaLambda/2) * Math.sin(deltaLambda/2);
        double  c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return R * c;
    }

    static private StandardDeviationResult computeStandardDeviation(final Vector<Double> numbers) {
        if(numbers == null) throw new NullPointerException("Argument cannot be null");
        if(numbers.isEmpty()) throw new IllegalArgumentException("Input vector cannot be empty");

        double sum = 0d;
        for(final double n : numbers) {
            sum+=n;
        }
        double mean = sum/numbers.size();

        double sumOfDifferencesSquared = 0d;
        for(final double n: numbers) {
            sumOfDifferencesSquared+=Math.pow(n-mean,2d);
        }
        double variance = sumOfDifferencesSquared/numbers.size();

        return new StandardDeviationResult(mean, variance);
    }
}