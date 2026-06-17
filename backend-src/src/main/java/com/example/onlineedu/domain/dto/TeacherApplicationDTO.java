package com.example.onlineedu.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 讲师申请 DTO
 *
 * @author feng
 */
@Data
@ApiModel("讲师申请请求")
public class TeacherApplicationDTO {

    /**
     * 真实姓名
     */
    @ApiModelProperty(value = "真实姓名", required = true, example = "张三")
    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    /**
     * 身份证号
     */
    @ApiModelProperty(value = "身份证号", required = true, example = "110101199001011234")
    @NotBlank(message = "身份证号不能为空")
    private String idCard;

    /**
     * 联系电话
     */
    @ApiModelProperty(value = "联系电话", required = true, example = "13800138000")
    @NotBlank(message = "联系电话不能为空")
    private String phone;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱", example = "teacher@example.com")
    private String email;

    /**
     * 学历
     */
    @ApiModelProperty(value = "学历", example = "本科")
    private String education;

    /**
     * 专业
     */
    @ApiModelProperty(value = "专业", example = "计算机科学与技术")
    private String major;

    /**
     * 教学经验描述
     */
    @ApiModelProperty(value = "教学经验描述", example = "5年Java教学经验")
    private String teachingExperience;

    /**
     * 资质证书URL（多个用逗号分隔）
     */
    @ApiModelProperty(value = "资质证书URL", example = "http://example.com/cert1.jpg,http://example.com/cert2.jpg")
    private String certificateUrls;
}
