package com.example.onlineedu.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 讲师申请 VO
 *
 * @author feng
 */
@Data
@ApiModel("讲师申请响应")
public class TeacherApplicationVO {

    /**
     * 申请ID
     */
    @ApiModelProperty("申请ID")
    private Long id;

    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    private Long userId;

    /**
     * 用户名
     */
    @ApiModelProperty("用户名")
    private String username;

    /**
     * 真实姓名
     */
    @ApiModelProperty("真实姓名")
    private String realName;

    /**
     * 身份证号
     */
    @ApiModelProperty("身份证号")
    private String idCard;

    /**
     * 联系电话
     */
    @ApiModelProperty("联系电话")
    private String phone;

    /**
     * 邮箱
     */
    @ApiModelProperty("邮箱")
    private String email;

    /**
     * 学历
     */
    @ApiModelProperty("学历")
    private String education;

    /**
     * 专业
     */
    @ApiModelProperty("专业")
    private String major;

    /**
     * 教学经验描述
     */
    @ApiModelProperty("教学经验描述")
    private String teachingExperience;

    /**
     * 资质证书URL
     */
    @ApiModelProperty("资质证书URL")
    private String certificateUrls;

    /**
     * 审核状态
     */
    @ApiModelProperty("审核状态：0-待审核，1-已通过，2-已拒绝，3-修改待审核，4.已取消讲师资格")
    private Integer status;

    /**
     * 拒绝原因
     */
    @ApiModelProperty("拒绝原因")
    private String rejectReason;

    /**
     * 审核人ID
     */
    @ApiModelProperty("审核人ID")
    private Long auditorId;

    /**
     * 审核人姓名
     */
    @ApiModelProperty("审核人姓名")
    private String auditorName;

    /**
     * 审核时间
     */
    @ApiModelProperty("审核时间")
    private LocalDateTime auditTime;

    /**
     * 申请时间
     */
    @ApiModelProperty("申请时间")
    private LocalDateTime createTime;
}
