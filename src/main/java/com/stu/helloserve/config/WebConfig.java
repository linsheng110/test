package com.stu.helloserve.config;

import com.stu.helloserve.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/api/**")
                // 只放行登录、注册，不放行修改/删除/查询（按基础作业要求）
                .excludePathPatterns("/api/users/login", "/api/users");
    }
}