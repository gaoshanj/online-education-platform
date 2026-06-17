package com.example.onlineedu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.onlineedu.domain.dto.UserCourseScore;
import com.example.onlineedu.domain.entity.CourseCategoryEntity;
import com.example.onlineedu.domain.entity.CourseEntity;
import com.example.onlineedu.domain.entity.UserEntity;
import com.example.onlineedu.domain.vo.CourseVO;
import com.example.onlineedu.mapper.CourseCategoryMapper;
import com.example.onlineedu.mapper.CourseMapper;
import com.example.onlineedu.mapper.RecommendMapper;
import com.example.onlineedu.mapper.UserMapper;
import com.example.onlineedu.service.RecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 课程推荐服务实现类
 * 实现四个首页推荐区块：热门课程 / 最新上线 / 精品课程 / ItemCF个性化推荐
 *
 * @author feng
 */
@Slf4j
@Service
public class RecommendServiceImpl implements RecommendService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseCategoryMapper categoryMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RecommendMapper recommendMapper;


    /**
     * 热门课程：按热度分降序
     *
     */
    @Override
    public List<CourseVO> getHotRecommend(int limit) {
        LambdaQueryWrapper<CourseEntity> wrapper = new LambdaQueryWrapper<CourseEntity>()
                .eq(CourseEntity::getStatus, 2)
                .eq(CourseEntity::getIsDeleted, 0)
                .orderByDesc(CourseEntity::getHotScore)
                .last("LIMIT " + limit);
        return courseMapper.selectList(wrapper).stream()
                .map(this::entityToVO)
                .collect(Collectors.toList());
    }

    /**
     * 最新上线课程：按 create_time 降序
     */
    @Override
    public List<CourseVO> getLatestRecommend(int limit) {
        LambdaQueryWrapper<CourseEntity> wrapper = new LambdaQueryWrapper<CourseEntity>()
                .eq(CourseEntity::getStatus, 2)
                .eq(CourseEntity::getIsDeleted, 0)
                .orderByDesc(CourseEntity::getCreateTime)
                .last("LIMIT " + limit);
        return courseMapper.selectList(wrapper).stream()
                .map(this::entityToVO)
                .collect(Collectors.toList());
    }

    /**
     * 精品课程：is_recommend=1，按热度降序
     */
    @Override
    public List<CourseVO> getFeaturedRecommend(int limit) {
        LambdaQueryWrapper<CourseEntity> wrapper = new LambdaQueryWrapper<CourseEntity>()
                .eq(CourseEntity::getStatus, 2)
                .eq(CourseEntity::getIsDeleted, 0)
                .eq(CourseEntity::getIsRecommend, 1)
                .orderByDesc(CourseEntity::getHotScore)
                .last("LIMIT " + limit);
        return courseMapper.selectList(wrapper).stream()
                .map(this::entityToVO)
                .collect(Collectors.toList());
    }

