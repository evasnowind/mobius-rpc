package com.prayerlaputa.mobiusrpccore.cluster;

import com.prayerlaputa.mobiusrpccore.api.LoadBalancer;

import java.util.List;
import java.util.Random;

/**
 * @author:
 * @description:
 * @date: 2024/4/5 16:03
 * @version: 1.0
 */
public class RandomLoadBalancer<T> implements LoadBalancer<T> {

    Random random = new Random();

    @Override
    public T choose(List<T> providers) {
        if (providers == null || providers.isEmpty()) {
            return null;
        }
        if (providers.size() == 1) {
            return providers.get(0);
        }
        return providers.get(random.nextInt(providers.size()));
    }
}
