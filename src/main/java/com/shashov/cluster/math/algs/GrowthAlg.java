package com.shashov.cluster.math.algs;

import com.shashov.cluster.math.model.Bits;
import com.shashov.cluster.math.model.Conformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by envoy on 18.04.2017.
 */
public class GrowthAlg {

    public static Conformation buildBestConf(Bits bits, int n, int iterations, double energyDelta, ClusterMathAdapter mathAdapter) {
        Conformation conf = null;

        Map<String, Conformation> results = new HashMap<>();
        buildConfRecursive(bits.getBites().toString(), n, iterations, energyDelta, mathAdapter, results);
        for (Conformation conformation : results.values()) {
            if ((conf == null) || (conf.getEnergy() > conformation.getEnergy())) {
                conf = conformation;
            }
        }
        return conf;
    }

    private static void buildConfRecursive(final String bits, final int n, int iterations, double energyDelta, ClusterMathAdapter mathAdapter, final Map<String, Conformation> results) {
        if (results.containsKey(bits)) {
            return;
        }

        int currSize = 0;
        for (int i = 0; i < bits.length(); i++) {
            if (bits.charAt(i) == '1') currSize++;
        }

        if (currSize == n) {
            results.put(bits, mathAdapter.getEnergy(bits, false));
            return;
        }

        List<String> temp = findBestAdjacentAtom(bits, energyDelta, mathAdapter);

        int itersMin = 1;
        if (iterations > 0) {
            itersMin = (temp.size() < iterations) ? temp.size() : iterations;
            iterations /= itersMin;
        }

        for (int i = 0; i < itersMin; i++) {
            buildConfRecursive(temp.get(i), n, iterations, energyDelta, mathAdapter, results);
        }
    }

    public static List<String> findBestAdjacentAtom(String startBits, double energyDelta, ClusterMathAdapter mathAdapter) {
        List<String> list = new ArrayList<>();

        Map<String, Integer> adjacentList = new HashMap<>();
        int adjacentMax = 0;
        StringBuilder sb = new StringBuilder(startBits);
        for (int i = 0; i < startBits.length(); i++) {
            if (startBits.charAt(i) == '0') {
                int temp = mathAdapter.getAdjacentCountWithStartConf(startBits, i);
                if (temp > adjacentMax) {
                    adjacentMax = temp;
                    adjacentList.clear();
                }
                if (temp == adjacentMax) {
                    sb.setCharAt(i, '1');
                    adjacentList.put(sb.toString(), i);
                    sb.setCharAt(i, '0');
                }
            }
        }

        Map<String, Double> conformations = new HashMap<>();
        double conf;
        double minEnergy = 0;
        for (Map.Entry<String, Integer> bits : adjacentList.entrySet()) {
            conf = mathAdapter.getEnergyAtomWithStartConf(startBits, bits.getValue());
            if (conf < minEnergy) {
                minEnergy = conf;
            }
            conformations.put(bits.getKey(), conf);
        }

        for (Map.Entry<String, Double> entry : conformations.entrySet()) {
            if (((Math.abs(entry.getValue() - minEnergy)) / minEnergy) < energyDelta) {
                list.add(entry.getKey());
            }
        }
        return list;
    }
}
