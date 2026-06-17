package com.example.onlineedu.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 讲师申请实体类
 *
 * @author feng
 */
@Data
@TableName("teacher_application")
public class TeacherApplicationEntity extends BaseEntity{

    /**
     * 申请ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 学历
     */
    private String education;

    /**
     * 专业
     */
    private String major;

    /**
     * 教学经验描述
     */
    private String teachingExperience;

    /**
     * 资质证书URL（多个用逗号分隔）
     */
    private String certificateUrls;

    /**
     * 审核状态：0-待审核，1-已通过，2-已拒绝，3-修改待审核，4.已取消讲师资格
     */
    private Integer status;

    /**
     * 拒绝原因
     */
    private String rejectReason;

    /**
     * 审核人ID
     */
    private Long auditorId;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;
}
