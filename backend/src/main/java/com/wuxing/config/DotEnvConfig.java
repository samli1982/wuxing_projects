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
    
    public DotEnvConfig() {
        // 查找项目根目录的 .env.local 文件
        String projectRoot = new File(".").getAbsolutePath();
        String envFilePath = projectRoot + "/.env.local";
        File envFile = new File(envFilePath);
        
        // 如果 .env.local 存在，则加载它
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
        }
    }
}
