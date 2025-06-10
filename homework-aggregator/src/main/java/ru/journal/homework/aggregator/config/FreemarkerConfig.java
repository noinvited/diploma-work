package ru.journal.homework.aggregator.config;

import freemarker.template.TemplateException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.IOException;

@Configuration
public class FreemarkerConfig {
    
    @Bean
    public FreeMarkerConfigurer freeMarkerConfigurer() throws IOException, TemplateException {
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        configurer.setTemplateLoaderPath("classpath:/templates");
        configurer.setDefaultEncoding("UTF-8");
        
        // Настройка конфигурации Freemarker
        freemarker.template.Configuration configuration = configurer.createConfiguration();
        configuration.setURLEscapingCharset("UTF-8");
        configurer.setConfiguration(configuration);
        
        return configurer;
    }
} 