package com.wuxing.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;
import java.io.File;

/**
 * DotEnv 配置类
 * 在应用启动时加载 .env.local 文件中的环境变量
 */
@Configuration
public class DotEnvConfig {
    
    static {
        // 使用 static 块确保在类加载时就获取環境变量
        String projectRoot = System.getProperty("user.dir");
        String envFilePath = projectRoot + "/.env.local";
        File envFile = new File(envFilePath);
        
        if (envFile.exists()) {
            Dotenv dotenv = Dotenv.configure()
                    .directory(projectRoot)
                    .filename(".env.local")
                    .ignoreIfMissing()
                    .load();
            
            // 将 .env.local 中的变量加载到系统属性中
            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
            });
            
            System.out.println("[✓] 已成功加载 .env.local 配置文件");
        } else {
            System.out.println("[✗] .env.local 文件不存在，将使用默认配置");
        }
    }
}
