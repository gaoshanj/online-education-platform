<template>
  <div class="user-page">
    <!-- 顶部过滤 -->
    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="filter-form">
        <el-form-item label="用户名">
          <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查 询</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重 置</el-button>
        </el-form-item>
        <el-form-item class="filter-actions">
          <el-button type="primary" :icon="Plus" @click="openAddDialog">新增用户</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 主体表格 -->
    <el-card class="table-card" shadow="never">
      <el-table v-loading="tableLoading" :data="tableData" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="头像" width="100" align="center">
          <template #default="{ row }">
            <el-avatar :size="40" :src="row.avatar" :icon="UserFilled" />
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" min-width="80" align="center" show-overflow-tooltip  />
        <!-- <el-table-column prop="nickname" label="昵称" min-width="110" show-overflow-tooltip /> -->
        <el-table-column label="角色" min-width="100" align="center">
          <template #default="{ row }">
            <div class="role-tags">
              <el-tag v-for="role in row.roles" :key="role" size="small"
                :type="role === 'ADMIN' ? 'danger' : (role === 'TEACHER' ? 'success' : 'info')">
                {{ role }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <!-- <el-table-column prop="email" label="邮箱" width="130" align="center" /> -->
        <!-- <el-table-column label="性别" width="70" align="center">
          <template #default="{ row }">
            <span v-if="row.gender === 1">男</span>
            <span v-else-if="row.gender === 2">女</span>
            <span v-else>保密</span>
          </template>
        </el-table-column> -->

        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <!-- 绑定1为正常，0为禁用，防抖以及变更前确认 -->
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              :loading="row.statusLoading"
              :before-change="() => beforeStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="170" align="center" />
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDetail(row.id)">详情</el-button>
            <el-button type="warning" link size="small" @click="openRoleDialog(row)">修改角色</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @change="fetchList"
        />
      </div>
    </el-card>

    <!-- 用户详情信息对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="用户详情"
      width="600px"
      destroy-on-close
    >
      <div v-loading="detailLoading" class="detail-wrap">
        <el-descriptions v-if="currentDetail" :column="2" border>
          <el-descriptions-item label="用户名">{{ currentDetail.username }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{ currentDetail.nickname }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ currentDetail.email || '无' }}</el-descriptions-item>
          <el-descriptions-item label="性别">
            {{ currentDetail.gender === 1 ? '男' : (currentDetail.gender === 2 ? '女' : '保密') }}
          </el-descriptions-item>
          <el-descriptions-item label="账号状态">
            <el-tag :type="currentDetail.status === 1 ? 'success' : 'danger'" size="small">
              {{ currentDetail.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="角色集合" :span="2">
            <el-tag
              v-for="role in currentDetail.roles"
              :key="role"
              size="small"
              class="detail-role-tag"
            >
              {{ role }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="注册时间" :span="2">{{ currentDetail.createTime }}</el-descriptions-item>
          <el-descriptions-item label="最后更新" :span="2">{{ currentDetail.updateTime }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关 闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 新增用户对话框 -->
    <el-dialog
      v-model="addDialogVisible"
      title="新增用户"
      width="500px"
      @close="handleAddClose"
      destroy-on-close
    >
      <el-form
        ref="addFormRef"
        :model="addForm"
        :rules="addRules"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="addForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="addForm.nickname" placeholder="请输入昵称" clearable />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="addForm.password" placeholder="请输入密码" show-password clearable />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="addForm.email" placeholder="请输入邮箱" clearable />
        </el-form-item>
        <el-form-item label="角色" prop="roleCodes">
          <el-select v-model="addForm.roleCodes" multiple placeholder="请选择角色至少一项" style="width: 100%">
            <el-option label="管理员(ADMIN)" value="ADMIN" />
            <el-option label="讲师(TEACHER)" value="TEACHER" />
            <el-option label="学生(STUDENT)" value="STUDENT" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addDialogVisible = false">取 消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitAdd">确 定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 修改角色对话框 -->
    <el-dialog
      v-model="roleDialogVisible"
      title="修改角色"
      width="400px"
      @close="handleRoleClose"
      destroy-on-close
    >
      <el-form
        ref="roleFormRef"
        :model="roleForm"
        :rules="roleRules"
        label-width="80px"
      >
        <el-form-item label="用户名">
          <el-input :model-value="roleForm.username" disabled />
        </el-form-item>
        <el-form-item label="角色" prop="roleCodes">
          <el-select v-model="roleForm.roleCodes" multiple placeholder="请选择角色至少一项" style="width: 100%">
            <el-option label="管理员(ADMIN)" value="ADMIN" />
            <el-option label="讲师(TEACHER)" value="TEACHER" />
            <el-option label="学生(STUDENT)" value="STUDENT" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="roleDialogVisible = false">取 消</el-button>
          <el-button type="primary" :loading="roleSubmitLoading" @click="submitRole">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, UserFilled, Plus } from '@element-plus/icons-vue'
import {
  getUserPage,
  getUserDetail,
  enableUser,
  disableUser,
  createUser,
  updateUserRoles,
  deleteUser
} from '@/api/user'

// ——— 查询与列表 ———
const queryParams = reactive({
  username: '',
  status: undefined,
  page: 1,
  size: 10
})

const tableLoading = ref(false)
const tableData = ref([])
const total = ref(0)

async function fetchList() {
  tableLoading.value = true
  try {
    const res = await getUserPage(queryParams)
    // 为表格数据增加独立开关 loading 状态
    tableData.value = res.data.records.map(item => ({
      ...item,
      statusLoading: false
    }))
    total.value = Number(res.data.total)
  } finally {
    tableLoading.value = false
  }
}

function handleSearch() {
  queryParams.page = 1
  fetchList()
}

function resetQuery() {
  queryParams.username = ''
  queryParams.status = undefined
  handleSearch()
}

onMounted(() => {
  fetchList()
})

// ——— 启禁状态切换 ———
const beforeStatusChange = (row) => {
  return new Promise((resolve, reject) => {
    const targetStatus = row.status === 1 ? 0 : 1
    const actionText = targetStatus === 1 ? '启用' : '禁用'

    ElMessageBox.confirm(`确定要${actionText}用户「${row.username}」吗？`, '二次确认', {
      type: 'warning'
    }).then(async () => {
      row.statusLoading = true
      try {
        if (targetStatus === 1) {
          await enableUser(row.id)
        } else {
          await disableUser(row.id)
        }
        ElMessage.success(`已${actionText}`)
        resolve(true)
      } catch (err) {
        reject(new Error('操作失败'))
      } finally {
        row.statusLoading = false
      }
    }).catch(() => {
      reject(new Error('取消操作'))
    })
  })
}

// ——— 新增用户 ———
const addDialogVisible = ref(false)
const submitLoading = ref(false)
const addFormRef = ref(null)

const addForm = reactive({
  username: '',
  nickname: '',
  password: '',
  email: '',
  roleCodes: []
})

const addRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: ['blur', 'change'] }
  ],
  roleCodes: [{ required: true, message: '请选择至少一个角色', trigger: 'change' }]
}

