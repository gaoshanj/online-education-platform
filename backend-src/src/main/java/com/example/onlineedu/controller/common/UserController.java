package com.example.onlineedu.controller.common;

import com.example.onlineedu.common.Result;
import com.example.onlineedu.domain.dto.UserEmailUpdateDTO;
import com.example.onlineedu.domain.dto.UserForgotPasswordDTO;
import com.example.onlineedu.domain.dto.UserPasswordUpdateDTO;
import com.example.onlineedu.domain.dto.UserUpdateDTO;
import com.example.onlineedu.domain.vo.UserInfoVO;
import com.example.onlineedu.service.UserService;
import com.example.onlineedu.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户中心接口
 */
@Api(tags = "用户中心接口")
@RestController
@RequestMapping("/api/common/user")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation("获取当前用户信息")
    @GetMapping("/info")
    public Result<UserInfoVO> getUserInfo() {
        Long userId = UserContext.getUserId();
        return Result.success(userService.getUserInfo(userId));
    }

    @ApiOperation("修改当前用户信息")
    @PutMapping("/info")
    public Result<Void> updateUserInfo(@Validated @RequestBody UserUpdateDTO dto) {
        Long userId = UserContext.getUserId();
        userService.updateUserInfo(userId, dto);
        return Result.success();
    }

    @ApiOperation("修改密码")
    @PutMapping("/password")
    public Result<Void> updatePassword(@Validated @RequestBody UserPasswordUpdateDTO dto) {
        Long userId = UserContext.getUserId();
        userService.updatePassword(userId, dto);
        return Result.success();
    }

    @ApiOperation("发送更换邮箱验证码")
    @PostMapping("/send-update-email-code")
    public Result<Void> sendUpdateEmailCode(@RequestParam String email) {
        Long userId = UserContext.getUserId();
        userService.sendUpdateEmailCode(userId, email);
        return Result.success();
    }

    @ApiOperation("更换邮箱")
    @PutMapping("/email")
    public Result<Void> updateEmail(@Validated @RequestBody UserEmailUpdateDTO dto) {
        Long userId = UserContext.getUserId();
        userService.updateEmail(userId, dto);
        return Result.success();
    }

//    @ApiOperation("发送找回密码验证码")
//    @PostMapping("/send-reset-code")
//    public Result<Void> sendResetCode(@RequestParam String email) {
//        userService.sendResetCode(email);
//        return Result.success();
//    }

    @ApiOperation("找回密码重置")
    @PostMapping("/forgot-password")
    public Result<Void> forgotPassword(@Validated @RequestBody UserForgotPasswordDTO dto) {
        userService.forgotPassword(dto);
        return Result.success();
    }
}