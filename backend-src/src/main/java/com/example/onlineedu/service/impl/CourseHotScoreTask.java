package com.example.onlineedu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.onlineedu.domain.entity.CourseEntity;
import com.example.onlineedu.mapper.CourseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 课程热度分数定时刷新任务
 *
 * 热度公式（每小时整点全量计算）：
 *   hot_score = view_count      × 1
 *             + student_count   × 10
 *             + like_count      × 3
 *             + favorite_count  × 5
 *             + avg_score × review_count × 1.5   （评分评价分数）
 *             + question_count  × 4              （问答活跃度，由提问/删除时实时维护）
 *
 * @author feng
 */
@Slf4j
@Component
public class CourseHotScoreTask {

    @Autowired
    private CourseMapper courseMapper;

    /**
     * 每小时整点全量刷新已发布课程的热度分数
     */
    @Scheduled(cron = "0 0 * * * *")
    //@Scheduled(cron = "*/5 * * * * *") 测试用5s刷新一次
    public void refreshHotScore() {
        log.info("开始刷新课程热度分数...");
        long start = System.currentTimeMillis();

        List<CourseEntity> courses = courseMapper.selectList(
                new LambdaQueryWrapper<CourseEntity>()
                        .eq(CourseEntity::getStatus, 2)
                        .eq(CourseEntity::getIsDeleted, 0));

        if (courses.isEmpty()) {
            log.info("没有已发布的课程，跳过热度刷新");
            return;
        }

        int count = 0;
        for (CourseEntity c : courses) {
            BigDecimal score = calcHotScore(c);
            courseMapper.update(null,
                    new LambdaUpdateWrapper<CourseEntity>()
                            .set(CourseEntity::getHotScore, score)
                            .eq(CourseEntity::getId, c.getId()));
            count++;
        }

        long cost = System.currentTimeMillis() - start;
        log.info("课程热度分数刷新完成，共更新 {} 条，耗时 {}ms", count, cost);
    }

    /**
     * 计算单门课程热度分数
     * question_count 为冗余字段，由 QuestionServiceImpl 在提问/删除时实时维护
     */
    private BigDecimal calcHotScore(CourseEntity c) {
        int view      = c.getViewCount()      != null ? c.getViewCount()      : 0;
        int student   = c.getStudentCount()   != null ? c.getStudentCount()   : 0;
        int like      = c.getLikeCount()      != null ? c.getLikeCount()      : 0;
        int favorite  = c.getFavoriteCount()  != null ? c.getFavoriteCount()  : 0;
        int review    = c.getReviewCount()    != null ? c.getReviewCount()    : 0;
        int question  = c.getQuestionCount()  != null ? c.getQuestionCount()  : 0;
        BigDecimal avg = c.getAvgScore()      != null ? c.getAvgScore()       : BigDecimal.ZERO;

        // avg_score × review_count × 1.5：将评分质量与评价数挂钩，低样本评分贡献自然偏小
        double score = view      * 1
                + student  * 10
                + like     * 3
                + favorite * 5
                + avg.doubleValue() * review * 1.5
                + question * 4;

        return BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP);
    }
}
