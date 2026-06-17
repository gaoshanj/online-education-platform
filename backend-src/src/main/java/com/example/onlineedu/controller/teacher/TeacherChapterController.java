package com.example.onlineedu.controller.teacher;

import com.example.onlineedu.annotation.RequireRole;
import com.example.onlineedu.common.Result;
import com.example.onlineedu.domain.dto.CourseChapterDTO;
import com.example.onlineedu.domain.enums.UserRole;
import com.example.onlineedu.domain.vo.CourseChapterTreeVO;
import com.example.onlineedu.service.CourseChapterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 讲师端 - 课程章节管理控制器
 * 讲师管理自己课程下的章节，所有写操作均会校验课程归属
 *
 * @author feng
 */
@Slf4j
@Api(tags = "讲师端-章节管理")
@RestController
@RequestMapping("/api/teacher/chapter")
public class TeacherChapterController {

    @Autowired
    private CourseChapterService chapterService;

    @ApiOperation("添加章节（校验课程归属）")
    @PostMapping("/add")
    @RequireRole({ UserRole.TEACHER })
    public Result<Void> add(@RequestBody @Valid CourseChapterDTO dto) {
        chapterService.add(dto);
        return Result.success();
    }

    @ApiOperation("更新章节（校验课程归属）")
    @PutMapping("/update")
    @RequireRole({ UserRole.TEACHER })
    public Result<Void> update(@RequestBody @Valid CourseChapterDTO dto) {
        chapterService.update(dto);
        return Result.success();
    }

    @ApiOperation("删除章节（校验课程归属）")
    @DeleteMapping("/delete/{id}")
    @RequireRole({ UserRole.TEACHER })
    public Result<Void> delete(@PathVariable Long id) {
        chapterService.delete(id);
        return Result.success();
    }

    @ApiOperation("查询课程章节树")
    @GetMapping("/tree/{courseId}")
    @RequireRole({ UserRole.TEACHER })
    public Result<List<CourseChapterTreeVO>> getChapterTree(@PathVariable Long courseId) {
        List<CourseChapterTreeVO> tree = chapterService.getChapterTree(courseId);
        return Result.success(tree);
    }

    @ApiOperation("调整章节排序（校验课程归属）")
    @PutMapping("/sort")
    @RequireRole({ UserRole.TEACHER })
    public Result<Void> updateSort(
            @ApiParam("章节ID") @RequestParam Long id,
            @ApiParam("排序序号") @RequestParam Integer sortOrder) {
        chapterService.updateSort(id, sortOrder);
        return Result.success();
    }
}
