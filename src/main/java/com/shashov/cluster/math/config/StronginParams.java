package com.shashov.cluster.math.config;

/**
 * Created by envoy on 03.06.2017.
 */
public class StronginParams {
    private int k;  //or calculated
    private int iterations; //or calculated
    private int repositorySize = 20;

    private StronginParams(int k, int iterations, int repositorySize) {
        this.k = k;
        this.iterations = iterations;
        this.repositorySize = repositorySize;
    }

    public int getK() {
        return k;
    }

    public int getIterations() {
        return iterations;
    }

    public int getRepositorySize() {
        return repositorySize;
    }

    public static class Builder {
        private int k;
        private int iterations = 100;
        private int repositorySize = 20;

        public Builder setK(int k) {
            this.k = k;
            return this;
        }

        public Builder setIterations(int iterations) {
            this.iterations = iterations;
            return this;
        }

        public Builder setRepositorySize(int repositorySize) {
            this.repositorySize = repositorySize;
            return this;
        }

        public StronginParams build() {
            return new StronginParams(k, iterations, repositorySize);
        }
    }
}