function openAddDialog() {
  addDialogVisible.value = true
}

function handleAddClose() {
  if (addFormRef.value) {
    addFormRef.value.resetFields()
  }
}

async function submitAdd() {
  if (!addFormRef.value) return
  await addFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        await createUser(addForm)
        ElMessage.success('新增用户成功')
        addDialogVisible.value = false
        queryParams.page = 1
        fetchList()
      } catch (error) {
        console.error('新增用户失败:', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// ——— 修改角色 ———
const roleDialogVisible = ref(false)
const roleSubmitLoading = ref(false)
const roleFormRef = ref(null)

const roleForm = reactive({
  id: undefined,
  username: '',
  roleCodes: []
})

const roleRules = {
  roleCodes: [{ required: true, message: '请选择至少一个角色', trigger: 'change' }]
}

function openRoleDialog(row) {
  roleForm.id = row.id
  roleForm.username = row.username
  // 假定 row.roles 是返回的 ['ADMIN', 'STUDENT'] 类似的数组
  roleForm.roleCodes = row.roles ? [...row.roles] : []
  roleDialogVisible.value = true
}

function handleRoleClose() {
  if (roleFormRef.value) {
    roleFormRef.value.resetFields()
  }
}

async function submitRole() {
  if (!roleFormRef.value) return
  await roleFormRef.value.validate(async (valid) => {
    if (valid) {
      roleSubmitLoading.value = true
      try {
        await updateUserRoles(roleForm.id, { roleCodes: roleForm.roleCodes })
        ElMessage.success('修改角色成功')
        roleDialogVisible.value = false
        // 更新当前表格中的角色数据，也可以选择重新请求 fetchList()
        const targetRow = tableData.value.find(item => item.id === roleForm.id)
        if (targetRow) {
          targetRow.roles = [...roleForm.roleCodes]
        }
      } catch (error) {
        console.error('修改角色失败:', error)
      } finally {
        roleSubmitLoading.value = false
      }
    }
  })
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要彻底删除用户「${row.username}」的所有数据吗？此操作不可恢复！`, '危险操作', {
      confirmButtonText: '彻底删除',
      cancelButtonText: '取消',
      type: 'error'
    })
  } catch {
    return
  }

  try {
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    if (tableData.value.length === 1 && queryParams.page > 1) {
      queryParams.page--
    }
    fetchList()
  } catch {}
}

// ——— 详情对话框 ———
const dialogVisible = ref(false)
const detailLoading = ref(false)
const currentDetail = ref(null)

async function openDetail(id) {
  dialogVisible.value = true
  detailLoading.value = true
  try {
    const res = await getUserDetail(id)
    currentDetail.value = res.data
  } catch {
    currentDetail.value = null
  } finally {
    detailLoading.value = false
  }
}
</script>

<style scoped>
.user-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.filter-card, .table-card {
  border-radius: 8px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
}

.filter-form .el-form-item {
  margin-bottom: 0;
  margin-right: 24px;
}

.filter-actions {
  margin-left: auto;
  margin-right: 0 !important;
}

.role-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.detail-wrap {
  min-height: 150px;
}

.detail-role-tag {
  margin-right: 8px;
  margin-bottom: 4px;
}
</style>
