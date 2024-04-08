package com.prayerlaputa.mobiusrpc.core.api;

import com.prayerlaputa.mobiusrpc.core.meta.InstanceMeta;
import lombok.Data;

import java.util.List;

@Data
public class RpcContext {

    List<Filter> filters;

    Router<InstanceMeta> router;

    LoadBalancer<InstanceMeta> loadBalancer;
}
