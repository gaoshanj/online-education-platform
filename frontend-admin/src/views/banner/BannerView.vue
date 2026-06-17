<template>
  <div class="banner-page">
    <!-- 页面头部：标题 + 新增按钮 -->
    <div class="page-header">
      <div class="page-info">
        <h3>轮播图管理</h3>
        <span class="sub-text">共 {{ total }} 条记录</span>
      </div>
      <el-button type="primary" :icon="Plus" @click="openDialog()">新增轮播图</el-button>
    </div>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="tableLoading"
        :data="tableData"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="title" label="标题" min-width="140" show-overflow-tooltip />
        <el-table-column label="图片预览" width="140" align="center">
          <template #default="{ row }">
            <el-image
              :src="row.imageUrl"
              fit="cover"
              style="width: 100px; height: 50px; border-radius: 4px;"
              :preview-src-list="[row.imageUrl]"
              preview-teleported
            >
              <template #error>
                <div class="img-error"><el-icon><Picture /></el-icon></div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" align="center" />
        <el-table-column prop="courseName" label="关联课程" width="110" align="center">
          <template #default="{ row }">
            {{ row.courseName || '—' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              plain
              :icon="Edit"
              @click="openDialog(row)"
            >编辑</el-button>
            <el-button
              v-if="row.status === 1"
              type="warning"
              size="small"
              plain
              style="margin-left: 8px"
              @click="handleToggleStatus(row, 0)"
            >禁用</el-button>
            <el-button
              v-else
              type="success"
              size="small"
              plain
              style="margin-left: 8px"
              @click="handleToggleStatus(row, 1)"
            >启用</el-button>
            <el-button
              type="danger"
              size="small"
              plain
              :icon="Delete"
              style="margin-left: 8px"
              @click="handleDelete(row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[5, 10, 20]"
          layout="total, sizes, prev, pager, next, jumper"
          @change="fetchList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑轮播图' : '新增轮播图'"
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="dialogFormRef"
        :model="dialogForm"
        :rules="dialogRules"
        label-width="100px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="dialogForm.title" placeholder="请输入标题" clearable />
        </el-form-item>
        <el-form-item label="封面图片" prop="imageUrl">
          <el-upload
            class="avatar-uploader"
            action="/api/common/file/upload/image"
            :show-file-list="false"
            :headers="uploadHeaders"
            :on-success="handleUploadSuccess"
            :before-upload="beforeAvatarUpload"
            accept="image/jpeg,image/png,image/gif,image/webp"
          >
            <img v-if="dialogForm.imageUrl" :src="dialogForm.imageUrl" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip">只能上传 jpg/png/gif/webp 文件，且不超过 5MB</div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="关联课程" prop="courseId">
          <div style="display: flex; align-items: center; gap: 10px; width: 100%;">
            <el-input
              v-model="dialogForm.courseName"
              readonly
              placeholder="请选择关联的课程"
              style="flex: 1"
            />
            <el-button type="primary" plain @click="courseSelectionVisible = true">
              选择课程
            </el-button>
            <el-button 
              v-if="dialogForm.courseId" 
              type="danger" 
              plain 
              @click="handleClearCourse"
            >
              清除
            </el-button>
          </div>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number
            v-model="dialogForm.sort"
            :min="0"
            :max="999"
            controls-position="right"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="dialogForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确 定</el-button>
      </template>
    </el-dialog>

    <!-- 选择课程弹窗 -->
    <CourseSelectionDialog
      v-model="courseSelectionVisible"
      @select="handleCourseSelect"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import {
  getBannerPage,
  createBanner,
  updateBanner,
  deleteBanner,
  enableBanner,
  disableBanner
} from '@/api/banner'
import { getCourseDetail } from '@/api/course'
import { useAuthStore } from '@/stores/auth'
import CourseSelectionDialog from './components/CourseSelectionDialog.vue'

const authStore = useAuthStore()
const uploadHeaders = computed(() => {
  return {
    Authorization: `Bearer ${authStore.token}`
  }
})

// ——— 图片上传钩子 ———
const handleUploadSuccess = (res) => {
  if (res.code === 200) {
    dialogForm.imageUrl = res.data
    ElMessage.success('图片上传成功')
    dialogFormRef.value?.validateField('imageUrl')
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('上传图片只能是图片格式!')
  }
  if (!isLt5M) {
    ElMessage.error('上传图片大小不能超过 5MB!')
  }
  return isImage && isLt5M
}

// ——— 列表状态 ———
const tableLoading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

async function fetchList() {
  tableLoading.value = true
  try {
    const res = await getBannerPage(currentPage.value, pageSize.value)
    tableData.value = res.data.records
    total.value = Number(res.data.total)
  } finally {
    tableLoading.value = false
  }
}

onMounted(fetchList)

// ——— 弹窗状态 ———
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const submitLoading = ref(false)
const dialogFormRef = ref(null)

const dialogForm = reactive({
  title: '',
  imageUrl: '',
  sort: 0,
  courseId: undefined,
  courseName: '',
  status: 1
})

const dialogRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  imageUrl: [{ required: true, message: '请上传轮播图', trigger: 'change' }],
  courseId: [{ required: true, message: '请选择关联课程', trigger: 'change' }]
}

function openDialog(row = null) {
  isEdit.value = !!row
  editId.value = row?.id ?? null
  Object.assign(dialogForm, {
    title: row?.title ?? '',
    imageUrl: row?.imageUrl ?? '',
    sort: row?.sort ?? 0,
    courseId: row?.courseId ?? undefined,
    courseName: '',
    status: row?.status ?? 1
  })
  dialogVisible.value = true

  if (row && row.courseId) {
    getCourseDetail(row.courseId).then(res => {
      dialogForm.courseName = res.data.courseName
    }).catch(() => {})
  }
}

// 课程选择
const courseSelectionVisible = ref(false)

const handleCourseSelect = ({ courseId, courseTitle }) => {
  dialogForm.courseId = courseId
  dialogForm.courseName = courseTitle
  dialogFormRef.value?.validateField('courseId')
}

const handleClearCourse = () => {
  dialogForm.courseId = undefined
  dialogForm.courseName = ''
  dialogFormRef.value?.validateField('courseId')
}

async function handleSubmit() {
  const valid = await dialogFormRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    const payload = {
      title: dialogForm.title,
      imageUrl: dialogForm.imageUrl,
      sort: dialogForm.sort,
      courseId: dialogForm.courseId || null,
      status: dialogForm.status
    }
    if (isEdit.value) {
      await updateBanner(editId.value, payload)
      ElMessage.success('修改成功')
    } else {
      await createBanner(payload)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchList()
  } finally {
    submitLoading.value = false
  }
}

// ——— 启用 / 禁用 ———
async function handleToggleStatus(row, status) {
  try {
    if (status === 1) {
      await enableBanner(row.id)
      ElMessage.success('已启用')
    } else {
      await disableBanner(row.id)
      ElMessage.success('已禁用')
    }
    fetchList()
  } catch {
    // 由拦截器弹出错误信息
  }
}

// ——— 删除 ———
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除轮播图「${row.title}」吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }

  try {
    await deleteBanner(row.id)
    ElMessage.success('删除成功')
    // 若当前页删完则回到上一页
    if (tableData.value.length === 1 && currentPage.value > 1) {
      currentPage.value--
    }
    fetchList()
  } catch {
    // 由拦截器弹出错误信息
  }
}
</script>

<style scoped>
.banner-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  padding: 16px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 70, 160, 0.06);
}

.page-info h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1a3a5c;
  margin-bottom: 2px;
}

.sub-text {
  font-size: 12px;
  color: #8fa8c8;
}

.table-card {
  border-radius: 8px;
}

.img-error {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f5ff;
  color: #8fa8c8;
  font-size: 20px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

/* 上传组件样式 */
.avatar-uploader :deep(.el-upload) {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader :deep(.el-upload:hover) {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 280px;
  height: 120px;
  text-align: center;
}

.avatar {
  width: 280px;
  height: 120px;
  display: block;
  object-fit: contain;
}
</style>
