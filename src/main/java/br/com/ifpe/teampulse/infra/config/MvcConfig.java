package br.com.ifpe.teampulse.infra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/acesso/cadastro").setViewName("forward:/acesso/cadastro.html");
        registry.addViewController("/acesso/login").setViewName("forward:/acesso/login.html");
        registry.addViewController("/acesso/cadastroadmin").setViewName("forward:/acesso/cadastroadmin.html");
        registry.addViewController("/user/dashboard").setViewName("forward:/user/dashboard.html");
        registry.addViewController("/user/feedbacks").setViewName("forward:/user/feedbacks.html");
        registry.addViewController("/user/profile").setViewName("forward:/user/profile.html");
    }
}