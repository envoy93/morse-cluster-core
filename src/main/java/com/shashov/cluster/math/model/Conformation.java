package com.shashov.cluster.math.model;

import java.util.List;

/**
 * Created by envoy on 17.04.2017.
 */
public class Conformation {
    private String bits;
    private double energy;
    private List<Vertex> vertices;

    public Conformation(String bits, List<Vertex> vertices, double energy) {
        this.bits = bits;
        this.vertices = vertices;
        this.energy = energy;
    }

    public String getBits() {
        return bits;
    }

    public double getEnergy() {
        return energy;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(bits).append(System.lineSeparator()).append("E = ").append(getEnergy()).append(System.lineSeparator());
        for (Vertex vertex : vertices) {
            sb.append(vertex.getX()).append(" ").append(vertex.getY()).append(" ").append(vertex.getZ()).append(System.lineSeparator());
        }
        return sb.toString().replace(".", ",");
    }
}
