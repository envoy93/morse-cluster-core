package com.shashov.cluster.math;

import com.shashov.cluster.math.config.Config;
import com.shashov.cluster.math.model.Bits;
import com.shashov.cluster.math.model.Conformation;
import com.shashov.cluster.math.utils.StronginTask;

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
public class ExecutorService {
    private Config config;

    public void process(Config config, StronginTask.ProgressCallBack progressCallBack, OnFinishCallBack finishCallback) throws ExecutionException, InterruptedException {
        this.config = config;
        Map<String, Conformation> output = new HashMap<>();
        long time = System.currentTimeMillis();
        if (config.getM() - config.getStronginM() == config.getTaskParams().getN()) {
            progressCallBack.onFinish(10);
            output.put(config.getTaskParams().getStartConf(), config.getMathAdapter().getEnergy(null, true));
            finishCallback.onFinish(saveResults(output, progressCallBack), System.currentTimeMillis() - time);
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
            StronginTask.ProgressCallBack progressCallBack1 = progressCallBack.clone();
            progressCallBack1.setId(i);
            StronginTask task = new StronginTask(strongin, progressCallBack1);
            tasks.add(task);
            executor.execute(task);
        }

        for (StronginTask task : tasks) {
            Strongin strongin = task.get();
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
        progressCallBack.onFinish(10);
        executor.shutdown();
        finishCallback.onFinish(saveResults(output, progressCallBack), System.currentTimeMillis() - time);
    }

    private List<Conformation> saveResults(Map<String, Conformation> map, StronginTask.ProgressCallBack progressCallBack) {
        List<Conformation> output = new ArrayList<>();

        for (Conformation conformation : map.values()) {
            output.add(config.getMathAdapter().getEnergy(conformation.getBits(), true));
        }
        progressCallBack.onFinish(50);
        output.sort((Conformation left, Conformation right) -> {
                    if (left.getEnergy() == right.getEnergy()) {
                        return 0;
                    }
                    return left.getEnergy() > right.getEnergy() ? 1 : -1;
                }
        );

        return output.subList(0, Math.min(output.size(), config.getTaskParams().getMinsCount()));
    }

    public Config getConfig() {
        return config;
    }

    public interface OnFinishCallBack {
        void onFinish(List<Conformation> results, long milliseconds);
    }
}
