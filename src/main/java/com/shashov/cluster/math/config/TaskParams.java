package com.shashov.cluster.math.config;

import com.shashov.cluster.math.model.Vertex;

import java.util.List;

/**
 * Created by envoy on 03.06.2017.
 */
public class TaskParams {
    private int n;  //required
    private int threadsCount = 2;
    private int minsCount = 30;
    private double ro = 14;
    private List<Vertex> vertices;
    private String startConf;  //or calculated

    public int getN() {
        return n;
    }

    public int getThreadsCount() {
        return threadsCount;
    }

    public int getMinsCount() {
        return minsCount;
    }

    public double getRo() {
        return ro;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public String getStartConf() {
        return startConf;
    }

    private TaskParams(int n, int threadsCount, int minsCount, double ro, List<Vertex> vertices, String startConf) {
        this.n = n;
        this.threadsCount = threadsCount;
        this.minsCount = minsCount;
        this.ro = ro;
        this.vertices = vertices;
        this.startConf = startConf;
    }

    public static class Builder {
        private int n;
        private int threadsCount = 2;
        private int minsCount = 30;
        private double ro = 14;
        private List<Vertex> vertices;
        private String startConf;

        public Builder setN(int n) {
            this.n = n;
            return this;
        }

        public Builder setThreadsCount(int threadsCount) {
            this.threadsCount = threadsCount;
            return this;
        }

        public Builder setMinsCount(int minsCount) {
            this.minsCount = minsCount;
            return this;
        }

        public Builder setRo(double ro) {
            this.ro = ro;
            return this;
        }

        public Builder setVertices(List<Vertex> vertices) {
            this.vertices = vertices;
            return this;
        }

        public Builder setStartConf(String startConf) {
            this.startConf = startConf;
            return this;
        }

        public TaskParams build() {
            return new TaskParams(n, threadsCount, minsCount, ro, vertices, startConf);
        }
    }
}
