package com.shashov.cluster.math.config;

import com.shashov.cluster.math.algs.ClusterMathAdapter;
import com.shashov.cluster.math.model.Bits;
import org.nevec.rjm.BigDecimalMath;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by envoy on 21.04.2017.
 */
public class Config {
    private int STRONGIN_N;  //only calculated
    private int STRONGIN_M;  //only calculated
    private int M;  //only calculated
    private double stronginEps = 20000;
    private int decimalScale = 30;
    private BigDecimal log2 = BigDecimalMath.log(new BigDecimal(2).setScale(decimalScale));

    private LOParams loParams;
    private GrowAlgParams growAlgParams;
    private StronginParams stronginParams;
    private TaskParams taskParams;
    private ClusterMathAdapter mathAdapter;

    public Config(TaskParams taskParams, StronginParams stronginParams, GrowAlgParams growAlgParams, LOParams loParams) {
        this(taskParams);
        setupStronginParams(stronginParams);
        setupGrowAlgParams(growAlgParams);
        setupLOParams(loParams);
    }

    public Config(TaskParams taskParams) {
        if ((taskParams.getVertices() == null) || (taskParams.getVertices().isEmpty())) {
            throw new IllegalArgumentException("Vertices list is empty");
        }

        if (taskParams.getRo() <= 0) {
            throw new IllegalArgumentException("Parameter RO is not set");
        }

        M = taskParams.getVertices().size();

        TaskParams.Builder params = new TaskParams.Builder()
                .setMinsCount(taskParams.getMinsCount() <= 0 ? 10 : taskParams.getMinsCount())
                .setThreadsCount((taskParams.getRo() <= 0) ? 1 : taskParams.getThreadsCount())
                .setVertices(new ArrayList<>(taskParams.getVertices()));
        String startConf;
        if ((taskParams.getStartConf() == null) || (taskParams.getStartConf().length() < M)) {
            startConf = new Bits(taskParams.getVertices().size()).getBites().toString();
        } else {
            startConf = taskParams.getStartConf();
        }

        //strongin
        int n2 = 0;
        params.setStartConf(startConf);
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < startConf.length(); i++) {
            if (startConf.charAt(i) == '0') {
                indexes.add(i);
            } else {
                n2++;
            }
        }
        STRONGIN_N = taskParams.getN() - n2;
        STRONGIN_M = indexes.size();

        if ((STRONGIN_N < 0) || (STRONGIN_N > STRONGIN_M)) {
            throw new IllegalArgumentException("Parameter N is incorrect");
        }

        this.taskParams = params.setN(taskParams.getN()).build();
        this.mathAdapter = new ClusterMathAdapter(this.taskParams.getStartConf(), this.taskParams.getVertices(), indexes, this);
    }


    public void setupStronginParams(StronginParams stronginParams) {
        this.stronginParams = new StronginParams.Builder()
                .setK((stronginParams.getK() <= 0 || stronginParams.getK() > STRONGIN_N) ? STRONGIN_N / 4 : stronginParams.getK())
                .setIterations(stronginParams.getIterations() <= 0 ? 100 * stronginParams.getK() + 10 * STRONGIN_M : stronginParams.getIterations())
                .setRepositorySize(stronginParams.getRepositorySize() <= 0 ? 1 : stronginParams.getRepositorySize())
                .build();
    }

    public void setupGrowAlgParams(GrowAlgParams growAlgParams) {
        this.growAlgParams = growAlgParams; //TODO check
    }

    public void setupLOParams(LOParams loParams) {
        this.loParams = loParams; //TODO check
    }

    public LOParams getLoParams() {
        return loParams;
    }

    public GrowAlgParams getGrowAlgParams() {
        return growAlgParams;
    }

    public StronginParams getStronginParams() {
        return stronginParams;
    }

    public TaskParams getTaskParams() {
        return taskParams;
    }

    public int getStronginN() {
        return STRONGIN_N;
    }

    public int getStronginM() {
        return STRONGIN_M;
    }

    public int getM() {
        return M;
    }

    public double getStronginEps() {
        return stronginEps;
    }

    public int getDecimalScale() {
        return decimalScale;
    }

    public void setDecimalScale(int decimalScale) {
        this.decimalScale = decimalScale;
        log2 = BigDecimalMath.log(new BigDecimal(2).setScale(decimalScale));
    }

    public BigDecimal getLog2() {
        return log2;
    }

    public void setStronginEps(double stronginEps) {
        this.stronginEps = stronginEps;
    }

    public ClusterMathAdapter getMathAdapter() {
        return mathAdapter;
    }
}
