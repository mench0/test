package com.tms.profilemanager.app.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class BeanUtils {

    private static ApplicationContext context;

    @Autowired
    public void setContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    public static <T> T getBean(Class<T> beanClass) {
        if (context == null) {
            throw new IllegalStateException("BeanUtils.context is not initialized");
        }
        return context.getBean(beanClass);
    }
}
