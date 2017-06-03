package com.shashov.cluster.math.algs;

import com.shashov.cluster.math.config.Config;
import com.shashov.cluster.math.model.Bits;
import com.shashov.cluster.math.model.Conformation;
import com.shashov.cluster.math.utils.InfSupFinder;
import com.shashov.cluster.math.utils.MinsRepository;

import java.math.BigDecimal;

/**
 * Created by envoy on 17.04.2017.
 */
public class Efficiency {
    private MinsRepository rep;
    private Bits x;
    private Bits xInf;
    private Bits xSup;
    private double z;
    private Config config;

    public Efficiency(MinsRepository repository, Bits x, Config config) {
        this.rep = repository;
        this.x = x;
        this.config = config;
        updateData();
    }

    private void updateData() {
        int N = config.getStronginN();
        int M = config.getStronginM();
        int K = config.getStronginParams().getK();

        StringBuilder[] res = InfSupFinder.findInfSup(x.getBites().toString(), N, M);
        xSup = new Bits(res[0]);
        xInf = new Bits(res[1]);

        int k = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < M; i++) {
            if ((k < K) && (xInf.get(i) == xSup.get(i)) && (xInf.get(i) == '1')) {
                k++;
                sb.append('1');
            } else {
                sb.append('0');
            }
        }
        Conformation conf;
        if (k == K) {
            conf = findBestConf(new Bits(sb), config.getGrowAlgParams().getIterations());
            z = conf.getEnergy();
        } else {
            Conformation confInf = findBestConf(getFirstAtoms(xInf, K), config.getGrowAlgParams().getIterations());
            Conformation confSup = findBestConf(getFirstAtoms(xSup, K), config.getGrowAlgParams().getIterations());
            conf = (confInf.getEnergy() < confSup.getEnergy()) ? confInf : confSup;
            BigDecimal t = new BigDecimal(confSup.getEnergy() - confInf.getEnergy()).setScale(config.getDecimalScale(), BigDecimal.ROUND_HALF_UP);
            z = confInf.getEnergy() + t.multiply(new BigDecimal(x.getNumber().subtract(xInf.getNumber()).divide(xSup.getNumber().subtract(xInf.getNumber()))).setScale(config.getDecimalScale())).doubleValue(); //TODO not log?
        }
        rep.tryAddConf(conf);
    }

    //call GrowthAlg or use cache
    private Conformation findBestConf(Bits bits, int iterations) {
        String key = bits.getBites().toString();
        if (!rep.getCache().containsKey(key)) {
            rep.getCache().put(key, GrowthAlg.buildBestConf(bits, config.getStronginN(), iterations, config.getGrowAlgParams().getEnergyDelta(), config.getMathAdapter()));
        }

        return rep.getCache().get(key);
    }

    //get subBits with first k ones
    private Bits getFirstAtoms(Bits bits, int K) {
        StringBuilder sb = new StringBuilder();
        int k = 0;
        for (int i = 0; i < bits.getSize(); i++) {
            sb.append(k < K ? bits.get(i) : '0');
            if (bits.get(i) == '1') {
                k++;
            }
        }
        return new Bits(sb);
    }

    public Bits getX() {
        return x;
    }

    public Bits getXInf() {
        return xInf;
    }

    public Bits getXSup() {
        return xSup;
    }

    public double getZ() {
        return z;
    }

}
