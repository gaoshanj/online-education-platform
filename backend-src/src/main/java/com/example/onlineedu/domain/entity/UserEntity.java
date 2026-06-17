package com.example.onlineedu.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体类
 *
 * @author feng
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class UserEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 用户名
	 */
	@TableField("username")
	private String username;

	/**
	 * 密码（加密）
	 */
	@TableField("password")
	private String password;

	/**
	 * 昵称
	 */
	@TableField("nickname")
	private String nickname;

	/**
	 * 头像URL
	 */
	@TableField("avatar")
	private String avatar;

	/**
	 * 邮箱
	 */
	@TableField("email")
	private String email;

	/**
	 * 手机号
	 */
	@TableField("phone")
	private String phone;

	/**
	 * 性别：0-保密，1-男，2-女
	 */
	@TableField("gender")
	private Integer gender;

	/**
	 * 状态：0-禁用，1-正常
	 */
	@TableField("status")
	private Integer status;
}
