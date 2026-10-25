package br.com.pw2m.contos;

import br.com.pw2m.contos.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class ContosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContosApplication.class, args);
    }
}
