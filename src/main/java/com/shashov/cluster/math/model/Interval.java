package com.shashov.cluster.math.model;

import com.shashov.cluster.math.algs.Efficiency;
import com.shashov.cluster.math.config.Config;
import com.shashov.cluster.math.utils.MinsRepository;
import javafx.util.Pair;
import org.nevec.rjm.BigDecimalMath;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Formatter;

/**
 * Created by envoy on 17.04.2017.
 */
public class Interval {
    private static final long m = 150;
    private Bits a;
    private Bits b;
    private double f;
    private Efficiency zA;
    private Efficiency zB;
    private MinsRepository rep;
    private Config config;
    private double logA;
    private double logB;

    public Interval(MinsRepository repository, Bits a, Bits b, Config config) {
        this(repository, a, b, new Efficiency(repository, a, config), new Efficiency(repository, b, config), config);
    }

    public Interval(MinsRepository repository, Bits a, Bits b, Efficiency zA, Efficiency zB, Config config) {
        this.rep = repository;
        this.config = config;
        setA(a, zA, false);
        setB(b, zB, true);
    }

    public Bits getA() {
        return a;
    }

    public Bits getB() {
        return b;
    }

    public void setA(Bits a, Efficiency zA) {
        setA(a, zA, true);
    }

    public void setB(Bits b, Efficiency zB) {
        setB(b, zB, true);
    }

    public void setA(Bits a, Efficiency zA, boolean isUpdateF) {
        this.a = a;
        this.zA = zA;
        logA = BigDecimalMath.log(new BigDecimal(a.getNumber()).setScale(config.getDecimalScale())).divide(config.getLog2(), RoundingMode.HALF_UP).doubleValue();
        if (isUpdateF) updateF();
    }

    public void setB(Bits b, Efficiency zB, boolean isUpdateF) {
        this.b = b;
        this.zB = zB;
        logB = BigDecimalMath.log(new BigDecimal(b.getNumber()).setScale(config.getDecimalScale())).divide(config.getLog2(), RoundingMode.HALF_UP).doubleValue();
        if (isUpdateF) updateF();
    }

    private void updateF() {
        f = calcF(a.getNumber(), b.getNumber(), zA.getZ(), zB.getZ());
    }

    public double calcF(BigInteger a, BigInteger b, double zA, double zB) {
        return m * (logB - logA) + (Math.pow(zB - zA, 2) / (m * (logB - logA))) - 2 * (zA + zB);
    }

    public double getF() {
        return f;
    }

    public Efficiency getZA() {
        return zA;
    }

    public Efficiency getZB() {
        return zB;
    }

    public double getLogSize() {
        return logB - logA;
    }

    public BigInteger getSize() {
        return b.getNumber().subtract(a.getNumber());
    }

    public Pair<Bits, Efficiency> getMiddle() {
        BigInteger x = new BigDecimal(getB().getNumber().add(getA().getNumber())).setScale(config.getDecimalScale()).divide(new BigDecimal(2)).setScale(0, BigDecimal.ROUND_HALF_UP).toBigInteger(); //Math.ceil(Math.pow(2, (logA + logB) / 2.0));
        Bits bitsX = new Bits(config.getStronginM(), x);
        return new Pair<>(bitsX, new Efficiency(rep, bitsX, config));
    }

    @Override
    public String toString() {
        Formatter formatter = new Formatter();
        formatter.format("[%22.15f; %22.15f] f = %22.15f | zA = %22.15f | zB = %22.15f", logA, logB, f, zA.getZ(), zB.getZ());
        return formatter.toString();
    }
}
