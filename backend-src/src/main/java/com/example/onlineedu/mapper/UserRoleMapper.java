package com.example.onlineedu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.onlineedu.domain.entity.UserRoleEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联 Mapper 接口
 *
 * @author feng
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRoleEntity> {
}
