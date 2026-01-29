package com.example.gnote;

import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
/**
 * @author 17853
 * 测试描述时去掉排除
 */
@SpringBootApplication( exclude = RedissonAutoConfiguration.class)
public class GNoteApplication {

    public static void main(String[] args) {
        SpringApplication.run(GNoteApplication.class, args);
    }

}
