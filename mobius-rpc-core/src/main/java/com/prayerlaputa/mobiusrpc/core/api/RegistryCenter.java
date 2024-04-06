package com.prayerlaputa.mobiusrpc.core.api;

import com.prayerlaputa.mobiusrpc.core.registry.ChangedListener;

import java.util.List;

/**
 * @author:
 * @description:
 * @date: 2024/4/5 16:57
 * @version: 1.0
 */
public interface RegistryCenter {

    void start(); // p/c
    void stop(); // p/c

    // provider侧
    void register(String service, String instance); // p
    void unregister(String service, String instance); // p

    // consumer侧
    List<String> fetchAll(String service); // c
    void subscribe(String service, ChangedListener listener);
    // void subscribe(); // c
    // void heartbeat();

    class StaticRegistryCenter implements RegistryCenter {

        List<String> providers;
        public StaticRegistryCenter(List<String> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(String service, String instance) {

        }

        @Override
        public void unregister(String service, String instance) {

        }

        @Override
        public List<String> fetchAll(String service) {
            return providers;
        }

        @Override
        public void subscribe(String service, ChangedListener listener) {

        }
    }

}
