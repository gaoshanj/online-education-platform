package com.example.onlineedu.controller.app;

import com.example.onlineedu.common.Result;
import com.example.onlineedu.domain.entity.CourseChapterEntity;
import com.example.onlineedu.domain.model.LoginUser;
import com.example.onlineedu.domain.vo.ChapterProgressVO;
import com.example.onlineedu.domain.vo.CourseChapterTreeVO;
import com.example.onlineedu.service.CourseChapterService;
import com.example.onlineedu.service.LearningProgressService;
import com.example.onlineedu.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端章节控制器
 *
 * @author feng
 */
@Slf4j
@Api(tags = "用户端-课程章节")
@RestController
@RequestMapping("/api/app/chapter")
public class AppChapterController {

    @Autowired
    private CourseChapterService chapterService;

    @Autowired
    private LearningProgressService progressService;

    @ApiOperation("获取课程章节列表(树形结构)")
    @GetMapping("/{courseId}/list")
    public Result<List<CourseChapterTreeVO>> getChapterList(@PathVariable Long courseId) {
        List<CourseChapterTreeVO> chapterTree = chapterService.getChapterTree(courseId);
        
        // 如果用户已登录,添加学习进度信息
        LoginUser currentUser = UserContext.getUser();
        if (currentUser != null) {
            Long userId = currentUser.getUserId();
            List<ChapterProgressVO> progressList = progressService.getCourseProgress(userId, courseId);
            
            // 将进度信息合并到章节树中
            mergeProgressToChapterTree(chapterTree, progressList);
        }
        
        return Result.success(chapterTree);
    }


    /**
     * 将学习进度合并到章节树中
     */
    private void mergeProgressToChapterTree(List<CourseChapterTreeVO> chapterTree, 
                                           List<ChapterProgressVO> progressList) {
        for (CourseChapterTreeVO chapter : chapterTree) {
            // 查找对应的进度
            ChapterProgressVO progress = progressList.stream()
                    .filter(p -> p.getChapterId().equals(chapter.getId()))
                    .findFirst()
                    .orElse(null);
            
            if (progress != null) {
                chapter.setLastPosition(progress.getLastPosition());
                chapter.setMaxPosition(progress.getMaxPosition());
                chapter.setIsCompleted(progress.getIsCompleted());
                
                // 计算进度百分比，使用真实的历史最高进度进行计算
                if (chapter.getVideoDuration() != null && chapter.getVideoDuration() > 0) {
                    int percent = (progress.getMaxPosition() * 100) / chapter.getVideoDuration();
                    chapter.setProgressPercent(Math.min(percent, 100));
                } else {
                    chapter.setProgressPercent(progress.getIsCompleted() == 1 ? 100 : 0);
                }
            } else {
                chapter.setLastPosition(0);
                chapter.setMaxPosition(0);
                chapter.setIsCompleted(0);
                chapter.setProgressPercent(0);
            }
            
            // 递归处理子章节
            if (chapter.getChildren() != null && !chapter.getChildren().isEmpty()) {
                mergeProgressToChapterTree(chapter.getChildren(), progressList);
            }
        }
    }

    /**
     * 从章节树中查找指定ID的章节
     */
    private CourseChapterTreeVO findChapterById(List<CourseChapterTreeVO> tree, Long chapterId) {
        for (CourseChapterTreeVO chapter : tree) {
            if (chapter.getId().equals(chapterId)) {
                return chapter;
            }
            
            if (chapter.getChildren() != null && !chapter.getChildren().isEmpty()) {
                CourseChapterTreeVO found = findChapterById(chapter.getChildren(), chapterId);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
