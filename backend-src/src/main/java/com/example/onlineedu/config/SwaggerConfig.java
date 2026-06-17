package com.example.onlineedu.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.temporal.Temporal;

/**
 * Swagger 配置
 *
 */
@Configuration
@EnableSwagger2
@Slf4j
public class SwaggerConfig {

	@Bean
	public Docket docket() {
		log.info("准备生成接口文档");
		ApiInfo apiInfo = new ApiInfoBuilder()
				.title("在线教育平台接口文档")
				.version("2.0")
				.description("在线教育平台接口文档")
				.build();
		Docket docket = new Docket(DocumentationType.SWAGGER_2)
				.groupName("Default")
				.apiInfo(apiInfo)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.example.onlineedu.controller"))
				.paths(PathSelectors.any())
				.build();
		return docket;
	}
}
