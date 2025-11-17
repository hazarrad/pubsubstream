package com.micro.pubstream;

import com.micro.pubstream.config.PropertiesConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(PropertiesConfig.class)
public class PubstreamApplication {

	public static void main(String[] args) {
		SpringApplication.run(PubstreamApplication.class, args);
	}

}
