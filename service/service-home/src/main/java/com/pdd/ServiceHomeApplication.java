package com.pdd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author youzairichangdawang
 * @version 1.0
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//因为没有使用数据库，所以取消数据源自动配置，防止报错
@EnableDiscoveryClient
@EnableFeignClients
public class ServiceHomeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceHomeApplication.class, args);
    }
}
