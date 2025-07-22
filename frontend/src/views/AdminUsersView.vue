<template>
  <div class="admin-users-container">
    <div class="admin-card">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-content">
          <div class="header-text">
            <h1 class="page-title">用户管理</h1>
            <p class="page-subtitle">管理系统用户信息和权限</p>
          </div>
          <div class="header-actions">
            <el-button type="primary" @click="showCreateDialog = true" style="background-color: var(--primary-teal); border-color: var(--primary-teal); color: #fff;">
              <el-icon><Plus /></el-icon>
              添加用户
            </el-button>
          </div>
        </div>
      </div>

      <!-- 用户列表 -->
      <div class="users-section">
        <div class="section-header">
          <h2 class="section-title">用户列表</h2>
          <div class="section-actions">
            <el-input v-model="searchKeyword" placeholder="搜索用户名/邮箱" clearable @input="handleSearch" style="width: 220px;" />
          </div>
        </div>
        <el-table
          :data="filteredUsers"
          v-loading="loading"
          style="width: 100%"
          row-key="userId"
        >
          <el-table-column prop="userId" label="ID" width="80" />
          <el-table-column prop="username" label="用户名" min-width="120" />
          <el-table-column prop="email" label="邮箱" min-width="180" />
          <el-table-column prop="role" label="角色" width="100">
            <template #default="{ row }">
              <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'info'" disable-transitions>
                {{ row.role === 'ADMIN' ? '管理员' : '普通用户' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="注册时间" min-width="160">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button text type="primary" size="small" @click="editUser(row)">编辑</el-button>
              <el-popconfirm title="确定要删除该用户吗？" @confirm="deleteUser(row.userId)">
                <template #reference>
                  <el-button text type="danger" size="small">删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-section">
          <el-pagination
            background
            layout="prev, pager, next, sizes, total"
            :total="totalItems"
            :page-size="pageSize"
            :current-page="currentPage"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :page-sizes="[10, 20, 50, 100]"
          />
        </div>
      </div>
    </div>

    <!-- 创建用户弹窗 -->
    <el-dialog v-model="showCreateDialog" title="添加新用户" width="500px">
      <el-form ref="createFormRef" :model="createForm" :rules="createRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="createForm.username" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="createForm.email" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="createForm.password" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCreateDialog = false">取消</el-button>
          <el-button type="primary" @click="createUser" style="background-color: var(--primary-teal); border-color: var(--primary-teal); color: #fff;">创建</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑用户弹窗 -->
    <el-dialog v-model="showEditDialog" title="编辑用户" width="500px">
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="editForm.username" disabled />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="editForm.role" placeholder="请选择角色">
            <el-option label="普通用户" value="USER" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showEditDialog = false">取消</el-button>
          <el-button type="primary" @click="editUser()" style="background-color: var(--primary-teal); border-color: var(--primary-teal); color: #fff;">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/userStore'
import userApi from '@/api/user'
import type { User } from '@/api/types'

const userStore = useUserStore()

const users = ref<User[]>([])
const loading = ref(false)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = ref(0)

const showCreateDialog = ref(false)
const showEditDialog = ref(false)
const createFormRef = ref<any>(null)
const editFormRef = ref<any>(null)
const createForm = ref({
  username: '',
  email: '',
  password: ''
})
const editForm = ref<{
  userId: number
  username: string
  email: string
  role: 'ADMIN' | 'USER'
}>({
  userId: 0,
  username: '',
  email: '',
  role: 'USER'
})

const createRules: Record<string, any[]> = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 32, message: '用户名长度应在2-32个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度应在6-32个字符', trigger: 'blur' }
  ],
}
const editRules: Record<string, any[]> = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await userStore.getUserList()
    users.value = res || []
    totalItems.value = users.value.length
  } catch (e) {
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(fetchUsers)

const filteredUsers = computed(() => {
  let filtered = users.value
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(u =>
      u.username.toLowerCase().includes(keyword) ||
      u.email.toLowerCase().includes(keyword)
    )
  }
  totalItems.value = filtered.length
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filtered.slice(start, end)
})

const handleSearch = () => {
  currentPage.value = 1
}
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
}
const handleCurrentChange = (page: number) => {
  currentPage.value = page
}

const formatDate = (date: string) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

const createUser = async () => {
  await createFormRef.value?.validate()
  loading.value = true
  try {
    await userStore.register(
      createForm.value.username,
      createForm.value.password,
      createForm.value.email
    )
    ElMessage.success('用户创建成功')
    showCreateDialog.value = false
    createForm.value = { username: '', email: '', password: '' }
    fetchUsers()
  } catch (e: any) {
    ElMessage.error(e.message || '创建用户失败')
  } finally {
    loading.value = false
  }
}

const editUser = async (user?: User) => {
  if (user) {
    // 只保留可编辑字段
    editForm.value.userId = user.userId
    editForm.value.username = user.username
    editForm.value.email = user.email
    editForm.value.role = user.role
    showEditDialog.value = true
    return
  }
  // 保存编辑，只传递被允许修改的字段
  loading.value = true
  try {
    // 只提交被更改的字段
    const updateData: Record<string, any> = {}
    const originalUser = users.value.find(u => u.userId === editForm.value.userId)
    if (editForm.value.email !== undefined && editForm.value.email !== originalUser?.email) {
      updateData.email = editForm.value.email
    }
    if (editForm.value.role !== undefined && editForm.value.role !== originalUser?.role) {
      updateData.role = editForm.value.role
    }
    if (Object.keys(updateData).length === 0) {
      ElMessage.info('未做任何更改')
      return
    }
    const response = await userStore.updateUserById(editForm.value.userId, updateData)
    if (response) {
      ElMessage.success('用户信息已更新')
    }
    showEditDialog.value = false
    fetchUsers()
  } catch (e: any) {
    ElMessage.error(e.message || '更新用户失败')
  } finally {
    loading.value = false
  }
}

const deleteUser = async (userId: number) => {
  loading.value = true
  try {
    await userStore.deleteUser(userId)
    ElMessage.success('用户已删除')
    fetchUsers()
  } catch (e: any) {
    ElMessage.error(e.message || '删除用户失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.admin-users-container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 2rem;
  background-color: var(--color-background-soft);
}

.admin-card {
  max-width: 120rem;
  width: 100%;
  background-color: var(--color-background);
  border-radius: 1.6rem;
  box-shadow: 0 0.8rem 3.2rem rgba(0, 0, 0, 0.1);
  padding: 3rem;
  border: 1px solid var(--color-border);
}

.page-header {
  margin-bottom: 3rem;
  border-bottom: 1px solid var(--color-border);
  padding-bottom: 2rem;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 2rem;
}

.page-title {
  font-size: 2.8rem;
  color: var(--color-text-primary);
  margin: 0 0 0.5rem 0;
  font-weight: 600;
}

.page-subtitle {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: 1.4rem;
}

.header-actions {
  flex-shrink: 0;
}

.users-section {
  margin-top: 3rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.section-title {
  font-size: 2rem;
  color: var(--color-text-primary);
  margin: 0;
}

.section-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.pagination-section {
  margin-top: 2rem;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .admin-users-container {
    padding: 1rem;
  }
  .admin-card {
    padding: 2rem;
  }
  .header-content {
    flex-direction: column;
    align-items: flex-start;
  }
  .section-header {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }
}
</style>
