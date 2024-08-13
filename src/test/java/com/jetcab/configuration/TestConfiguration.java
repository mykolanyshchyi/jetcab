package com.jetcab.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

@org.springframework.boot.test.context.TestConfiguration
public class TestConfiguration {

    @Bean
    public MessageSourceAccessor messageSourceAccessor() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages_validation");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        return new MessageSourceAccessor(messageSource, getLocale());
    }
}
