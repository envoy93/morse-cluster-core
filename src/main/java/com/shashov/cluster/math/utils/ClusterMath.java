package com.shashov.cluster.math.utils;

import com.shashov.cluster.math.model.Vertex;

import java.util.List;

/**
 * Created by envoy on 31.05.2017.
 */
public class ClusterMath {

    public static int getAdjacentCount(double minDistance, Vertex vertex, List<Vertex> vertices) {
        int count = 0;
        for (Vertex v : vertices) {
            if (vertex != v) {
                if (vertex.distanceTo(v) < minDistance) {
                    count++;
                }
            }
        }

        return count;
    }

    public static double getCalcEnergyAtom(double ro, Vertex vertex, List<Vertex> vertices) {
        double r;
        double energy = 0;
        for (Vertex v : vertices) {
            if (vertex != v) {
                r = vertex.distanceTo(v);
                energy += Math.exp(ro * (1 - r)) * (Math.exp(ro * (1 - r)) - 2);
            }
        }
        return energy;
    }


    public static double getEnergy(List<Vertex> vertices, double ro) {
        double r;
        double energy = 0;
        for (int i = 0; i < vertices.size() - 1; i++) {
            for (int j = i + 1; j < vertices.size(); j++) {
                r = vertices.get(i).distanceTo(vertices.get(j));
                energy += Math.exp(ro * (1 - r)) * (Math.exp(ro * (1 - r)) - 2);
            }
        }

        return energy;
    }
}
