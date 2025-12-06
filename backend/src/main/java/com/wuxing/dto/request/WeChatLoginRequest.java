package com.wuxing.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 微信登录请求DTO
 * 
 * @author wuxing
 */
@Data
@Schema(description = "微信登录请求")
public class WeChatLoginRequest {

    @Schema(description = "微信临时登录凭证", required = true)
    @NotBlank(message = "code不能为空")
    private String code;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "性别 0未知 1男 2女")
    private Integer gender;

    @Schema(description = "手机号加密数据")
    private String encryptedData;

    @Schema(description = "手机号加密向量")
    private String iv;
}