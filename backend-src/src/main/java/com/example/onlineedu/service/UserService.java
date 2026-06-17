package com.example.onlineedu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.domain.dto.*;
import com.example.onlineedu.domain.vo.LoginVO;
import com.example.onlineedu.domain.vo.UserDetailVO;
import com.example.onlineedu.domain.vo.UserInfoVO;

/**
 * 用户服务接口
 *
 * @author feng
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     */
    void register(RegisterDTO registerDTO);

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 登录响应（Token + 用户信息）
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 用户注销
     *
     * @param token JWT Token
     */
    void logout(String token);

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserInfoVO getUserInfo(Long userId);

    /**
     * 分页查询用户（管理端）
     *
     * @param page     当前页
     * @param size     每页大小
     * @param username 用户名（可选）
     * @param status   状态（可选）
     * @return 分页结果
     */
    Page<UserDetailVO> pageUsers(Integer page, Integer size,
                                 String username, Integer status);

    /**
     * 查询用户详情（管理端）
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    UserDetailVO getUserDetail(Long userId);

    /**
     * 禁用用户
     *
     * @param userId 用户ID
     */
    void disableUser(Long userId);

    /**
     * 启用用户
     *
     * @param userId 用户ID
     */
    void enableUser(Long userId);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     */
    void deleteUser(Long userId);

    /**
     * 重置用户密码 (管理端)
     *
     * @param userId 用户ID
     */
    void resetPassword(Long userId);

    /**
     * 更新当前用户信息
     *
     * @param userId 用户ID
     * @param dto    更新信息
     */
    void updateUserInfo(Long userId, UserUpdateDTO dto);

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param dto    密码更新信息
     */
    void updatePassword(Long userId, UserPasswordUpdateDTO dto);

    /**
     * 发送更换邮箱验证码
     *
     * @param userId 当前用户ID
     * @param email  新邮箱
     */
    void sendUpdateEmailCode(Long userId, String email);

    /**
     * 更换邮箱
     *
     * @param userId 当前用户ID
     * @param dto    更换邮箱信息
     */
    void updateEmail(Long userId, UserEmailUpdateDTO dto);

    /**
     * 发送找回密码验证码（无需登录）
     *
     * @param email 注册邮箱
     */
    void sendResetCode(String email);

    /**
     * 统一发送邮箱验证码（无需登录，适用于注册/忘记密码场景）
     * scene 取值：register（注册）、forgot（忘记密码）
     * 修改邮箱请使用 sendUpdateEmailCode（需登录校验不重复）
     *
     * @param email 目标邮箱
     * @param scene 使用场景
     */
    void sendEmailCode(String email, String scene);

    /**
     * 忘记密码重置
     *
     * @param dto 重置密码信息
     */
    void forgotPassword(UserForgotPasswordDTO dto);

    /**
     * 管理员创建用户（指定角色集合）
     *
     * @param dto 创建用户信息（含 roleCodes，至少一个）
     */
    void createUser(AdminCreateUserDTO dto);

    /**
     * 修改用户角色集合
     *
     * @param userId   目标用户ID
     * @param dto      新角色集合（至少一个）
     */
    void updateUserRoles(Long userId, UpdateUserRolesDTO dto);
}
