package com.prayerlaputa.mobiusrpc.core.api;

import com.prayerlaputa.mobiusrpc.core.meta.InstanceMeta;
import com.prayerlaputa.mobiusrpc.core.meta.ServiceMeta;
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
    void register(ServiceMeta service, InstanceMeta instance); // p
    void unregister(ServiceMeta service, InstanceMeta instance); // p

    // consumer侧
    List<InstanceMeta> fetchAll(ServiceMeta service); // c
    void subscribe(ServiceMeta service, ChangedListener listener);
    // void subscribe(); // c
    // void heartbeat();

    class StaticRegistryCenter implements RegistryCenter {

        List<InstanceMeta> providers;
        public StaticRegistryCenter(List<InstanceMeta> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(ServiceMeta service, InstanceMeta instance) {

        }

        @Override
        public void unregister(ServiceMeta service, InstanceMeta instance) {

        }

        @Override
        public List<InstanceMeta> fetchAll(ServiceMeta service) {
            return providers;
        }

        @Override
        public void subscribe(ServiceMeta service, ChangedListener listener) {

        }
    }

}
