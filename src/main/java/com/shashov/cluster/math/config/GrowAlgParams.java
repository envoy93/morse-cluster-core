package com.shashov.cluster.math.config;

/**
 * Created by envoy on 03.06.2017.
 */
public class GrowAlgParams {
    private double minDistance = 1.1;
    private double energyDelta = 0.05;
    private int iterations = 10;

    private GrowAlgParams(double minDistance, double energyDelta, int iterations) {
        this.minDistance = minDistance;
        this.energyDelta = energyDelta;
        this.iterations = iterations;
    }

    public double getMinDistance() {
        return minDistance;
    }

    public double getEnergyDelta() {
        return energyDelta;
    }

    public int getIterations() {
        return iterations;
    }

    public static class Builder {
        private double minDistance = 1.1;
        private double energyDelta = 0.05;
        private int iterations = 10;

        public Builder setMinDistance(double minDistance) {
            this.minDistance = minDistance;
            return this;
        }

        public Builder setEnergyDelta(double energyDelta) {
            this.energyDelta = energyDelta;
            return this;
        }

        public Builder setIterations(int iterations) {
            this.iterations = iterations;
            return this;
        }

        public GrowAlgParams build() {
            return new GrowAlgParams(minDistance, energyDelta, iterations);
        }
    }
}
