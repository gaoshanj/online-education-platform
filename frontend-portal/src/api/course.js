import request from '@/utils/request'

/** 获取所有分类（树形结构） */
export function getCategories() {
    return request.get('/app/category/list')
}

/** 获取首页轮播图列表 */
export function getBanners() {
    return request.get('/app/banner/list')
}

/**
 * 分页查询课程
 * @param {Object} params - { page, size, categoryId, keyword, orderBy }
 */
export function getCoursePage(params) {
    return request.get('/app/course/page', { params })
}

/** 获取课程详情 */
export function getCourseDetail(id) {
    return request.get(`/app/course/detail/${id}`)
}

/** 获取课程章节列表（树形结构） */
export function getChapterList(courseId) {
    return request.get(`/app/chapter/${courseId}/list`)
}

/** 报名课程 */
export function enrollCourse(courseId) {
    return request.post(`/app/course/enroll/${courseId}`)
}

/** 热门课程推荐 */
export function getHotRecommend(params) {
    return request.get('/app/recommend/hot', { params })
}

/** 最新上线推荐 */
export function getLatestRecommend(params) {
    return request.get('/app/recommend/latest', { params })
}

/** 精品课程推荐 */
export function getFeaturedRecommend(params) {
    return request.get('/app/recommend/featured', { params })
}

/** 个性化为你推荐 */
export function getPersonalRecommend(params) {
    return request.get('/app/recommend/personal', { params })
}

/** 点赞/取消点赞课程 */
export function toggleCourseLike(courseId) {
    return request.post(`/app/course-like/toggle/${courseId}`)
}

/** 收藏/取消收藏课程 */
export function toggleCourseFavorite(courseId) {
    return request.post(`/app/course-favorite/toggle/${courseId}`)
}

/** 
 * 保存学习进度 
 * @param {Object} data - { chapterId, courseId, duration, lastPosition }
 */
export function saveProgress(data) {
    return request.post('/app/course/progress', data)
}

/** 获取最近学习的一门课程 */
export function getRecentLearning() {
    return request.get('/app/course/recent-learning')
}

/** 
 * 获取我的学习课程列表（分页）
 * @param {Object} params - { page, size }
 */
export function getMyLearningPage(params) {
    return request.get('/app/course/my-learning', { params })
}

/** 
 * 获取我的收藏课程列表（分页）
 * @param {Object} params - { page, size }
 */
export function getMyFavoritesPage(params) {
    return request.get('/app/course-favorite/my', { params })
}

/** 
 * 获取我的点赞课程列表（分页）
 * @param {Object} params - { page, size }
 */
export function getMyLikesPage(params) {
    return request.get('/app/course-like/my', { params })
}

// ---------------- 课程评价相关接口 ----------------

/**
 * 提交课程评价
 * @param {Object} data - { courseId, rating, content }
 */
export function submitCourseReview(data) {
    return request.post('/app/course/review', data)
}

/**
 * 获取课程评价列表
 * @param {Object} params - { courseId, page, size, rating, sortType, mine }
 */
export function getCourseReviewList(params) {
    return request.get('/app/course/review/list', { params })
}

/**
 * 获取课程评分统计
 * @param {Number} courseId
 */
export function getCourseReviewStat(courseId) {
    return request.get(`/app/course/review/stat/${courseId}`)
}

/**
 * 修改课程评价
 * @param {Number} id - 评价ID
 * @param {Object} data - { courseId, rating, content }
 */
export function updateCourseReview(id, data) {
    return request.put(`/app/course/review/${id}`, data)
}

/**
 * 删除课程评价
 * @param {Number} id - 评价ID
 */
export function deleteCourseReview(id) {
    return request.delete(`/app/course/review/${id}`)
}

/**
 * 点赞或取消点赞评价
 * @param {Number} id - 评价ID
 */
export function toggleCourseReviewLike(id) {
    return request.post(`/app/course/review/${id}/like`)
}
