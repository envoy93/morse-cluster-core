package com.shashov.cluster.math;

import com.shashov.cluster.math.model.Conformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by envoy on 16.04.2017.
 */
public class MinsRepository {
    private int size;
    private List<Conformation> mins;
    private Map<String, Conformation> cache;

    public MinsRepository(int size) {
        this.mins = new ArrayList<>();
        this.size = size;
        cache = new HashMap<>();
    }

    public boolean tryAddConf(Conformation conformation) {
        //TODO
        for (Conformation conf : mins) {
            if (conf.getBits().equals(conformation.getBits())) {
                return false;
            }
        }

        if (mins.size() < size) {
            mins.add(conformation);
            return true;
        }

        for (int i = 0; i < mins.size(); i++) {
            if (conformation.getEnergy() < mins.get(i).getEnergy()) {
//                    (conformation.getBits().getNumber().subtract(mins.get(i).getBits().getNumber()).abs().compareTo(
//                    new BigInteger(String.valueOf(Configuration.get().getROU_LO()))) < 0) &&

                mins.set(i, conformation);
                return true;
            }
        }

        int maxEnergyIndex = 0;
        double maxEnergy = mins.get(maxEnergyIndex).getEnergy();
        for (int i = 0; i < mins.size(); i++) {
            if (mins.get(i).getEnergy() >= maxEnergy) {
                maxEnergy = mins.get(i).getEnergy();
                maxEnergyIndex = i;
            }
        }

        if (conformation.getEnergy() < maxEnergy) {
            mins.set(maxEnergyIndex, conformation);
            return true;
        }

        return false;
    }

    public List<Conformation> getMins() {
        return mins;
    }

    public Map<String, Conformation> getCache() {
        return cache;
    }
}
