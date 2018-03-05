package org.inspirecenter.indoorpositioningsystem;

/**
 * @author npaspallis
 * Created 24/09/2014.
 */
public class StandardDeviationResult
{
    private double mean;
    private double variance;

    public StandardDeviationResult(final double mean, final double variance)
    {
        this.mean = mean;
        this.variance = variance;
    }

    public double getMean()
    {
        return mean;
    }

    public double getVariance()
    {
        return variance;
    }

    @Override
    public String toString()
    {
        return String.format("Mean: %10.4f, Variance: %10.4f", mean, variance);
    }
}