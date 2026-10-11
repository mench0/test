package com.tms.profilemanager.app.config;

import com.tms.profilemanager.app.utils.MessageSourceUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.Validator;

@Configuration
public class MessageSourceConfiguration {

    @Bean
    public ResourceBundleMessageSource messageSource() {
        var source = new ResourceBundleMessageSource();
        source.setBasenames("messages/permissions", "messages/validation");
        source.setDefaultEncoding("UTF-8");
        MessageSourceUtils.setMessageSource(source);
        return source;
    }

    @Bean
    @Primary
    public Validator localValidator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }
}
