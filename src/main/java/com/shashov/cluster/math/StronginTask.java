package com.shashov.cluster.math;

import com.shashov.cluster.math.algs.Strongin;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Created by envoy on 02.05.2017.
 */
public class StronginTask extends FutureTask<Strongin> {
    private ProgressCallBack progressCallBack;

    public StronginTask(Strongin strongin, ProgressCallBack progressCallBack) {
        super(new Callable<Strongin>() {
            @Override
            public Strongin call() throws Exception {
                progressCallBack.onProgress(0);
                strongin.solve(progressCallBack);
                return strongin;
            }
        });

        this.progressCallBack = progressCallBack;
    }

    public ProgressCallBack getProgressCallBack() {
        return progressCallBack;
    }

    public static class ProgressCallBack {
        private int id;
        private Progress progress;

        public ProgressCallBack(int id, Progress progress) {
            this.id = id;
            this.progress = progress;
        }

        public void onProgress(int percent) {
            progress.onProgress(id, percent);
        }
    }

    @FunctionalInterface
    public interface Progress {
        void onProgress(int threadId, int percent);
    }
}
