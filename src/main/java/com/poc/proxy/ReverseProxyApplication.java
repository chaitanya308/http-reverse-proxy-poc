package com.poc.proxy;

import com.poc.proxy.configuration.ReverseProxyFilterConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import static org.springframework.boot.SpringApplication.run;

@Import(ReverseProxyFilterConfiguration.class)
@SpringBootApplication
public class ReverseProxyApplication {
	public static void main(String[] args) {
		run(ReverseProxyApplication.class, args);
	}
}
