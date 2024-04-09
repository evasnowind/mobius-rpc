package com.prayerlaputa.mobiusrpc.core.meta;

import lombok.Builder;
import lombok.Data;

/**
 * @author:
 * @description:
 * @date: 2024/4/8 23:21
 * @version: 1.0
 */
@Data
@Builder
public class ServiceMeta {

    private String app;
    private String namespace;
    private String env;
    private String name;

    public String toPath() {
        return String.format("%s_%s_%s_%s", app, namespace, env, name);
    }
}

