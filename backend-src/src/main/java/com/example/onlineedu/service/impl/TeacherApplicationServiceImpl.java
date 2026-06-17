package com.example.onlineedu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.common.ErrorCode;
import com.example.onlineedu.domain.dto.TeacherApplicationDTO;
import com.example.onlineedu.domain.entity.RoleEntity;
import com.example.onlineedu.domain.entity.TeacherApplicationEntity;
import com.example.onlineedu.domain.entity.UserEntity;
import com.example.onlineedu.domain.entity.UserRoleEntity;
import com.example.onlineedu.domain.vo.TeacherApplicationVO;
import com.example.onlineedu.exception.BusinessException;
import com.example.onlineedu.mapper.RoleMapper;
import com.example.onlineedu.mapper.TeacherApplicationMapper;
import com.example.onlineedu.mapper.UserMapper;
import com.example.onlineedu.mapper.UserRoleMapper;
import com.example.onlineedu.service.TeacherApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * 讲师申请服务实现类
 *
 * @author feng
 */
@Slf4j
@Service
public class TeacherApplicationServiceImpl implements TeacherApplicationService {

    @Autowired
    private TeacherApplicationMapper applicationMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    private static final String TEACHER_ROLE_CODE = "TEACHER";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitApplication(TeacherApplicationDTO dto, Long userId) {
        // 检查用户是否存在
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 检查是否已经是讲师
        RoleEntity teacherRole = roleMapper.selectOne(
                new LambdaQueryWrapper<RoleEntity>().eq(RoleEntity::getRoleCode, TEACHER_ROLE_CODE));
        if (teacherRole != null) {
            Long count = userRoleMapper.selectCount(
                    new LambdaQueryWrapper<UserRoleEntity>()
                            .eq(UserRoleEntity::getUserId, userId)
                            .eq(UserRoleEntity::getRoleId, teacherRole.getId()));
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "您已经是讲师，无需申请");
            }
        }

        // 查询最新申请记录
        TeacherApplicationEntity existing = applicationMapper.selectOne(
                new LambdaQueryWrapper<TeacherApplicationEntity>()
                        .eq(TeacherApplicationEntity::getUserId, userId)
                        .orderByDesc(TeacherApplicationEntity::getCreateTime)
                        .last("LIMIT 1"));

        if (existing != null) {
            int s = existing.getStatus();
            if (s == 1) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "您的讲师申请已通过，无需重复申请");
            }
            if (s == 0 || s == 3) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "您有待审核的申请，无需重复申请，请耐心等待");
            }
            // 已拒绝(2)/已取消资格（4）：引导用户使用 updateApplication 接口
            throw new BusinessException(ErrorCode.PARAM_ERROR, "您的申请已被拒绝或您的讲师资格已被取消，请使用「修改申请」接口重新提交");
        }

        // 首次申请
        TeacherApplicationEntity entity = new TeacherApplicationEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setUserId(userId);
        entity.setStatus(0); // 待审核
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());

        applicationMapper.insert(entity);
        log.info("用户提交讲师申请成功：userId={}", userId);
    }

    @Override
    public TeacherApplicationVO getMyApplication(Long userId) {
        // 查询最新的申请
        TeacherApplicationEntity entity = applicationMapper.selectOne(
                new LambdaQueryWrapper<TeacherApplicationEntity>()
                        .eq(TeacherApplicationEntity::getUserId, userId)
                        .orderByDesc(TeacherApplicationEntity::getCreateTime)
                        .last("LIMIT 1"));

        if (entity == null) {
            return null;
        }

        return entityToVO(entity);
    }

    @Override
    public Page<TeacherApplicationVO> pageApplications(Integer page, Integer size, Integer status) {
        // 构建查询条件
        LambdaQueryWrapper<TeacherApplicationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(status != null, TeacherApplicationEntity::getStatus, status)
                .orderByAsc(TeacherApplicationEntity::getStatus) // 待审核的排前面
                .orderByDesc(TeacherApplicationEntity::getCreateTime);

        // 分页查询
        Page<TeacherApplicationEntity> entityPage = applicationMapper.selectPage(
                new Page<>(page, size), queryWrapper);

        // 转换为VO
        Page<TeacherApplicationVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(),
                entityPage.getTotal());
        voPage.setRecords(entityPage.getRecords().stream()
                .map(this::entityToVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public TeacherApplicationVO getDetail(Long id) {
        TeacherApplicationEntity entity = applicationMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "申请不存在");
        }

        return entityToVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id, Long auditorId) {
        TeacherApplicationEntity entity = applicationMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "申请不存在");
        }
        // 支持 待审核(0) 和 修改待审核(3)
        if (entity.getStatus() != 0 && entity.getStatus() != 3) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "该申请已审核完毕，无需重复操作");
        }

        // 查询TEACHER角色
        RoleEntity teacherRole = roleMapper.selectOne(
                new LambdaQueryWrapper<RoleEntity>().eq(RoleEntity::getRoleCode, TEACHER_ROLE_CODE));
        if (teacherRole == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "讲师角色不存在");
        }

        // 添加角色（防重复）
        Long existCount = userRoleMapper.selectCount(
                new LambdaQueryWrapper<UserRoleEntity>()
                        .eq(UserRoleEntity::getUserId, entity.getUserId())
                        .eq(UserRoleEntity::getRoleId, teacherRole.getId()));
        if (existCount == 0) {
            UserRoleEntity userRole = new UserRoleEntity();
            userRole.setUserId(entity.getUserId());
            userRole.setRoleId(teacherRole.getId());
            userRole.setCreateTime(LocalDateTime.now());
            userRoleMapper.insert(userRole);
        }

        entity.setStatus(1); // 已通过
        entity.setAuditorId(auditorId);
        entity.setAuditTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        applicationMapper.updateById(entity);

        log.info("审核通过讲师申请：id={}, userId={}", id, entity.getUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long id, Long auditorId, String rejectReason) {
        TeacherApplicationEntity entity = applicationMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "申请不存在");
        }
        // 支持 待审核(0) 和 修改待审核(3)
        if (entity.getStatus() != 0 && entity.getStatus() != 3) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "该申请已审核完毕，无需重复操作");
        }

        entity.setStatus(2); // 已拒绝
        entity.setRejectReason(rejectReason);
        entity.setAuditorId(auditorId);
        entity.setAuditTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        applicationMapper.updateById(entity);

        log.info("拒绝讲师申请：id={}, userId={}, reason={}", id, entity.getUserId(), rejectReason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateApplication(TeacherApplicationDTO dto, Long userId) {
        TeacherApplicationEntity entity = applicationMapper.selectOne(
                new LambdaQueryWrapper<TeacherApplicationEntity>()
                        .eq(TeacherApplicationEntity::getUserId, userId)
                        .orderByDesc(TeacherApplicationEntity::getCreateTime)
                        .last("LIMIT 1"));
        if (entity == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "没有找到申请记录，请先提交申请");
        }
        int s = entity.getStatus();
        // 待审核(0)和修改待审核(3)：不允许修改
        if (s == 0 || s == 3) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "申请待审核期间不能修改，请等待审核完成");
        }
        if (s == 1) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "申请已通过，如需修改认证信息请在讲师端操作");
        }
        // 只允许：已拒绝(2) 或 已取消讲师资格(4)
        BeanUtils.copyProperties(dto, entity);
        entity.setStatus(0); // 重新设为待审核
        entity.setRejectReason(null);
        entity.setAuditorId(null);
        entity.setAuditTime(null);
        entity.setUpdateTime(LocalDateTime.now());
        applicationMapper.updateById(entity);

        log.info("用户修改申请并重新提交：userId={}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateByTeacher(TeacherApplicationDTO dto, Long userId) {
        TeacherApplicationEntity entity = applicationMapper.selectOne(
                new LambdaQueryWrapper<TeacherApplicationEntity>()
                        .eq(TeacherApplicationEntity::getUserId, userId)
                        .orderByDesc(TeacherApplicationEntity::getCreateTime)
                        .last("LIMIT 1"));
        if (entity == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "没有找到申请记录");
        }
        int s = entity.getStatus();
        // 待审核(0)和修改待审核(3)：不允许修改
        if (s == 0 || s == 3) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "认证信息已在审核中，请等待审核完成再操作");
        }
        // 允许：已通过(1) 或 已取消讲师资格(4)
        BeanUtils.copyProperties(dto, entity);
        entity.setStatus(3); // 修改待审核
        entity.setRejectReason(null);
        entity.setAuditorId(null);
        entity.setAuditTime(null);
        entity.setUpdateTime(LocalDateTime.now());
        applicationMapper.updateById(entity);

        log.info("讲师修改认证信息并重新提交审核：userId={}", userId);
    }

    /**
     * Entity 转 VO
     */
    private TeacherApplicationVO entityToVO(TeacherApplicationEntity entity) {
        TeacherApplicationVO vo = new TeacherApplicationVO();
        BeanUtils.copyProperties(entity, vo);

        // 查询用户名
        UserEntity user = userMapper.selectById(entity.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
        }

        // 查询审核人姓名
        if (entity.getAuditorId() != null) {
            UserEntity auditor = userMapper.selectById(entity.getAuditorId());
            if (auditor != null) {
                vo.setAuditorName(auditor.getNickname() != null ? auditor.getNickname() : auditor.getUsername());
            }
        }

        return vo;
    }
}
