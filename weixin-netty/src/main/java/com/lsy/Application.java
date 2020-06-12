package com.lsy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.lsy", "org.n3r.idworker"})
//扫描mybatis mapper包
@MapperScan(basePackages = {"com.lsy.mapper"})
public class Application {

    @Bean
    public SpringUtil getSpringUtils() {
        return new SpringUtil();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
