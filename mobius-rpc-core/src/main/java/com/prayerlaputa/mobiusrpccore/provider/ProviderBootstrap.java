package com.prayerlaputa.mobiusrpccore.provider;

import com.prayerlaputa.mobiusrpccore.annotation.MobiusProvider;
import com.prayerlaputa.mobiusrpccore.api.RegistryCenter;
import com.prayerlaputa.mobiusrpccore.meta.ProviderMeta;
import com.prayerlaputa.mobiusrpccore.util.MethodUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
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
@Data
public class ProviderBootstrap implements ApplicationContextAware {


    ApplicationContext applicationContext;

    RegistryCenter rc;

    private MultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();
    private String instance;

    @Value("${server.port}")
    private String port;

    @SneakyThrows
    @PostConstruct  // init-method
    public void init() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(MobiusProvider.class);
        rc = applicationContext.getBean(RegistryCenter.class);
        providers.forEach((x, y) -> System.out.println(x));
        providers.values().forEach(x -> genInterface(x));
    }

    @SneakyThrows
    public void start() {
        rc.start();
        String ip = InetAddress.getLocalHost().getHostAddress();
        instance = ip + "_" + port;
        skeleton.keySet().forEach(this::registerService);
    }

    @PreDestroy
    public void stop() {
        skeleton.keySet().forEach(this::unregisterService);
        rc.stop();
    }

    private void createProvider(Class<?> itfer, Object x, Method method) {
        ProviderMeta meta = new ProviderMeta();
        meta.setServiceImpl(x);
        meta.setMethod(method);
        meta.setMethodSign(MethodUtils.methodSign(method));
        System.out.println("ProviderMeta: " + meta);
        skeleton.add(itfer.getCanonicalName(), meta);
    }

    private void registerService(String service) {
        rc.register(service, instance);
    }

    private void unregisterService(String service) {
        rc.unregister(service, instance);
    }

    private void genInterface(Object x) {
        Arrays.stream(x.getClass().getInterfaces()).forEach(
                itfer -> {
                    Method[] methods = itfer.getMethods();
                    for (Method method : methods) {
                        if (MethodUtils.checkLocalMethod(method)) {
                            continue;
                        }
                        createProvider(itfer, x, method);
                    }
                });
    }
}
