package com.wuxing;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 五行项目启动类
 * 
 * @author wuxing
 */
@SpringBootApplication
@MapperScan("com.wuxing.mapper")
public class WuxingApplication {

    public static void main(String[] args) {
        SpringApplication.run(WuxingApplication.class, args);
        System.out.println("====================================");
        System.out.println("五行项目启动成功！");
        System.out.println("API文档地址: http://localhost:8080/doc.html");
        System.out.println("====================================");
    }
}
