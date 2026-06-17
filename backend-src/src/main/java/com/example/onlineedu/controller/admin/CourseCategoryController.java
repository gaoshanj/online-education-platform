package com.example.onlineedu.controller.admin;

import com.example.onlineedu.annotation.RequireRole;
import com.example.onlineedu.common.Result;
import com.example.onlineedu.domain.dto.CourseCategoryDTO;
import com.example.onlineedu.domain.enums.UserRole;
import com.example.onlineedu.domain.vo.CourseCategoryVO;
import com.example.onlineedu.service.CourseCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 课程分类管理控制器
 *
 * @author feng
 */
@Slf4j
@Api(tags = "管理端-课程分类管理")
@RestController
@RequestMapping("/api/admin/category")
public class CourseCategoryController {

    @Autowired
    private CourseCategoryService categoryService;

    @ApiOperation("查询所有分类（树形结构）")
    @GetMapping("/list")
    @RequireRole({ UserRole.ADMIN, UserRole.TEACHER })
    public Result<List<CourseCategoryVO>> list() {
        List<CourseCategoryVO> list = categoryService.listAll();
        return Result.success(list);
    }

    @ApiOperation("添加分类")
    @PostMapping("/add")
    @RequireRole({ UserRole.ADMIN })
    public Result<Void> add(@RequestBody @Valid CourseCategoryDTO dto) {
        categoryService.add(dto);
        return Result.success();
    }

    @ApiOperation("更新分类")
    @PutMapping("/update")
    @RequireRole({ UserRole.ADMIN })
    public Result<Void> update(@RequestBody @Valid CourseCategoryDTO dto) {
        categoryService.update(dto);
        return Result.success();
    }

    @ApiOperation("删除分类")
    @DeleteMapping("/delete/{id}")
    @RequireRole({ UserRole.ADMIN })
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }
}
