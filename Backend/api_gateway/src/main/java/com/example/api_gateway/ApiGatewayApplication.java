package com.example.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.api_gateway.filter.GatewayJwtFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean<GatewayJwtFilter> jwtFilterRegistration(GatewayJwtFilter jwtFilter) {
		FilterRegistrationBean<GatewayJwtFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(jwtFilter);
		registration.addUrlPatterns("/gateway/*");  // Apply filter only on /gateway paths
		return registration;
	}
}
