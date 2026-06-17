<template>
  <el-dialog
    v-model="visible"
    title="选择课程"
    width="800px"
    destroy-on-close
    @close="handleClose"
  >
    <!-- 搜索区域 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="课程名称">
        <el-input v-model="searchForm.courseName" placeholder="请输入课程名称" clearable />
      </el-form-item>
      <el-form-item label="课程分类">
        <el-tree-select
          v-model="searchForm.categoryId"
          :data="categoryOptions"
          :props="defaultProps"
          check-strictly
          placeholder="请选择课程分类"
          clearable
          style="width: 200px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 课程列表表格 -->
    <el-table
      v-loading="loading"
      :data="tableData"
      border
      stripe
      style="width: 100%"
      height="400"
    >
      <el-table-column prop="courseName" label="课程名称" min-width="180" show-overflow-tooltip />
      <el-table-column prop="teacherName" label="讲师" width="120" align="center" show-overflow-tooltip />
      <el-table-column prop="categoryName" label="分类" width="120" show-overflow-tooltip />
      <el-table-column prop="studentCount" label="报名人数" width="100" align="center" />
      <el-table-column prop="avgScore" label="评分" width="80" align="center">
        <template #default="{ row }">
          {{ row.avgScore?.toFixed(1) || '0.0' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" align="center" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleSelect(row)">选择</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @change="fetchList"
      />
    </div>

    <!-- 底部按钮 -->
    <template #footer>
      <el-button @click="visible = false">关 闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { getCategoryList } from '@/api/category'
import { getBannerCoursePage } from '@/api/banner'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'select'])

const visible = ref(props.modelValue)

watch(() => props.modelValue, (newVal) => {
  visible.value = newVal
  if (newVal) {
    if (categoryOptions.value.length === 0) {
      fetchCategoryList()
    }
    fetchList()
  }
})

watch(() => visible.value, (newVal) => {
  emit('update:modelValue', newVal)
})

const handleClose = () => {
  visible.value = false
}

// 搜索表单
const searchForm = reactive({
  courseName: '',
  categoryId: null
})

// 分类树
const categoryOptions = ref([])
const defaultProps = {
  children: 'children',
  label: 'categoryName',
  value: 'id'
}

const fetchCategoryList = async () => {
  try {
    const res = await getCategoryList()
    categoryOptions.value = res.data
  } catch (error) {
    // 错误处理由拦截器完成
  }
}

// 表格数据与分页
const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getBannerCoursePage(
      currentPage.value,
      pageSize.value,
      searchForm.categoryId || null,
      searchForm.courseName || null
    )
    tableData.value = res.data.records
    total.value = Number(res.data.total)
  } catch (error) {
    // 错误处理由拦截器完成
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchList()
}

const resetSearch = () => {
  searchForm.courseName = ''
  searchForm.categoryId = null
  currentPage.value = 1
  fetchList()
}

// 选择课程
const handleSelect = (row) => {
  emit('select', {
    courseId: row.id,
    courseTitle: row.courseName
  })
  visible.value = false
}
</script>

<style scoped>
.search-form {
  margin-bottom: 16px;
}
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
