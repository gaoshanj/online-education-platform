package com.example.onlineedu.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 修改用户角色集合 DTO
 *
 * @author feng
 */
@Data
@ApiModel("修改用户角色集合请求")
public class UpdateUserRolesDTO {

    @ApiModelProperty(value = "角色编码集合（至少一个）", required = true, example = "[\"STUDENT\",\"TEACHER\"]")
    @NotEmpty(message = "角色不能为空，至少选择一个")
    private List<String> roleCodes;
}
