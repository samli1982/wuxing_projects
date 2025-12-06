package com.wuxing.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 微信工具类
 * 
 * @author wuxing
 */
@Slf4j
@Component
public class WeChatUtil {

    @Value("${wechat.appid}")
    private String appid;

    @Value("${wechat.secret}")
    private String secret;

    @Value("${wechat.dev-mode:false}")
    private boolean devMode;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 通过微信code获取openid
     * 
     * @param code 登录凭证
     * @return openid
     */
    public String getOpenidByCode(String code) {
        // 开发模式下直接返回模拟的openid
        if (devMode) {
            log.info("微信登录开发模式：使用模拟openid");
            // 开发模式下使用固定的openid，避免每次登录都创建新会员
            return "dev_openid_fixed_for_testing";
        }

        // 检查配置是否完整
        if (!StringUtils.hasText(appid) || !StringUtils.hasText(secret)) {
            log.error("微信配置不完整：appid或secret为空");
            return null;
        }

        try {
            // 微信接口地址
            String urlStr = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appid, secret, code
            );

            // 发起HTTP请求
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            // 读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // 解析JSON响应
            JsonNode jsonNode = objectMapper.readTree(response.toString());
            
            // 检查是否有错误
            if (jsonNode.has("errcode") && jsonNode.get("errcode").asInt() != 0) {
                log.error("微信登录验证失败: errcode={}, errmsg={}", 
                    jsonNode.get("errcode").asInt(), 
                    jsonNode.has("errmsg") ? jsonNode.get("errmsg").asText() : "未知错误"
                );
                return null;
            }

            // 返回openid
            if (jsonNode.has("openid")) {
                return jsonNode.get("openid").asText();
            }

            log.error("微信登录验证失败: 未返回openid");
            return null;
        } catch (Exception e) {
            log.error("微信登录验证异常", e);
            return null;
        }
    }
    
    /**
     * 解密微信手机号
     * 
     * @param encryptedData 加密数据
     * @param iv 偏移量
     * @return 手机号
     */
    public String decryptPhoneNumber(String encryptedData, String iv) {
        // 开发模式下直接返回模拟的手机号
        if (devMode) {
            log.info("微信解密开发模式：使用模拟手机号");
            return "13800138000";
        }
        
        try {
            // 注意：实际开发中需要通过session_key来解密，这里简化处理
            // 真实场景需要先通过code获取session_key，然后用session_key解密
            log.warn("当前实现仅为演示，实际项目中需要通过session_key解密手机号");
            return null;
        } catch (Exception e) {
            log.error("手机号解密异常", e);
            return null;
        }
    }
    
    /**
     * 获取session_key（用于解密手机号等敏感信息）
     * 
     * @param code 登录凭证
     * @return session_key
     */
    public String getSessionKey(String code) {
        // 开发模式下返回模拟的session_key
        if (devMode) {
            log.info("微信获取session_key开发模式：使用模拟session_key");
            return "dev_session_key_" + System.currentTimeMillis();
        }

        // 检查配置是否完整
        if (!StringUtils.hasText(appid) || !StringUtils.hasText(secret)) {
            log.error("微信配置不完整：appid或secret为空");
            return null;
        }

        try {
            // 微信接口地址
            String urlStr = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appid, secret, code
            );

            // 发起HTTP请求
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            // 读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // 解析JSON响应
            JsonNode jsonNode = objectMapper.readTree(response.toString());
            
            // 检查是否有错误
            if (jsonNode.has("errcode") && jsonNode.get("errcode").asInt() != 0) {
                log.error("获取session_key失败: errcode={}, errmsg={}", 
                    jsonNode.get("errcode").asInt(), 
                    jsonNode.has("errmsg") ? jsonNode.get("errmsg").asText() : "未知错误"
                );
                return null;
            }

            // 返回session_key
            if (jsonNode.has("session_key")) {
                return jsonNode.get("session_key").asText();
            }

            log.error("获取session_key失败: 未返回session_key");
            return null;
        } catch (Exception e) {
            log.error("获取session_key异常", e);
            return null;
        }
    }
}