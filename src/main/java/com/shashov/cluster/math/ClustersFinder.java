package com.shashov.cluster.math;

import com.shashov.cluster.math.algs.Strongin;
import com.shashov.cluster.math.config.Config;
import com.shashov.cluster.math.model.Bits;
import com.shashov.cluster.math.model.Conformation;
import com.shashov.cluster.math.model.Interval;
import javafx.util.Pair;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * Created by envoy on 05.03.2017.
 */
public class ClustersFinder {
    private Config config;

    public void process(Config config, StronginTask.Progress progress, Finish finish) throws ExecutionException, InterruptedException {
        this.config = config;
        List<List<Interval>> intervals = new ArrayList<>();
        Map<String, Conformation> output = new HashMap<>();
        if (config.getM() - config.getStronginM() == config.getTaskParams().getN()) {
            output.put(config.getTaskParams().getStartConf(), config.getMathAdapter().getEnergy(null, true));
            finish.onFinish(saveResults(output, progress), intervals);
            return;
        }
        //interval
        StringBuilder stronginOnes = new StringBuilder();
        for (int i = 0; i < config.getStronginN(); i++) {
            stronginOnes.append('1');
        }
        StringBuilder stronginZeros = new StringBuilder();
        for (int i = 0; i < config.getStronginM() - config.getStronginN(); i++) {
            stronginZeros.append('0');
        }
        Bits a = new Bits(new StringBuilder(stronginZeros).append(stronginOnes));
        Bits b = new Bits(new StringBuilder(stronginOnes).append(stronginZeros));

        java.util.concurrent.ExecutorService executor = Executors.newFixedThreadPool(config.getTaskParams().getThreadsCount());
        List<StronginTask> tasks = new ArrayList<>(config.getTaskParams().getThreadsCount());
        List<BigInteger> points = new ArrayList<>(config.getTaskParams().getThreadsCount() + 1);
        points.add(a.getNumber());
        for (int i = 0; i < config.getTaskParams().getThreadsCount() - 1; i++) {
            points.add(b.getNumber().subtract(a.getNumber()).divide(BigInteger.valueOf(config.getTaskParams().getThreadsCount())).multiply(BigInteger.valueOf(i + 1)).add(a.getNumber()));
        }
        points.add(b.getNumber());
        for (int i = 0; i < config.getTaskParams().getThreadsCount(); i++) {
            Strongin strongin = new Strongin(new Bits(config.getStronginM(), points.get(i)), new Bits(config.getStronginM(), points.get(i + 1)), config);
            StronginTask task = new StronginTask(strongin, new StronginTask.ProgressCallBack(i + 1, progress));
            tasks.add(task);
            executor.execute(task);
        }

        for (StronginTask task : tasks) {
            Strongin strongin = task.get();
            intervals.add(strongin.getIntervals());
            String key;
            for (Conformation variant : strongin.getRep().getMins()) {
                key = variant.getBits();
                if (!output.containsKey(key)) {
                    output.put(key, variant);
                } else {
                    if (variant.getEnergy() < output.get(key).getEnergy()) {
                        output.put(key, variant);
                    }
                }
            }
            task.getProgressCallBack().onProgress(100);
        }

        executor.shutdown();
        finish.onFinish(saveResults(output, progress), intervals);
    }

    private List<Conformation> saveResults(Map<String, Conformation> map, StronginTask.Progress progress) {
        List<Conformation> output = new ArrayList<>();
        progress.onProgress(0, 0);
        double percentDelta = 98.0 / map.size();
        int i = 1;
        for (Conformation conformation : map.values()) {
            progress.onProgress(0, (int) ((i++) * percentDelta));
            output.add(config.getMathAdapter().calcE(new Pair<>(conformation.getBits(), conformation.getVertices()), true));
        }

        output.sort((Conformation left, Conformation right) -> {
                    if (left.getEnergy() == right.getEnergy()) {
                        return 0;
                    }
                    return left.getEnergy() > right.getEnergy() ? 1 : -1;
                }
        );
        progress.onProgress(0, 100);
        return output.subList(0, Math.min(output.size(), config.getTaskParams().getMinsCount()));
    }

    public Config getConfig() {
        return config;
    }

    @FunctionalInterface
    public interface Finish {
        void onFinish(List<Conformation> results, List<List<Interval>> intervals);
    }
}
