package com.shashov.cluster.math.config;

/**
 * Created by envoy on 03.06.2017.
 */
public class LOParams {
    private double eps;
    private int iterations;

    private LOParams(double eps, int iterations) {
        this.eps = eps;
        this.iterations = iterations;
    }

    public double getEps() {
        return eps;
    }

    public int getIterations() {
        return iterations;
    }

    public static class Builder {
        private double eps = 1e-8;
        private int iterations = 10000;

        public Builder setEps(double eps) {
            this.eps = eps;
            return this;
        }

        public Builder setIterations(int iterations) {
            this.iterations = iterations;
            return this;
        }

        public LOParams build() {
            return new LOParams(eps, iterations);
        }
    }
}
