package org.inspirecenter.indoorpositioningsystem;

/**
 * @author npaspallis
 * Created on 24/09/2014.
 */
public class SimulationResult
{
    private double minDistance;
    private double maxDistance;
    private StandardDeviationResult standardDeviationResult;

    public SimulationResult(double minDistance, double maxDistance, StandardDeviationResult standardDeviationResult)
    {
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.standardDeviationResult = standardDeviationResult;
    }

    public double getMinDistance()
    {
        return minDistance;
    }

    public double getMaxDistance()
    {
        return maxDistance;
    }

    public StandardDeviationResult getStandardDeviationResult()
    {
        return standardDeviationResult;
    }

    @Override
    public String toString()
    {
        return String.format("Min: %10.4f, Max: %10.4f, Standard deviation: %s", minDistance, maxDistance, standardDeviationResult);
    }
}