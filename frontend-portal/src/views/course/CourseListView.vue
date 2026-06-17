<template>
  <div class="course-list-page">
    <div class="page-inner">
      <!-- 左侧分类树 (支持多级分类) -->
      <aside class="sidebar">
        <h4 class="sidebar-title">
          <el-icon><Menu /></el-icon> 课程分类
        </h4>

        <!-- 全部分类项作为一个独立的“根”选项 -->
        <div class="category-item-all" :class="{ active: !selectedCategoryId }" @click="selectCategory(null)">全部课程</div>

        <!-- 树形结构展示多级分类 -->
        <el-tree ref="categoryTreeRef" :data="categories" :props="{ children: 'children', label: 'categoryName' }"
          node-key="id" :current-node-key="selectedCategoryId"
          :expand-on-click-node="false" highlight-current @node-click="handleNodeClick"
          class="category-tree">
          <template #default="{ node, data }">
            <span class="tree-node-label">
              {{ data.categoryName }}
            </span>
          </template>
        </el-tree>
      </aside>

      <!-- 右侧内容 -->
      <main class="content">
        <!-- 筛选工具栏 -->
        <div class="toolbar">
          <div class="sort-tabs">
            <span v-for="opt in sortOptions" :key="opt.value" class="sort-tab"
              :class="{ active: orderBy === opt.value }" @click="changeOrder(opt.value)">{{ opt.label }}</span>
          </div>
          <el-input v-model="keyword" placeholder="搜索课程..." class="toolbar-search" clearable @keyup.enter="handleSearch"
            @clear="handleSearch">
            <template #suffix>
              <el-icon class="search-icon" @click="handleSearch">
                <Search />
              </el-icon>
            </template>
          </el-input>
        </div>

        <!-- 课程卡片网格 -->
        <div v-loading="loading" style="min-height: 300px;">
          <template v-if="courses.length">
            <div class="course-grid">
              <CourseCard v-for="course in courses" :key="course.id" :course="course" />
            </div>
          </template>
          <el-empty v-else-if="!loading" description="暂无课程" :image-size="100" />
        </div>

        <!-- 分页 -->
        <div class="pagination-wrap" v-if="total > 0">
          <span class="total-tip">共 {{ total }} 门课程</span>
          <el-pagination v-model:current-page="currentPage" :page-size="pageSize" :total="total"
            layout="prev, pager, next" background @current-change="fetchCourses" />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search, ArrowDown, ArrowRight, Menu } from '@element-plus/icons-vue'
import { getCategories, getCoursePage } from '@/api/course'
import CourseCard from '@/components/course/CourseCard.vue'

const route = useRoute()
const router = useRouter()

const categories = ref([])
const courses = ref([])
const total = ref(0)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = 12
const selectedCategoryId = ref(route.query.categoryId ? Number(route.query.categoryId) : null)
const categoryTreeRef = ref(null)
const orderBy = ref('')
const keyword = ref(route.query.keyword || '')

const sortOptions = [
  { label: '综合', value: '' },
  { label: '最热', value: 'hot' },
  { label: '最新', value: 'time' }
]

async function fetchCategories() {
  try {
    const res = await getCategories()
    categories.value = res.data || []

    // 当分类数据加载完，如果已经有选中的ID，则自动展开其所在的层级路径
    if (selectedCategoryId.value) {
      nextTick(() => {
        categoryTreeRef.value?.setCurrentKey(selectedCategoryId.value)

        let node = categoryTreeRef.value?.getNode(selectedCategoryId.value)
        while (node && node.parent) {
          node.expanded = true
          node = node.parent
        }
      })
    }
  } catch (e) {
    console.error('分类加载失败', e)
  }
}

async function fetchCourses() {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize,
      orderBy: orderBy.value || undefined,
      categoryId: selectedCategoryId.value || undefined,
      keyword: keyword.value || undefined
    }
    const res = await getCoursePage(params)
    courses.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

// --- 树形分类事件处理 ---
function selectCategory(id) {
  if (selectedCategoryId.value === id) return
  selectedCategoryId.value = id
  currentPage.value = 1
  router.replace({ query: { ...route.query, categoryId: id || undefined } }).catch(() => { })
  fetchCourses()
}

function handleNodeClick(data) {
  selectCategory(data.id)
}

