package com.example.onlineedu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.common.ErrorCode;
import com.example.onlineedu.domain.dto.*;
import com.example.onlineedu.domain.entity.RoleEntity;
import com.example.onlineedu.domain.entity.UserEntity;
import com.example.onlineedu.domain.entity.UserRoleEntity;
import com.example.onlineedu.domain.vo.LoginVO;
import com.example.onlineedu.domain.vo.UserDetailVO;
import com.example.onlineedu.domain.vo.UserInfoVO;
import com.example.onlineedu.exception.BusinessException;
import com.example.onlineedu.mapper.RoleMapper;
import com.example.onlineedu.mapper.UserMapper;
import com.example.onlineedu.mapper.UserRoleMapper;
import com.example.onlineedu.service.UserService;
import com.example.onlineedu.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 *
 * @author feng
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MailUtil mailUtil;

    private static final String DEFAULT_ROLE_CODE = "STUDENT";
    private static final String REDIS_TOKEN_BLACKLIST_PREFIX = "token:blacklist:";
    // 统一的验证码 Redis key 格式：email:code:{scene}:{email}
    // scene 取値：register / forgot / update-email
    private static final String REDIS_EMAIL_CODE_PREFIX = "email:code:";
    private static final String REDIS_UPDATE_EMAIL_CODE_PREFIX = "email:code:update-email:";
    private static final String REDIS_FORGOT_PASSWORD_CODE_PREFIX = "email:code:forgot:";
    private static final String REDIS_REGISTER_CODE_PREFIX = "email:code:register:";
    private static final long EMAIL_CODE_EXPIRE = 300L; // 5分钟

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO registerDTO) {
        // 1. 校验用户名是否已存在
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getUsername, registerDTO.getUsername());
        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXIST, "用户名已存在");
        }

        // 2. 校验邮筱验证码
        String redisCode = (String) redisUtil.get(REDIS_REGISTER_CODE_PREFIX + registerDTO.getEmail());
        if (redisCode == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "验证码已过期，请重新获取");
        }
        if (!registerDTO.getEmailCode().equals(redisCode)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "邮筱验证码错误");
        }

        // 3. 创建用户
        UserEntity user = new UserEntity();
        BeanUtils.copyProperties(registerDTO, user);
        // MD5 加密
        user.setPassword(PasswordUtil.encrypt(registerDTO.getPassword()));
        user.setStatus(1); // 默认正常状态
        user.setIsDeleted(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userMapper.insert(user);

        // 4. 分配默认角色（学生）
        RoleEntity role = roleMapper.selectOne(new LambdaQueryWrapper<RoleEntity>()
                .eq(RoleEntity::getRoleCode, DEFAULT_ROLE_CODE));

        if (role == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统默认角色配置错误");
        }

        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setUserId(user.getId());
        userRole.setRoleId(role.getId());
        userRole.setCreateTime(LocalDateTime.now());

        userRoleMapper.insert(userRole);

        // 5. 删除已使用的验证码
        redisUtil.del(REDIS_REGISTER_CODE_PREFIX + registerDTO.getEmail());

        log.info("用户注册成功：{}", user.getUsername());
    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 1. 根据用户名查询用户
        UserEntity user = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, loginDTO.getUsername()));

        if (user == null) {
            throw new BusinessException(ErrorCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 2. 校验密码
        if (!PasswordUtil.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 3. 校验用户状态
        if (user.getStatus() != 1) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }

        // 4. 查询用户角色
        // 这里需要关联查询，暂时先简单实现，后续可以在 UserMapper 中添加自定义 SQL
        List<UserRoleEntity> userRoles = userRoleMapper.selectList(new LambdaQueryWrapper<UserRoleEntity>()
                .eq(UserRoleEntity::getUserId, user.getId()));

        List<String> roleCodes;
        if (userRoles.isEmpty()) {
            roleCodes = Collections.emptyList();
        } else {
            List<Long> roleIds = userRoles.stream().map(UserRoleEntity::getRoleId).collect(Collectors.toList());
            List<RoleEntity> roles = roleMapper.selectBatchIds(roleIds);
            roleCodes = roles.stream().map(RoleEntity::getRoleCode).collect(Collectors.toList());
        }

        // 5. 校验 loginType 与角色是否匹配
        String loginType = loginDTO.getLoginType();
        String requiredRole;
        String portalName;
        switch (loginType == null ? "" : loginType) {
            case "app":
                requiredRole = "STUDENT";
                portalName = "用户端";
                break;
            case "teacher":
                requiredRole = "TEACHER";
                portalName = "讲师端";
                break;
            case "admin":
                requiredRole = "ADMIN";
                portalName = "管理员端";
                break;
            default:
                throw new BusinessException(ErrorCode.PARAM_ERROR, "loginType 不合法，只支持 app / teacher / admin");
        }
        if (!roleCodes.contains(requiredRole)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "您没有登录" + portalName + "的权限");
        }

        // 6. 生成 Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), roleCodes);

        // 6. 封装返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setRoles(roleCodes);

        UserInfoVO userInfo = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfo);
        loginVO.setUserInfo(userInfo);

        log.info("用户登录成功：{}", user.getUsername());
        return loginVO;
    }

    @Override
    public void logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (token != null && jwtUtil.validateToken(token)) {
            // 获取剩余有效时间
            long remainTime = jwtUtil.getRemainTimeFromToken(token);
            if (remainTime > 0) {
                // 加入黑名单
                redisUtil.set(REDIS_TOKEN_BLACKLIST_PREFIX + token, "1", remainTime);
            }
        }

        UserContext.clear();
        log.info("用户注销成功");
    }

    @Override
    public UserInfoVO getUserInfo(Long userId) {
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        UserInfoVO userInfo = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfo);
        return userInfo;
    }

    @Override
    public Page<UserDetailVO> pageUsers(
            Integer page, Integer size, String username, Integer status) {
        // 构建查询条件
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(username), UserEntity::getUsername, username)
                .eq(status != null, UserEntity::getStatus, status)
                .orderByDesc(UserEntity::getCreateTime);

        // 分页查询
        Page<UserEntity> entityPage = userMapper
                .selectPage(new Page<>(page, size), queryWrapper);

        // 转换为VO
        Page<UserDetailVO> voPage = new Page<>(
                entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(entityPage.getRecords().stream()
                .map(this::entityToDetailVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public UserDetailVO getUserDetail(Long userId) {
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        return entityToDetailVO(user);
    }

    @Override
    public void disableUser(Long userId) {
        // 不能禁用自己
        Long currentUserId = UserContext.getUserId();
        if (currentUserId != null && currentUserId.equals(userId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "不能禁用自己");
        }

        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        user.setStatus(0);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("禁用用户成功：{}", user.getUsername());
    }

    @Override
    public void enableUser(Long userId) {
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        user.setStatus(1);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("启用用户成功：{}", user.getUsername());
    }

    @Override
    public void deleteUser(Long userId) {
        // 不能删除自己
        Long currentUserId = UserContext.getUserId();
        if (currentUserId != null && currentUserId.equals(userId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "不能删除自己");
        }

        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 逻辑删除
        userMapper.deleteById(userId);

        log.info("删除用户成功：{}", user.getUsername());
    }

    @Override
    public void resetPassword(Long userId) {
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 重置为默认密码 123456
        user.setPassword(PasswordUtil.encrypt("123456"));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("重置用户密码成功：{}", user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserInfo(Long userId, UserUpdateDTO dto) {
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 更新可以修改的字段
        if (StringUtils.hasText(dto.getNickname())) {
            user.setNickname(dto.getNickname());
        }
        if (StringUtils.hasText(dto.getAvatar())) {
            user.setAvatar(dto.getAvatar());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }
        // 手机号可选更新
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }

        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(Long userId, UserPasswordUpdateDTO dto) {
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 校验旧密码
        if (!PasswordUtil.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "旧密码错误");
        }

        // 更新新密码
        user.setPassword(PasswordUtil.encrypt(dto.getNewPassword()));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public void sendUpdateEmailCode(Long userId, String email) {
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        
        // 校验邮箱是否已被其他用户使用
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getEmail, email)
                .ne(UserEntity::getId, userId);
        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "该邮箱已被其他账号绑定");
        }

        // 生成6位验证码
        String code = generateRandomCode(6);
        // 存入Redis（先存码，再异步发邮件）
        redisUtil.set(REDIS_UPDATE_EMAIL_CODE_PREFIX + email, code, EMAIL_CODE_EXPIRE);

        // 异步发送邮件（立即返回，后台线程完成发送）
        String subject = "【在线教育平台】绑定/修改邮箱验证码";
        String content = "您的验证码为：" + code + "，有效期为5分钟。请勿泄露给他人。";
        mailUtil.sendTextMailAsync(email, subject, content);
        log.info("已异步发送修改邮箱验证码：email={}", email);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEmail(Long userId, UserEmailUpdateDTO dto) {
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 校验验证码
        String redisCode = (String) redisUtil.get(REDIS_UPDATE_EMAIL_CODE_PREFIX + dto.getEmail());
        if (!dto.getCode().equals(redisCode)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "验证码错误或已过期");
        }

        // 校验邮箱是否被占用 (双重校验)
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getEmail, dto.getEmail())
                .ne(UserEntity::getId, userId);
        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "该邮箱已被其他账号绑定");
        }

        user.setEmail(dto.getEmail());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 删除已使用的验证码
        redisUtil.del(REDIS_UPDATE_EMAIL_CODE_PREFIX + dto.getEmail());
    }

    @Override
    public void sendResetCode(String email) {
        // 委托给统一发码方法（forgot 场景会自动校验邮箱是否存在）
        sendEmailCode(email, "forgot");
    }

    @Override
    public void sendEmailCode(String email, String scene) {
        if (!java.util.Set.of("register", "forgot").contains(scene)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "scene 不合法，只支持 register / forgot");
        }

        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEntity::getEmail, email);
        long emailCount = userMapper.selectCount(wrapper);

        if ("register".equals(scene)) {
            // 注册：邮箱不能已被注册
            if (emailCount > 0) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "该邮箱已被注册，请直接登录或找回密码");
            }
        } else {
            // forgot：邮箱必须存在
            if (emailCount == 0) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "该邮箱尚未绑定任何账号");
            }
        }

        String code = generateRandomCode(6);
        String redisKey = REDIS_EMAIL_CODE_PREFIX + scene + ":" + email;
        redisUtil.set(redisKey, code, EMAIL_CODE_EXPIRE);

        // 异步发送邮件（立即返回，后台线程完成发送）
        String subject = "register".equals(scene)
                ? "【在线教育平台】注册验证码"
                : "【在线教育平台】找回密码验证码";
        String content = "您的验证码为：" + code + "，有效期为5分钟。请勿泄露给他人。";
        mailUtil.sendTextMailAsync(email, subject, content);
        log.info("已异步发送邮箱验证码：email={}, scene={}", email, scene);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void forgotPassword(UserForgotPasswordDTO dto) {
        String redisCode = (String) redisUtil.get(REDIS_FORGOT_PASSWORD_CODE_PREFIX + dto.getEmail());
        if (!dto.getCode().equals(redisCode)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "验证码错误或已过期");
        }

        UserEntity user = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getEmail, dto.getEmail()));
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST, "未找到该邮箱对应的账号");
        }

        // 更新密码
        user.setPassword(PasswordUtil.encrypt(dto.getNewPassword()));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 删除已使用的验证码
        redisUtil.del(REDIS_FORGOT_PASSWORD_CODE_PREFIX + dto.getEmail());
    }

    /**
     * 生成指定长度的纯数字随机验证码
     */
    private String generateRandomCode(int length) {
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * Entity 转 UserDetailVO
     */
    private UserDetailVO entityToDetailVO(UserEntity entity) {
        UserDetailVO vo = new UserDetailVO();
        BeanUtils.copyProperties(entity, vo);

        // 查询用户角色
        List<UserRoleEntity> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRoleEntity>().eq(UserRoleEntity::getUserId, entity.getId()));

        if (!userRoles.isEmpty()) {
            List<Long> roleIds = userRoles.stream().map(UserRoleEntity::getRoleId).collect(Collectors.toList());
            List<RoleEntity> roles = roleMapper.selectBatchIds(roleIds);
            List<String> roleCodes = roles.stream().map(RoleEntity::getRoleCode).collect(Collectors.toList());
            vo.setRoles(roleCodes);
        } else {
            vo.setRoles(Collections.emptyList());
        }

        return vo;
    }

    // ==================== 管理员新增接口 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUser(AdminCreateUserDTO dto) {
        // 1. 校验用户名唯一
        if (userMapper.selectCount(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, dto.getUsername())) > 0) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXIST, "用户名已存在");
        }

        //TODO 校验邮箱唯一

        // 2. 校验所有角色编码都存在
        List<RoleEntity> roles = resolveRoles(dto.getRoleCodes());

        // 3. 创建用户
        UserEntity user = new UserEntity();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(PasswordUtil.encrypt(dto.getPassword()));
        user.setStatus(1);
        user.setIsDeleted(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);

        // 4. 批量插入角色关联
        bindRoles(user.getId(), roles);

        log.info("管理员创建用户成功：{}，角色：{}", dto.getUsername(), dto.getRoleCodes());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserRoles(Long userId, UpdateUserRolesDTO dto) {
        // 1. 校验用户存在
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 2. 校验所有角色编码都存在
        List<RoleEntity> roles = resolveRoles(dto.getRoleCodes());

        // 3. 清空旧角色（物理删除 user_role 关联）
        userRoleMapper.delete(new LambdaQueryWrapper<UserRoleEntity>()
                .eq(UserRoleEntity::getUserId, userId));

        // 4. 批量写入新角色
        bindRoles(userId, roles);

        log.info("管理员修改用户 {} 角色成功：{}", userId, dto.getRoleCodes());
    }

    /**
     * 根据 roleCode 列表查询角色实体，有任一不存在则抛出异常
     */
    private List<RoleEntity> resolveRoles(List<String> roleCodes) {
        List<RoleEntity> roles = roleMapper.selectList(new LambdaQueryWrapper<RoleEntity>()
                .in(RoleEntity::getRoleCode, roleCodes));
        if (roles.size() != roleCodes.size()) {
            // 找出不存在的角色编码
            List<String> foundCodes = roles.stream().map(RoleEntity::getRoleCode).collect(Collectors.toList());
            List<String> notFound = roleCodes.stream()
                    .filter(c -> !foundCodes.contains(c))
                    .collect(Collectors.toList());
            throw new BusinessException(ErrorCode.PARAM_ERROR, "以下角色编码不存在：" + notFound);
        }
        return roles;
    }

    /**
     * 批量绑定用户角色
     */
    private void bindRoles(Long userId, List<RoleEntity> roles) {
        for (RoleEntity role : roles) {
            UserRoleEntity ur = new UserRoleEntity();
            ur.setUserId(userId);
            ur.setRoleId(role.getId());
            ur.setCreateTime(LocalDateTime.now());
            userRoleMapper.insert(ur);
        }
    }
}
