package com.example.onlineedu.config;

import com.example.onlineedu.config.FileUploadConfig;
import com.example.onlineedu.interceptor.JwtInterceptor;
import com.example.onlineedu.interceptor.TokenParseInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置类
 * 配置静态资源映射等
 *
 * @author feng
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Autowired
    private TokenParseInterceptor tokenParseInterceptor;

    @Autowired
    private FileUploadConfig fileUploadConfig;

    /**
     * 配置拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. 注册全量 Token 解析拦截器（由于它不阻断请求，所以不需要 exclude）
        registry.addInterceptor(tokenParseInterceptor)
                .addPathPatterns("/api/**");

        // 2. 注册核心权限校验拦截器（针对白名单直接放行）
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/auth/send-email-code",
                        "/api/app/banner/list",
                        "/api/app/category/list",
                        "/api/app/chapter/**",
                        "/api/app/course/page",
                        "/api/app/course/detail/{id}",
                        "/api/app/course/review/list",
                        "/api/app/course/review/stat/{courseId}",
                        "/api/app/question/course/{courseId}",
                        "/api/app/question/detail/{id}",
                        "/api/app/question/answer/replies/{answerId}",
                        "/api/app/recommend/**",
                        "/api/app/public-chat/**",
                        //"/api/common/file/**",
                        "/doc.html",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/v2/api-docs");
    }

    /**
     * 配置静态资源映射
     * 将 /files/** 映射到本地文件上传目录
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 文件访问映射 - 使用配置文件中的路径，适配 Windows/Linux
        String uploadPath = fileUploadConfig.getPath().replace("\\", "/");
        if (!uploadPath.endsWith("/")) uploadPath += "/";
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + uploadPath);

        // Knife4j 文档资源
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     *  配置跨域
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 所有接口都允许跨域
                //.allowedOriginPatterns("http://localhost:5173") // 允许前端访问
                .allowedOriginPatterns("*") // 允许所有来源（生产环境）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的请求方法
                .allowedHeaders("*") // 允许请求头
                .allowCredentials(true) // 是否允许携带 cookie
                .maxAge(3600); // 缓存 preflight 请求 1 小时
    }
}
