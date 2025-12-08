package com.revature.movieservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String currentDir = System.getProperty("user.dir");
        
        registry.addResourceHandler("/display/**")
                .addResourceLocations("file:" + currentDir + "/public/display/");
        
        registry.addResourceHandler("/banner/**")
                .addResourceLocations("file:" + currentDir + "/public/banner/");
    }
}