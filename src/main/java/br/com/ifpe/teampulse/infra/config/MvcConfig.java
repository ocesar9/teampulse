package br.com.ifpe.teampulse.infra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/cadastro").setViewName("forward:/views/acesso/cadastro.html");
        registry.addViewController("/login").setViewName("forward:/views/acesso/login.html");
        registry.addViewController("/cadastroadmin").setViewName("forward:/views/acesso/cadastroadmin.html");
        registry.addViewController("/dashboard").setViewName("forward:/views/areaLogada/dashboard.html");
        registry.addViewController("/feedbacks").setViewName("forward:/views/areaLogada/feedbacks.html");
        registry.addViewController("/profile").setViewName("forward:/views/areaLogada/profile.html");
    }
}