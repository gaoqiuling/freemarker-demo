package com.itisacat.freemarker.service.services.util;

import com.itisacat.freemarker.service.facade.IAnimalService;
import com.itisacat.freemarker.service.util.EmptyUtils;
import com.itisacat.freemarker.service.util.context.SpringApplicationContext;
import com.itisacat.freemarker.service.util.exception.CcException;
import com.itisacat.freemarker.support.enums.AnimalEnum;
import com.itisacat.freemarker.support.model.dto.request.BaseRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ServiceHelper implements ApplicationListener<ContextRefreshedEvent> {
    private static final Map<String, IAnimalService> ANIMAL_SERVICE_MAP = new ConcurrentHashMap<>();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext().getParent();
        if (context == null) {
            System.out.println("spring容器初始化完毕");
            System.out.println("root application context");
        }
        loadAllServices(event.getApplicationContext());
    }

    private void loadAllServices(ApplicationContext context) {
        if (EmptyUtils.isEmpty(context)) {
            stopServer("failed load spring context!");
        }
        for (AnimalEnum animalEnum : AnimalEnum.values()) {
            loadService(animalEnum.getServiceName(), ANIMAL_SERVICE_MAP, IAnimalService.class, context);
            log.info("Success load animal services!");
        }
    }

    public <T extends BaseRequest> IAnimalService getAnimalService(T request) {
        return getAnimalService(request.getType());
    }

    public IAnimalService getAnimalService(Integer businessType) {
        return getAnimalService(AnimalEnum.validate(businessType).getServiceName());
    }

    public IAnimalService getAnimalService(String serviceName) {
        return getService(serviceName, IAnimalService.class, ANIMAL_SERVICE_MAP);
    }

    private <T> T getService(String serviceName, Class<T> clazz, Map<String, T> serviceMap) {
        if (EmptyUtils.isEmpty(serviceName)) {
            throw new CcException(-40001, "ServiceName is empty!");
        }
        T result = serviceMap.get(serviceName);
        if (EmptyUtils.isNotEmpty(result)) {
            return result;
        }
        synchronized (this) {
            result = serviceMap.get(serviceName);
            if (EmptyUtils.isNotEmpty(result)) {
                return result;
            }
            result = SpringApplicationContext.getBean(serviceName, clazz);
            if (EmptyUtils.isEmpty(result)) {
                throw new CcException(-40001, "Cannot found service for " + serviceName);
            }
            serviceMap.put(serviceName, result);
            return result;
        }
    }

    private <T> void loadService(String name, Map<String, T> serviceMap, Class<T> clazz, ApplicationContext context) {
        if (EmptyUtils.isNotEmpty(serviceMap.get(name))) {
            return;
        }

        T service = context.getBean(name, clazz);
        if (EmptyUtils.isEmpty(service)) {
            stopServer("Cannot load service for " + name);
        }
        serviceMap.put(name, service);
    }

    private void stopServer(String msg) {
        log.error(msg);
        Runtime.getRuntime().exit(2);
    }
}
