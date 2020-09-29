package com.webank.ai.fatecloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.webank.ai.fatecloud.system.dao.*")
public class CloudManagerApplication {
    public static void main(String[] args){
        SpringApplication.run(CloudManagerApplication.class,args);
    }
}