//    /**
//     * 个性化推荐（ItemCF 混合策略）
//     * 降级策略统一改为综合排序（avg_score → student_count → create_time），并自动去重
//     */
//    @Override
//    public List<CourseVO> getPersonalRecommend(Long userId, int limit) {
//        // 未登录，降级为综合排序
//        if (userId == null) {
//            log.info("[推荐] 未登录用户，降级返回综合排序课程");
//            return getComprehensiveRecommend(limit, Collections.emptySet());
//        }
//
//        // 1: 查询目标用户行为评分
//        List<UserCourseScore> userBehaviors = recommendMapper.getUserBehaviorScores(userId);
//        if (userBehaviors.isEmpty()) {
//            log.info("[推荐] 用户 {} 无行为数据，冷启动降级综合排序", userId);
//            return getComprehensiveRecommend(limit, Collections.emptySet());
//        }
//
//        // 目标用户的课程兴趣评分 Map<courseId, score>
//        Map<Long, Double> userScoreMap = userBehaviors.stream()
//                .collect(Collectors.toMap(UserCourseScore::getCourseId, UserCourseScore::getTotalScore));
//
//        // 已学课程ID集合（用于排除）
//        Set<Long> learnedCourseIds = new HashSet<>(recommendMapper.getEnrolledCourseIds(userId));
//
//        // 2: 获取全量用户-课程评分，构建课程向量
//        List<UserCourseScore> allScores = recommendMapper.getAllUserCourseScores();
//        Map<Long, Map<Long, Double>> courseVectors = new HashMap<>();
//        for (UserCourseScore s : allScores) {
//            courseVectors
//                    .computeIfAbsent(s.getCourseId(), k -> new HashMap<>())
//                    .put(s.getUserId(), s.getTotalScore());
//        }
//
//        // 3: 计算用户已学课程与所有课程的余弦相似度
//        Map<Long, Double> candidateScores = new HashMap<>();
//        for (Long learnedCourseId : userScoreMap.keySet()) {
//            Map<Long, Double> vec_i = courseVectors.getOrDefault(learnedCourseId, Collections.emptyMap());
//            if (vec_i.isEmpty()) continue;
//            double norm_i = norm(vec_i);
//            for (Map.Entry<Long, Map<Long, Double>> entry : courseVectors.entrySet()) {
//                Long candidateId = entry.getKey();
//                if (learnedCourseIds.contains(candidateId)) continue;
//                Map<Long, Double> vec_j = entry.getValue();
//                double dot = dotProduct(vec_i, vec_j);
//                if (dot == 0) continue;
//                double sim = dot / (norm_i * norm(vec_j));
//                double userWeight = userScoreMap.get(learnedCourseId);
//                candidateScores.merge(candidateId, userWeight * sim, Double::sum);
//            }
//        }
//
//        if (candidateScores.isEmpty()) {
//            log.info("[推荐] 用户 {} CF 候选集为空，降级综合排序", userId);
//            return getComprehensiveRecommend(limit, learnedCourseIds);
//        }
//
//        // 4: 归一化 cfScore
//        double maxCf = Collections.max(candidateScores.values());
//        if (maxCf > 0) candidateScores.replaceAll((id, score) -> score / maxCf);
//
//        // 5: 查询候选课程实体，计算混合得分
//        List<Long> candidateIds = new ArrayList<>(candidateScores.keySet());
//        List<CourseEntity> candidateCourses = courseMapper.selectList(
//                new LambdaQueryWrapper<CourseEntity>()
//                        .eq(CourseEntity::getStatus, 2)
//                        .eq(CourseEntity::getIsDeleted, 0)
//                        .in(CourseEntity::getId, candidateIds));
//
//        double maxHot = candidateCourses.stream()
//                .mapToDouble(c -> c.getHotScore() != null ? c.getHotScore().doubleValue() : 0.0)
//                .max().orElse(1.0);
//        LocalDateTime now = LocalDateTime.now();
//
//        List<CourseVO> result = candidateCourses.stream()
//                .sorted(Comparator.comparingDouble((CourseEntity c) -> {
//                    double cfScore  = candidateScores.getOrDefault(c.getId(), 0.0);
//                    double rawHot   = c.getHotScore() != null ? c.getHotScore().doubleValue() : 0.0;
//                    double hotScore = maxHot > 0 ? rawHot / maxHot : 0;
//                    long daysOld    = ChronoUnit.DAYS.between(c.getCreateTime(), now);
//                    double newBoost = 100.0 * Math.exp(-0.1 * daysOld);
//                    return cfScore * 0.5 + hotScore * 0.35 + newBoost * 0.15;
//                }).reversed())
//                .limit(limit)
//                .map(this::entityToVO)
//                .collect(Collectors.toList());
//
//        // 6: 结果不足时用综合排序补齐，排除已展示及已学课程
//        if (result.size() < limit) {
//            Set<Long> excludeIds = new HashSet<>(learnedCourseIds);
//            result.stream().map(CourseVO::getId).forEach(excludeIds::add);
//            List<CourseVO> fill = getComprehensiveRecommend(limit - result.size(), excludeIds);
//            result.addAll(fill);
//        }
//
//        return result;
//    }

    /**
     * 个性化推荐（ItemCF）
     * 降级策略统一改为综合排序（avg_score → student_count → create_time），并自动去重
     */
    @Override
    public List<CourseVO> getPersonalRecommend(Long userId, int limit) {
        // 未登录，降级为综合排序
        if (userId == null) {
            log.info("[推荐] 未登录用户，降级返回综合排序课程");
            return getComprehensiveRecommend(limit, Collections.emptySet());
        }

        // 1: 查询目标用户行为评分
        List<UserCourseScore> userBehaviors = recommendMapper.getUserBehaviorScores(userId);
        if (userBehaviors.isEmpty()) {
            log.info("[推荐] 用户 {} 无行为数据，冷启动降级综合排序", userId);
            return getComprehensiveRecommend(limit, Collections.emptySet());
        }

        // 目标用户的课程兴趣评分 Map<courseId, score>
        Map<Long, Double> userScoreMap = userBehaviors.stream()
                .collect(Collectors.toMap(UserCourseScore::getCourseId, UserCourseScore::getTotalScore));

        // 已学课程ID集合（用于排除）
        Set<Long> learnedCourseIds = new HashSet<>(recommendMapper.getEnrolledCourseIds(userId));

        // 2: 获取全量用户-课程评分，构建课程向量
        List<UserCourseScore> allScores = recommendMapper.getAllUserCourseScores();
        Map<Long, Map<Long, Double>> courseVectors = new HashMap<>();
        for (UserCourseScore s : allScores) {
            courseVectors
                    .computeIfAbsent(s.getCourseId(), k -> new HashMap<>())
                    .put(s.getUserId(), s.getTotalScore());
        }

        // 3: 计算用户已学课程与所有课程的余弦相似度
        Map<Long, Double> candidateScores = new HashMap<>();
        for (Long learnedCourseId : userScoreMap.keySet()) {
            Map<Long, Double> vec_i = courseVectors.getOrDefault(learnedCourseId, Collections.emptyMap());
            if (vec_i.isEmpty()) continue;
            double norm_i = norm(vec_i);
            for (Map.Entry<Long, Map<Long, Double>> entry : courseVectors.entrySet()) {
                Long candidateId = entry.getKey();
                if (learnedCourseIds.contains(candidateId)) continue;
                Map<Long, Double> vec_j = entry.getValue();
                double dot = dotProduct(vec_i, vec_j);
                if (dot == 0) continue;
                double sim = dot / (norm_i * norm(vec_j));
                double userWeight = userScoreMap.get(learnedCourseId);
                candidateScores.merge(candidateId, userWeight * sim, Double::sum);
            }
        }

        if (candidateScores.isEmpty()) {
            log.info("[推荐] 用户 {} CF 候选集为空，降级综合排序", userId);
            return getComprehensiveRecommend(limit, learnedCourseIds);
        }

        // 4: 归一化 cfScore
        double maxCf = Collections.max(candidateScores.values());
        if (maxCf > 0) candidateScores.replaceAll((id, score) -> score / maxCf);

        // 5: 查询候选课程实体,排序
        List<Long> candidateIds = new ArrayList<>(candidateScores.keySet());
        List<CourseEntity> candidateCourses = courseMapper.selectList(
                new LambdaQueryWrapper<CourseEntity>()
                        .eq(CourseEntity::getStatus, 2)
                        .eq(CourseEntity::getIsDeleted, 0)
                        .in(CourseEntity::getId, candidateIds));

        List<CourseVO> result = candidateCourses.stream()
                .sorted(Comparator.comparingDouble((CourseEntity c) -> candidateScores.getOrDefault(c.getId(), 0.0)).reversed())
                .limit(limit)
                .map(this::entityToVO)
                .collect(Collectors.toList());

        // 6: 结果不足时用综合排序补齐，排除已展示及已学课程
        if (result.size() < limit) {
            Set<Long> excludeIds = new HashSet<>(learnedCourseIds);
            result.stream().map(CourseVO::getId).forEach(excludeIds::add);
            List<CourseVO> fill = getComprehensiveRecommend(limit - result.size(), excludeIds);
            result.addAll(fill);
        }

        return result;
    }

    /**
     * 综合排序课程（质量优先：avg_score → student_count → create_time）
     * 用于降级和补齐，自动排除 excludeIds 中的课程
     *
     * @param limit      需要返回条数
     * @param excludeIds 需要排除的课程ID（已学/已展示）
     */
    private List<CourseVO> getComprehensiveRecommend(int limit, Set<Long> excludeIds) {
        // 多取一些以应对排除后数量不足
        int fetchSize = limit + (excludeIds.isEmpty() ? 0 : excludeIds.size());
        LambdaQueryWrapper<CourseEntity> wrapper = new LambdaQueryWrapper<CourseEntity>()
                .eq(CourseEntity::getStatus, 2)
                .eq(CourseEntity::getIsDeleted, 0)
                .orderByDesc(CourseEntity::getAvgScore)
                .orderByDesc(CourseEntity::getStudentCount)
                .orderByDesc(CourseEntity::getCreateTime)
                .last("LIMIT " + fetchSize);
        return courseMapper.selectList(wrapper).stream()
                .filter(c -> !excludeIds.contains(c.getId()))
                .limit(limit)
                .map(this::entityToVO)
                .collect(Collectors.toList());
    }

    // ===================== 工具方法 =====================

    /**
     * 计算两个课程向量的点积（只遍历较小向量以优化稀疏计算）
     */
    private double dotProduct(Map<Long, Double> vec_i, Map<Long, Double> vec_j) {
        double dot = 0.0;
        // 遍历较小的向量
        Map<Long, Double> small = vec_i.size() <= vec_j.size() ? vec_i : vec_j;
        Map<Long, Double> large = vec_i.size() <= vec_j.size() ? vec_j : vec_i;
        for (Map.Entry<Long, Double> e : small.entrySet()) {
            Double val = large.get(e.getKey());
            if (val != null) {
                dot += e.getValue() * val;
            }
        }
        return dot;
    }

    /**
     * 计算向量的 L2 范数
     */
    private double norm(Map<Long, Double> vec) {
        double sum = 0.0;
        for (Double v : vec.values()) {
            sum += v * v;
        }
        return Math.sqrt(sum);
    }

    /**
     * CourseEntity → CourseVO（查分类名 + 讲师名）
     */
    private CourseVO entityToVO(CourseEntity entity) {
        CourseVO vo = new CourseVO();
        BeanUtils.copyProperties(entity, vo);

        CourseCategoryEntity category = categoryMapper.selectById(entity.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getCategoryName());
        }

        UserEntity teacher = userMapper.selectById(entity.getTeacherId());
        if (teacher != null) {
            vo.setTeacherName(teacher.getNickname() != null ? teacher.getNickname() : teacher.getUsername());
        }

        return vo;
    }
}
