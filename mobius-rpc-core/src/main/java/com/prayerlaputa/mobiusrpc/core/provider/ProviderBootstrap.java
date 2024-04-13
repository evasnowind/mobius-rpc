package com.prayerlaputa.mobiusrpc.core.provider;

import com.prayerlaputa.mobiusrpc.core.api.RegistryCenter;
import com.prayerlaputa.mobiusrpc.core.annotation.MobiusProvider;
import com.prayerlaputa.mobiusrpc.core.meta.InstanceMeta;
import com.prayerlaputa.mobiusrpc.core.meta.ProviderMeta;
import com.prayerlaputa.mobiusrpc.core.meta.ServiceMeta;
import com.prayerlaputa.mobiusrpc.core.util.MethodUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Map;

/**
 * @Description:
 * @author: yuchenglong05
 * @create: 2024-03-09 22:48:10
 */
@Slf4j
@Data
public class ProviderBootstrap implements ApplicationContextAware {


    ApplicationContext applicationContext;

    RegistryCenter rc;

    private MultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();
    private InstanceMeta instance;

    @Value("${server.port}")
    private String port;

    @Value("${app.id}")
    private String app;

    @Value("${app.namespace}")
    private String namespace;

    @Value("${app.env}")
    private String env;

    @SneakyThrows
    @PostConstruct  // init-method
    public void init() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(MobiusProvider.class);
        rc = applicationContext.getBean(RegistryCenter.class);
        providers.keySet().forEach(System.out::println);
        providers.values().forEach(this::genInterface);
    }

    @SneakyThrows
    public void start() {
        rc.start();
        String ip = InetAddress.getLocalHost().getHostAddress();
        instance = InstanceMeta.http(ip, Integer.valueOf(port));
        skeleton.keySet().forEach(this::registerService);
    }

    @PreDestroy
    public void stop() {
        skeleton.keySet().forEach(this::unregisterService);
        rc.stop();
    }

    private void createProvider(Class<?> service, Object impl, Method method) {
        ProviderMeta providerMeta = ProviderMeta.builder().method(method)
                .serviceImpl(impl).methodSign(MethodUtils.methodSign(method)).build();
        log.info(" create a provider: " + providerMeta);
        skeleton.add(service.getCanonicalName(), providerMeta);
    }

    private void registerService(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(app)
                .namespace(namespace)
                .env(env)
                .name(service)
                .build();
        rc.register(serviceMeta, instance);
    }

    private void unregisterService(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(app)
                .namespace(namespace)
                .env(env)
                .name(service)
                .build();
        rc.unregister(serviceMeta, instance);
    }

    private void genInterface(Object impl) {
        Arrays.stream(impl.getClass().getInterfaces()).forEach(
                service -> {
                    Method[] methods = service.getMethods();
                    for (Method method : methods) {
                        if (MethodUtils.checkLocalMethod(method)) {
                            continue;
                        }
                        createProvider(service, impl, method);
                    }
                });
    }
}
