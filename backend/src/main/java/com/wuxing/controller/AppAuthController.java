package com.wuxing.controller;

import com.wuxing.dto.response.Result;
import com.wuxing.utils.ResultUtil;
import com.wuxing.entity.Member;
import com.wuxing.service.MemberService;
import com.wuxing.utils.JwtUtil;
import com.wuxing.utils.WeChatUtil;
import com.wuxing.dto.request.WeChatLoginRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 小程序认证控制器
 * 
 * @author wuxing
 */
@Tag(name = "小程序认证", description = "小程序认证相关接口")
@RestController
@RequestMapping("/api/app/auth")
@RequiredArgsConstructor
@Slf4j
public class AppAuthController {

    private final MemberService memberService;
    private final WeChatUtil weChatUtil;
    private final JwtUtil jwtUtil;

    @Operation(summary = "检查是否需要手机号授权")
    @PostMapping("/check-phone")
    public Result<Map<String, Object>> checkPhone(@RequestBody Map<String, String> request) {
        try {
            String code = request.get("code");
            log.info("检查手机号授权，code: {}", code);
            
            // 获取openid
            String openid = weChatUtil.getOpenidByCode(code);
            if (openid == null || openid.isEmpty()) {
                log.error("获取openid失败");
                Map<String, Object> data = new HashMap<>();
                data.put("hasPhone", false);
                return ResultUtil.success(data);
            }
            
            // 查找会员
            Member member = memberService.findByOpenid(openid);
            boolean hasPhone = member != null && member.getPhone() != null && !member.getPhone().isEmpty();
            
            log.info("会员openid: {}, 是否有手机号: {}", openid, hasPhone);
            
            Map<String, Object> data = new HashMap<>();
            data.put("hasPhone", hasPhone);
            return ResultUtil.success(data);
        } catch (Exception e) {
            log.error("检查手机号异常", e);
            Map<String, Object> data = new HashMap<>();
            data.put("hasPhone", false);
            return ResultUtil.success(data);
        }
    }
    
    @Operation(summary = "微信登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> wechatLogin(@Valid @RequestBody WeChatLoginRequest request) {
        try {
            log.info("微信登录请求，code: {}", request.getCode());
            
            // 调用微信接口验证code，获取openid等信息
            String openid = weChatUtil.getOpenidByCode(request.getCode());
            
            if (openid == null || openid.isEmpty()) {
                log.error("微信登录验证失败，openid为空");
                return ResultUtil.error("微信登录验证失败");
            }
            
            log.info("获取到openid: {}", openid);
            
            // 解密手机号
            String phone = null;
            if (request.getEncryptedData() != null && request.getIv() != null) {
                phone = weChatUtil.decryptPhoneNumber(request.getEncryptedData(), request.getIv());
                log.info("解密手机号成功: {}", phone != null ? phone.substring(0, 3) + "****" + phone.substring(7) : "解密失败");
            }
            
            // 根据openid查找会员
            Member member = memberService.findByOpenid(openid);
            
            // 如果会员不存在，则创建新会员
            if (member == null) {
                log.info("会员不存在，创建新会员，openid: {}", openid);
                member = new Member();
                member.setOpenid(openid);
                member.setNickname(request.getNickname() != null ? request.getNickname() : "微信用户");
                member.setAvatar(request.getAvatar());
                member.setGender(request.getGender() != null ? request.getGender() : 0);
                member.setPhone(phone);
                member.setStatus(1); // 正常状态
                member.setMemberLevel(1); // 普通会员
                member.setPoints(0); // 初始积分
                
                boolean saved = memberService.saveMember(member);
                if (!saved) {
                    log.error("会员保存失败，openid: {}", openid);
                    return ResultUtil.error("登录失败，请稍后重试");
                }
                log.info("新会员创建成功，id: {}, openid: {}, phone: {}", member.getId(), openid, phone != null ? phone.substring(0, 3) + "****" + phone.substring(7) : "无");
            } else {
                log.info("会员已存在，id: {}, openid: {}", member.getId(), openid);
                
                // 更新会员信息（头像和昵称可能会变）
                if (request.getNickname() != null && !request.getNickname().equals(member.getNickname())) {
                    member.setNickname(request.getNickname());
                }
                if (request.getAvatar() != null && !request.getAvatar().equals(member.getAvatar())) {
                    member.setAvatar(request.getAvatar());
                }
                if (request.getGender() != null && !request.getGender().equals(member.getGender())) {
                    member.setGender(request.getGender());
                }
                // 更新手机号（如果提供了且与现有手机号不同）
                if (phone != null && !phone.equals(member.getPhone())) {
                    member.setPhone(phone);
                }
                
                // 更新最后登录时间
                member.setLastLoginTime(java.time.LocalDateTime.now().toString());
                
                memberService.updateMember(member);
                log.info("会员信息更新成功，id: {}", member.getId());
            }
            
            // 生成JWT token
            String token = jwtUtil.generateToken(member.getId(), member.getNickname());
            
            // 生成会员信息返回
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("memberInfo", member);
            
            log.info("登录成功，会员id: {}, openid: {}", member.getId(), openid);
            return ResultUtil.success(data);
        } catch (Exception e) {
            log.error("微信登录异常", e);
            return ResultUtil.error("登录失败，请稍后重试");
        }
    }
    
    @Operation(summary = "绑定手机号")
    @PostMapping("/bind-phone")
    public Result<Map<String, Object>> bindPhone(@RequestHeader("Authorization") String authorization,
                                                 @RequestBody Map<String, String> request) {
        try {
            // 从Authorization头中提取token
            String token = authorization.replace("Bearer ", "");
            
            // 解析token获取用户ID
            Long memberId = jwtUtil.getUserIdFromToken(token);
            
            // 查找会员
            Member member = memberService.findById(memberId);
            if (member == null) {
                return ResultUtil.error("会员不存在");
            }
            
            // 获取加密数据
            String encryptedData = request.get("encryptedData");
            String iv = request.get("iv");
            
            // 调用微信接口解密手机号
            String phone = weChatUtil.decryptPhoneNumber(encryptedData, iv);
            if (phone == null) {
                return ResultUtil.error("手机号解密失败");
            }
            
            // 更新会员手机号
            member.setPhone(phone);
            memberService.updateMember(member);
            
            // 生成新的token和会员信息返回
            String newToken = jwtUtil.generateToken(member.getId(), member.getNickname());
            
            Map<String, Object> data = new HashMap<>();
            data.put("token", newToken);
            data.put("memberInfo", member);
            
            return ResultUtil.success(data);
        } catch (Exception e) {
            log.error("绑定手机号异常", e);
            return ResultUtil.error("绑定手机号失败，请稍后重试");
        }
    }
}