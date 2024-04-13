package com.prayerlaputa.mobiusrpc.core.registry;

import com.prayerlaputa.mobiusrpc.core.api.RegistryCenter;
import com.prayerlaputa.mobiusrpc.core.meta.InstanceMeta;
import com.prayerlaputa.mobiusrpc.core.meta.ServiceMeta;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author:
 * @description: zk 注册中心
 * @date: 2024/4/5 17:33
 * @version: 1.0
 */
@Slf4j
public class ZkRegistryCenter  implements RegistryCenter {

    @Value("${mobius-rpc.zkServer}")
    String servers;

    @Value("${mobius-rpc.zkRoot}")
    String root;


    private CuratorFramework client = null;
    private TreeCache cache = null;

    @Override
    public void start() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(servers)
                .namespace(root)
                .retryPolicy(retryPolicy)
                .build();
        log.info(" ===> zk client starting to server[" + servers + "/" + root + "].");
        client.start();
    }

    @Override
    public void stop() {
        log.info(" ===> zk client stopped...");
        if (null != cache) {
            cache.close();
        }
        client.close();
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        String servicePath = "/" + service;
        try {
            // 创建服务的持久化节点
            if (client.checkExists().forPath(servicePath) == null) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath, "service".getBytes());
            }
            // 创建实例的临时性节点
            String instancePath = servicePath + "/" + instance.toPath();
            log.info(" ===> register to zk: " + instancePath);
            client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, "provider".getBytes());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        String servicePath = "/" + service;
        try {
            // 判断服务是否存在
            if (client.checkExists().forPath(servicePath) == null) {
                return;
            }
            // 删除实例节点
            String instancePath = servicePath + "/" + instance;
            log.info(" ===> unregister to zk: " + instancePath);
            client.delete().quietly().forPath(instancePath);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        String servicePath = "/" + service;
        try {
            // 获取所有子节点
            List<String> nodes = client.getChildren().forPath(servicePath);
            List<InstanceMeta> instances = mapInstance(nodes);
            log.info(" ===> fetchAll from zk: " + servicePath);
            instances.forEach(System.out::println);
            return instances;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @SneakyThrows
    @Override
    public void subscribe(ServiceMeta service, ChangedListener listener) {
        cache = TreeCache.newBuilder(client, "/"+service)
                .setCacheData(true).setMaxDepth(2).build();
        cache.getListenable().addListener(
                (curator, event) -> {
                    // 有任何节点变动这里会执行
                    log.info("zk subscribe event: " + event);
                    List<InstanceMeta> nodes = fetchAll(service);
                    listener.fire(new Event(nodes));
                }
        );
        cache.start();
    }

    private List<InstanceMeta> mapInstance(List<String> nodes) {
        return nodes.stream()
                .map(x -> {
                    String[] strs = x.split("_");
                    return InstanceMeta.http(strs[0], Integer.valueOf(strs[1]));
                }).collect(Collectors.toList());
    }
}
