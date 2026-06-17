<template>
  <div class="category-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="page-info">
        <h3>课程分类管理</h3>
        <span class="sub-text">支持树形层级管理</span>
      </div>
      <el-button type="primary" :icon="Plus" @click="openDialog(null)">新增顶级分类</el-button>
    </div>

    <!-- 数据表格 (树形展示) -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="tableLoading"
        :data="tableData"
        row-key="id"
        border
        default-expand-all
        :tree-props="{ children: 'children' }"
        style="width: 100%"
      >
        <el-table-column prop="categoryName" label="分类名称" min-width="180" />
        <!-- <el-table-column prop="icon" label="图标" width="100" align="center">
          <template #default="{ row }">
            <el-image
              v-if="row.icon"
              :src="row.icon"
              style="width: 30px; height: 30px; border-radius: 4px;"
            >
              <template #error>
                <el-icon><Picture /></el-icon>
              </template>
            </el-image>
            <span v-else>—</span>
          </template>
        </el-table-column> -->
        <!-- <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip /> -->
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column prop="createTime" label="创建时间" width="170" align="center" />
        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              plain
              :icon="Plus"
              @click="openDialog(row, true)"
            >新增子分类</el-button>
            <el-button
              type="primary"
              size="small"
              plain
              :icon="Edit"
              @click="openDialog(row, false)"
            >编辑</el-button>
            <el-button
              type="danger"
              size="small"
              plain
              :icon="Delete"
              @click="handleDelete(row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="dialogFormRef"
        :model="dialogForm"
        :rules="dialogRules"
        label-width="100px"
      >
        <el-form-item label="父级分类" v-if="dialogForm.parentId !== 0">
          <el-input v-model="parentCategoryName" disabled />
        </el-form-item>
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="dialogForm.categoryName" placeholder="请输入分类名称" clearable />
        </el-form-item>
        <!-- <el-form-item label="分类描述" prop="description">
          <el-input
            v-model="dialogForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述信息"
            clearable
          />
        </el-form-item> -->
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number
            v-model="dialogForm.sortOrder"
            :min="1"
            :max="999"
            controls-position="right"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Picture } from '@element-plus/icons-vue'
import { getCategoryList, createCategory, updateCategory, deleteCategory } from '@/api/category'

// ——— 列表状态 ———
const tableLoading = ref(false)
const tableData = ref([])

async function fetchList() {
  tableLoading.value = true
  try {
    const res = await getCategoryList()
    tableData.value = res.data || []
  } finally {
    tableLoading.value = false
  }
}

onMounted(fetchList)

// ——— 弹窗状态 ———
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const dialogFormRef = ref(null)
const parentCategoryName = ref('')

// 用于判断当前操作的类型，并计算标题
const dialogTitle = computed(() => {
  if (isEdit.value) return '编辑分类'
  if (dialogForm.parentId === 0) return '新增顶级分类'
  return '新增子分类'
})

const dialogForm = reactive({
  id: undefined,
  parentId: 0,
  categoryName: '',
  description: '',
  icon: '',
  sortOrder: 1
})

const dialogRules = {
  categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

/**
 * 打开弹窗
 * @param {Object} row 当前行数据
 * @param {Boolean} isAddChild 是否是新增子分类动作
 */
function openDialog(row = null, isAddChild = false) {
  if (row && isAddChild) {
    // 新增子分类
    isEdit.value = false
    parentCategoryName.value = row.categoryName
    Object.assign(dialogForm, {
      id: undefined,
      parentId: row.id,
      categoryName: '',
      description: '',
      icon: '',
      sortOrder: 1
    })
  } else if (row && !isAddChild) {
    // 编辑当前分类
    isEdit.value = true
    parentCategoryName.value = '' // 编辑时不显示父级（或需要从列表反查，这里简单处理）
    Object.assign(dialogForm, {
      id: row.id,
      parentId: row.parentId,
      categoryName: row.categoryName,
      description: row.description || '',
      icon: row.icon || '',
      sortOrder: row.sortOrder
    })
  } else {
    // 新增顶级分类
    isEdit.value = false
    parentCategoryName.value = ''
    Object.assign(dialogForm, {
      id: undefined,
      parentId: 0,
      categoryName: '',
      description: '',
      icon: '',
      sortOrder: 1
    })
  }
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await dialogFormRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    const payload = { ...dialogForm }
    if (isEdit.value) {
      await updateCategory(payload)
      ElMessage.success('修改成功')
    } else {
      await createCategory(payload)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchList()
  } finally {
    submitLoading.value = false
  }
}

// ——— 删除 ———
async function handleDelete(row) {
  const hasChildren = row.children && row.children.length > 0
  const confirmMsg = hasChildren 
    ? `该分类「${row.categoryName}」包含子分类，删除将一并移除，是否继续？`
    : `确定删除分类「${row.categoryName}」吗？`

  try {
    await ElMessageBox.confirm(confirmMsg, '危险操作', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'error'
    })
  } catch {
    return
  }

  try {
    await deleteCategory(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch {
    // 由拦截器弹出错误信息
  }
}
</script>

<style scoped>
.category-page {
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
</style>
