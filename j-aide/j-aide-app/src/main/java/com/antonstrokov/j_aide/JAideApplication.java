package com.antonstrokov.j_aide;

import com.antonstrokov.j_aide.core.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.antonstrokov.j_aide.core.config.AiProperties;


@SpringBootApplication
@EnableConfigurationProperties({AiProperties.class, AppProperties.class})
public class JAideApplication {

	public static void main(String[] args) {
		SpringApplication.run(JAideApplication.class, args);
	}
}
