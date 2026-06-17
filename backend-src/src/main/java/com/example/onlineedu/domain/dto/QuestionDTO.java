package com.example.onlineedu.domain.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 问题提交DTO
 *
 * @author feng
 */
@Data
public class QuestionDTO {

    /**
     * 问题ID（更新时需要）
     */
    private Long id;

    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    /**
     * 问题标题
     */
    @NotBlank(message = "问题标题不能为空")
    @Length(max = 200, message = "标题长度不能超过200字符")
    private String title;

    /**
     * 问题内容
     */
    @NotBlank(message = "问题内容不能为空")
    private String content;
}
