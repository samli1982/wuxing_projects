package com.wuxing;

import io.github.cdimascio.dotenv.Dotenv;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.File;

/**
 * 五行项目启动类
 * 
 * @author wuxing
 */
@SpringBootApplication
@MapperScan("com.wuxing.mapper")
public class WuxingApplication {

    public static void main(String[] args) {
        // 在启动 Spring Boot 前，先加载 .env.local 配置文件
        loadEnvFile();
        
        SpringApplication.run(WuxingApplication.class, args);
        System.out.println("====================================");
        System.out.println("五行项目启动成功！");
        System.out.println("API文档地址: http://localhost:8080/doc.html");
        System.out.println("====================================");
    }
    
    /**
     * 加载 .env.local 配置文件
     */
    private static void loadEnvFile() {
        String projectRoot = System.getProperty("user.dir");
        
        // 先查找当前目录下的 .env.local
        File envFile = new File(projectRoot + "/.env.local");
        
        // 如果不存在，尝试找父目录下的 .env.local（应对从 backend 或基础目录启动）
        if (!envFile.exists() && projectRoot.endsWith("backend")) {
            projectRoot = new File(projectRoot).getParent();
            envFile = new File(projectRoot + "/.env.local");
        }
        
        if (envFile.exists()) {
            try {
                Dotenv dotenv = Dotenv.configure()
                        .directory(projectRoot)
                        .filename(".env.local")
                        .ignoreIfMissing()
                        .load();
                
                // 将 .env.local 中的上起来到系统属性中，以便 Spring Boot 可以读取
                dotenv.entries().forEach(entry -> {
                    System.setProperty(entry.getKey(), entry.getValue());
                });
                
                System.out.println("[✓] 成功加载 .env.local 配置文件 (位置: " + projectRoot + "/.env.local)");
            } catch (Exception e) {
                System.err.println("[✗] 加载 .env.local 失败: " + e.getMessage());
            }
        } else {
            System.out.println("[⚠️ ] .env.local 文件不存在，将使用默认配置 (查找位置: " + projectRoot + "/.env.local)");
        }
    }
}
