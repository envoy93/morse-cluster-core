package com.shashov.cluster.math.algs;

import com.shashov.cluster.math.config.Config;
import com.shashov.cluster.math.model.Conformation;
import com.shashov.cluster.math.model.Vertex;
import com.shashov.cluster.math.utils.ClusterMath;
import com.shashov.cluster.math.utils.Lbfgs;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by envoy on 16.04.2017.
 */
public class ClusterMathAdapter {
    private final Config config;
    private String startConf;
    private List<Integer> indexes;
    private List<Vertex> vertices;
    private Map<String, Conformation> optCache = new HashMap<>();

    public ClusterMathAdapter(String startConf, List<Vertex> vertices, List<Integer> indexes, Config config) {
        this.startConf = startConf;
        this.vertices = vertices;
        this.indexes = indexes;
        this.config = config;
        optCache.clear();
    }

    public Conformation getEnergy(String stronginBits, boolean isLocalOpt) {
        return calcE(getSelectedVertices(stronginBits), isLocalOpt);
    }

    double getEnergyAtomWithStartConf(String stronginBits, int atomIndex) {
        if (atomIndex >= stronginBits.length()) {
            throw new IllegalArgumentException("Invalid bits size");
        }

        return ClusterMath.getCalcEnergyAtom(config.getTaskParams().getRo(), vertices.get(indexes.get(atomIndex)), getSelectedVertices(stronginBits).getValue());
    }


    int getAdjacentCountWithStartConf(String stronginBits, int atomIndex) {
        if (atomIndex >= stronginBits.length()) {
            throw new IllegalArgumentException("Invalid bits size");
        }

        return ClusterMath.getAdjacentCount(config.getGrowAlgParams().getMinDistance(), vertices.get(indexes.get(atomIndex)), getSelectedVertices(stronginBits).getValue());
    }

    private Pair<String, List<Vertex>> getSelectedVertices(String stronginBits) {
        List<Vertex> vertices1 = new ArrayList<>();
        StringBuilder sb = new StringBuilder(startConf);
        if (stronginBits != null && !stronginBits.isEmpty()) {
            if (indexes.size() != stronginBits.length()) {
                throw new IllegalArgumentException("Invalid bits size");
            }

            for (int i = 0; i < stronginBits.length(); i++) {
                sb.setCharAt(indexes.get(i), stronginBits.charAt(i));
            }
        }

        for (int i = 0; i < vertices.size(); i++) {
            if (sb.charAt(i) == '1') {
                vertices1.add(new Vertex(vertices.get(i)));
            }
        }

        return new Pair<>(sb.toString(), vertices1);
    }

    public Conformation calcE(Pair<String, List<Vertex>> vertices, boolean isLocalOpt) {
        String key = vertices.getKey();
        if (optCache.containsKey(key)) {
            return optCache.get(key);
        }

        Conformation conf;
        if (isLocalOpt) {
            conf = Lbfgs.minimize(new Conformation(key, vertices.getValue(), 0), config.getTaskParams().getRo(), config.getLoParams().getEps(), config.getLoParams().getIterations());
        } else {
            double energy = ClusterMath.getEnergy(vertices.getValue(), config.getTaskParams().getRo());
            conf = new Conformation(key, vertices.getValue(), energy);
        }

        if (isLocalOpt && !optCache.containsKey(key)) {
            optCache.put(key, conf);
        }
        return conf;
    }
}
