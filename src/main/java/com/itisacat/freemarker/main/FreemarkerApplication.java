package com.itisacat.freemarker.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages="com.itisacat.freemarker")
public class FreemarkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FreemarkerApplication.class, args);
	}
}
