package com.shashov.cluster.math.algs;

import com.shashov.cluster.math.StronginTask;
import com.shashov.cluster.math.config.Config;
import com.shashov.cluster.math.model.Bits;
import com.shashov.cluster.math.model.Interval;
import com.shashov.cluster.math.utils.MinsRepository;
import javafx.util.Pair;
import org.nevec.rjm.BigDecimalMath;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by envoy on 15.04.2017.
 */
public class Strongin {
    private List<Interval> intervals;
    private MinsRepository rep;
    private Bits b;
    private Bits a;
    private StronginTask.ProgressCallBack progressCallBack;
    private Config config;

    public Strongin(Bits a, Bits b, Config config) {
        this.config = config;
        this.a = a;
        this.b = b;
    }

    public MinsRepository solve(StronginTask.ProgressCallBack progressCallBack) {
        if ((a == null) || (b == null)) {
            throw new IllegalArgumentException("Strongin params not exist");
        }

        this.progressCallBack = progressCallBack;
        return solve(a, b);
    }


    private MinsRepository solve(Bits a, Bits b) {
        rep = new MinsRepository(config.getStronginParams().getRepositorySize());
        intervals = new ArrayList<>(config.getStronginParams().getIterations());
        Interval ab = new Interval(rep, a, b, config);
        intervals.add(ab);

        if (a.getNumber().compareTo(b.getNumber()) == 0) {
            rep.tryAddConf(config.getMathAdapter().getEnergy(a.getBites().toString(), true));
            return rep;
        }

        int ind = 0;
        Interval interval;
        Efficiency zX;
        Bits bitsX;
        boolean isUsed;
        Bits cacheB;
        Efficiency cacheZB;
        int progressDelta = 2 * config.getStronginParams().getIterations() / 100;
        if (progressDelta <= 0) {
            progressDelta = 2;
        }
        for (int i = 0; i < config.getStronginParams().getIterations(); i++) {
            if (i % progressDelta == 0) {
                if (progressCallBack != null) {
                    progressCallBack.onProgress(Math.min(i * 100 / config.getStronginParams().getIterations(), 99));
                }
            }

            interval = intervals.get(ind);
            isUsed = false;

            double logSize = BigDecimalMath.log(new BigDecimal(config.getStronginEps()).setScale(config.getDecimalScale()).divide(new BigDecimal(interval.getA().getNumber()), RoundingMode.HALF_UP).add(BigDecimal.ONE)).divide(config.getLog2(), RoundingMode.HALF_UP).doubleValue();
            if (interval.getLogSize() / 2.0 < logSize) { //todo check
                intervals.remove(ind);
            } else {
                Pair<Bits, Efficiency> middle = interval.getMiddle();
                bitsX = middle.getKey();
                zX = middle.getValue();

                cacheB = interval.getB();
                cacheZB = interval.getZB();
                if (!(zX.getXInf().getNumber().compareTo(interval.getA().getNumber()) < 0) && !(zX.getXInf().getNumber().compareTo(interval.getB().getNumber()) > 0)) {
                    interval.setB(bitsX, zX);
                    isUsed = true;
                }

                if (!(zX.getXSup().getNumber().compareTo(bitsX.getNumber()) < 0) && !(zX.getXSup().getNumber().compareTo(cacheB.getNumber()) > 0)) {
                    if (isUsed) {
                        intervals.add(new Interval(rep, bitsX, cacheB, zX, cacheZB, config));
                    } else {
                        interval.setA(bitsX, zX);
                        interval.setA(bitsX, zX);
                        isUsed = true;
                    }
                }

                if (!isUsed) {
                    intervals.remove(ind);
                }
            }

            if (intervals.size() == 0) {
                break;
            }

            ind = 0;
            interval = intervals.get(ind);
            for (int j = 0; j < intervals.size(); j++) {
                if (intervals.get(j).getF() > interval.getF()) {
                    ind = j;
                }
            }
        }

        getIntervals().sort((Interval left, Interval right) -> {
            return left.getA().getNumber().compareTo(right.getA().getNumber());
        });

        return rep;
    }

    public List<Interval> getIntervals() {
        return intervals;
    }

    public MinsRepository getRep() {
        return rep;
    }
}
