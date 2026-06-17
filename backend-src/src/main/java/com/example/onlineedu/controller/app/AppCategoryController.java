package com.example.onlineedu.controller.app;

import com.example.onlineedu.common.Result;
import com.example.onlineedu.domain.vo.CourseCategoryVO;
import com.example.onlineedu.service.CourseCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端分类控制器
 *
 * @author feng
 */
@Slf4j
@Api(tags = "用户端-课程分类")
@RestController
@RequestMapping("/api/app/category")
public class AppCategoryController {

    @Autowired
    private CourseCategoryService categoryService;

    @ApiOperation("查询所有分类（树形结构）")
    @GetMapping("/list")
    public Result<List<CourseCategoryVO>> list() {
        List<CourseCategoryVO> list = categoryService.listAll();
        return Result.success(list);
    }
}