function changeOrder(val) {
  orderBy.value = val
  currentPage.value = 1
  fetchCourses()
}

function handleSearch() {
  currentPage.value = 1
  router.replace({ query: { ...route.query, keyword: keyword.value || undefined } }).catch(() => { })
  fetchCourses()
}

// 监听路由 keyword 参数变化（从导航栏搜索进入）
watch(() => route.query.keyword, val => {
  const newKw = val || ''
  if (keyword.value !== newKw) {
    keyword.value = newKw
    currentPage.value = 1
    fetchCourses()
  }
})

// 监听路由 categoryId 参数变化（从首页点击分类进入）
watch(() => route.query.categoryId, val => {
  const newId = val ? Number(val) : null
  if (selectedCategoryId.value !== newId) {
    selectedCategoryId.value = newId

    // 手动寻找该节点所在父级并展开
    nextTick(() => {
      categoryTreeRef.value?.setCurrentKey(newId)
      let node = categoryTreeRef.value?.getNode(newId)
      while (node && node.parent) {
        node.expanded = true
        node = node.parent
      }
    })

    currentPage.value = 1
    fetchCourses()
  }
})

onMounted(() => {
  fetchCategories()
  fetchCourses()
})
</script>

<style scoped>
.course-list-page {
  min-height: calc(100vh - 60px);
  background: var(--bg-page);
}

.page-inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 24px 24px 40px;
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

/* 左侧分类 */
.sidebar {
  width: 180px;
  flex-shrink: 0;
  background: #ffffff;
  border-radius: 8px;
  padding: 16px 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.sidebar-title {
  margin: 0 0 6px 0;
  font-size: 14px;
  font-weight: 700;
  color: #303133;
  padding: 0 16px 10px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  gap: 8px;
}

.category-item-all {
  padding: 10px 16px;
  font-size: 14px;
  font-weight: normal;
  color: #303133;
  cursor: pointer;
  transition: all 0.15s;
  margin-bottom: 4px;
}

.category-item-all:hover {
  color: #409eff;
  background: #ecf5ff;
}

.category-item-all.active {
  color: #409eff;
  background: #ecf5ff;
}

/* el-tree 视觉优化 */
.category-tree {
  /* 去掉自带边框背景 */
  background: transparent;
  color: #606266;
}

:deep(.el-tree-node__content) {
  height: 36px;
  border-radius: 4px;
  margin: 0 8px;
  transition: background-color 0.2s;
}

:deep(.el-tree-node__content:hover) {
  background-color: #f5f7fa;
}

/* el-tree 高亮被选中的节点 */
:deep(.el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content) {
  background-color: #ecf5ff;
}

:deep(.el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content .tree-node-label) {
  color: #409eff;
  font-weight: 600;
}

.tree-node-label {
  font-size: 13px;
  flex: 1;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}

/* 右侧内容 */
.content {
  flex: 1;
  min-width: 0;
}

.toolbar {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
  background: #fff;
  border-radius: var(--radius);
  padding: 12px 16px;
  box-shadow: var(--shadow);
}

.sort-tabs {
  display: flex;
  gap: 4px;
}

.sort-tab {
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.15s;
}

.sort-tab:hover {
  color: var(--primary);
}

.sort-tab.active {
  background: var(--primary);
  color: #fff;
  font-weight: 600;
}

.toolbar-search {
  width: 250px;
  margin-left: auto;
}

:deep(.toolbar-search .el-input__wrapper) {
  border-radius: 8px;
  display: flex;
}

:deep(.toolbar-search .el-input__suffix-inner) {
  display: flex !important;
  flex-direction: row;
  align-items: center;
}

:deep(.toolbar-search .el-input__suffix-inner > .el-icon.el-input__clear) {
  order: 1;
  margin-right: 4px;
}

:deep(.toolbar-search .search-icon) {
  order: 2;
  cursor: pointer;
  color: var(--primary);
  font-size: 16px;
  font-weight: bold;
}

/* 课程网格 */
.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 18px;
}

.loading-wrap {
  padding: 24px;
  background: #fff;
  border-radius: var(--radius);
}

.pagination-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 32px;
}

.total-tip {
  font-size: 13px;
  color: var(--text-secondary);
  white-space: nowrap;
}
</style>
