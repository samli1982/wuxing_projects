package com.wuxing.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuxing.dto.response.Result;
import com.wuxing.entity.Member;
import com.wuxing.service.MemberService;
import com.wuxing.utils.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 会员管理控制器
 * 
 * @author wuxing
 */
@Tag(name = "会员管理", description = "会员管理相关接口")
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "会员列表")
    @GetMapping("/list")
    public Result<Page<Member>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "昵称") @RequestParam(required = false) String nickname,
            @Parameter(description = "手机号") @RequestParam(required = false) String phone,
            @Parameter(description = "关键词（手机号或昵称）") @RequestParam(required = false) String keyword) {
        
        Page<Member> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Member> queryWrapper = new LambdaQueryWrapper<>();
        
        // 构建查询条件
        queryWrapper.eq(Member::getDeleted, 0);
        
        // 如果有keyword参数，使用OR查询（手机号或昵称）
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper -> 
                wrapper.like(Member::getNickname, keyword)
                       .or()
                       .like(Member::getPhone, keyword)
            );
        } else {
            // 如果没有keyword，按原来的逻辑（AND关系）
            if (nickname != null && !nickname.isEmpty()) {
                queryWrapper.like(Member::getNickname, nickname);
            }
            
            if (phone != null && !phone.isEmpty()) {
                queryWrapper.like(Member::getPhone, phone);
            }
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc(Member::getCreateTime);
        
        Page<Member> result = memberService.page(page, queryWrapper);
        return ResultUtil.success(result);
    }

    @Operation(summary = "获取会员详情")
    @GetMapping("/{id}")
    public Result<Member> getInfo(@Parameter(description = "会员ID") @PathVariable Long id) {
        Member member = memberService.getById(id);
        if (member == null || member.getDeleted() == 1) {
            return ResultUtil.error("会员不存在");
        }
        return ResultUtil.success(member);
    }

    @Operation(summary = "更新会员状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @Parameter(description = "会员ID") @PathVariable Long id,
            @Parameter(description = "状态（0禁用 1正常）") @RequestParam Integer status) {
        
        Member member = memberService.getById(id);
        if (member == null || member.getDeleted() == 1) {
            return ResultUtil.error("会员不存在");
        }
        
        member.setStatus(status);
        memberService.updateById(member);
        return ResultUtil.success();
    }
}