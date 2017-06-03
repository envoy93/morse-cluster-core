package com.shashov.cluster.math.utils;

import com.github.lbfgs4j.LbfgsMinimizer;
import com.github.lbfgs4j.liblbfgs.Function;
import com.github.lbfgs4j.liblbfgs.LbfgsConstant;
import com.shashov.cluster.math.model.Conformation;
import com.shashov.cluster.math.model.Vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by envoy on 28.04.2017.
 */
public class Lbfgs {

    public static Conformation minimize(Conformation conformation, double ro, double epsilon, int maxIterations) {
        List<Vertex> vertices = conformation.getVertices();
        double[] input = new double[vertices.size() * 3];
        int j = 0;
        for (int i = 0; i < vertices.size(); i++) {
            input[j++] = vertices.get(i).getX();
            input[j++] = vertices.get(i).getY();
            input[j++] = vertices.get(i).getZ();
        }

        LbfgsConstant.LBFGS_Param params = new LbfgsConstant.LBFGS_Param(com.github.lbfgs4j.liblbfgs.Lbfgs.defaultParams());
        params.epsilon = epsilon;
        params.max_iterations = maxIterations;

        MorseFunction morseFunction = new MorseFunction(input.length, ro);
        LbfgsMinimizer minimizer = new LbfgsMinimizer(params, false);
        double[] output = minimizer.minimize(morseFunction, input);
        double min = morseFunction.valueAt(output);

        List<Vertex> verticesOpt = arrayToCollection(output);

        return new Conformation(conformation.getBits(), verticesOpt, min);
    }

    private static List<Vertex> arrayToCollection(double[] x) {
        List<Vertex> vertices = new ArrayList<>(x.length / 3);
        int j = 0;
        for (int i = 0; i < x.length / 3; i++) {
            vertices.add(new Vertex(x[j++], x[j++], x[j++]));
        }

        return vertices;
    }

    public static class MorseFunction implements Function {
        private int size;
        private double ro;

        public MorseFunction(int size, double ro) {
            this.size = size;
            this.ro = ro;
        }

        @Override
        public int getDimension() {
            return size;
        }

        @Override
        public double valueAt(double[] x) {
            return ClusterMath.getEnergy(arrayToCollection(x), ro);
        }

        @Override
        public double[] gradientAt(double[] x) {
            double[] grad = new double[x.length];
            List<Vertex> vertices = arrayToCollection(x);
            for (int k = 0; k < vertices.size(); k++) {
                for (int i = 0; i < vertices.size(); i++) {
                    if (k != i) {
                        Vertex xk = new Vertex(vertices.get(k).getX(), vertices.get(k).getY(), vertices.get(k).getZ());
                        Vertex xi = new Vertex(vertices.get(i).getX(), vertices.get(i).getY(), vertices.get(i).getZ());
                        double rki = xk.distanceTo(xi);

                        double[] aki = new double[3];
                        aki[0] = (xk.getX() - xi.getX()) / rki;
                        aki[1] = (xk.getY() - xi.getY()) / rki;
                        aki[2] = (xk.getZ() - xi.getZ()) / rki;

                        double fki = 2 * ro * (Math.exp(ro * (1 - rki)) - Math.exp(2 * ro * (1 - rki)));
                        grad[3 * k + 0] += fki * aki[0];
                        grad[3 * k + 1] += fki * aki[1];
                        grad[3 * k + 2] += fki * aki[2];
                    }
                }
            }

            return grad;
        }
    }
}
