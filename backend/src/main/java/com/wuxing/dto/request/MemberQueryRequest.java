package com.wuxing.dto.request;

import com.wuxing.dto.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会员查询请求DTO
 * 
 * @author wuxing
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "会员查询请求")
public class MemberQueryRequest extends PageRequest {

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "手机号")
    private String phone;
}