<template>
  <div class="profile-container">
    <div class="profile-card">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-content">
          <div class="user-avatar-section">
            <el-avatar :size="80" class="profile-avatar">
              {{ userInitial }}
            </el-avatar>
            <div class="user-info">
              <h1 class="username">{{ userStore.user?.username }}</h1>
              <p class="user-role">{{ userStore.user?.role === 'ADMIN' ? '管理员' : '普通用户' }}</p>
              <p class="join-date">加入时间：{{ formatDate(userStore.user?.createdAt) }}</p>
            </div>
          </div>
          <div class="profile-actions">
            <el-button type="primary" @click="editProfile">
              编辑资料
            </el-button>
            <el-button @click="goToResults">
              查看测试结果
            </el-button>
          </div>
        </div>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-section">
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><DocumentChecked /></el-icon>
          </div>
          <div class="stat-content">
            <h3>{{ userStore.user?.answerCount || 0 }}</h3>
            <p>完成测试</p>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><Calendar /></el-icon>
          </div>
          <div class="stat-content">
            <h3>{{ daysSinceJoin }}</h3>
            <p>使用天数</p>
          </div>
        </div>

        <div class="stat-card" v-if="testStore.latestTestResult">
          <div class="stat-icon mbti-icon">
            <span>{{ testStore.latestTestResult.mbtiType }}</span>
          </div>
          <div class="stat-content">
            <h3>最新类型</h3>
            <p>{{ formatDate(testStore.latestTestResult.submittedAt) }}</p>
          </div>
        </div>

        <div class="stat-card" v-if="userStore.user?.role === 'ADMIN'">
          <div class="stat-icon">
            <el-icon><Management /></el-icon>
          </div>
          <div class="stat-content">
            <h3>管理面板</h3>
            <p>
              <el-button text type="primary" @click="goToAdmin">
                查看统计
              </el-button>
            </p>
          </div>
        </div>
      </div>

      <!-- 快速操作 -->
      <div class="actions-section">
        <h2 class="section-title">快速操作</h2>
        <div class="action-grid">
          <div class="action-item" @click="goToQuestionnaires">
            <div class="action-icon">
              <el-icon><Edit /></el-icon>
            </div>
            <h3>开始测试</h3>
            <p>选择问卷开始新的MBTI测试</p>
          </div>
          
          <div class="action-item" @click="goToResults">
            <div class="action-icon">
              <el-icon><Document /></el-icon>
            </div>
            <h3>查看结果</h3>
            <p>查看历史测试结果和详细报告</p>
          </div>
          
          <div class="action-item" @click="exportData">
            <div class="action-icon">
              <el-icon><Download /></el-icon>
            </div>
            <h3>导出数据</h3>
            <p>导出个人测试数据和报告</p>
          </div>
        </div>
      </div>

      <!-- 管理员统计面板 -->
      <div v-if="userStore.user?.role === 'ADMIN'" class="admin-section">
        <h2 class="section-title">管理员统计</h2>
        <div class="admin-stats" v-if="testStore.statistics">
          <div class="admin-stat-card">
            <h3>{{ testStore.statistics.totalAnswers }}</h3>
            <p>总测试次数</p>
          </div>
          <div class="admin-stat-card">
            <h3>{{ testStore.statistics.totalQuestionnaires }}</h3>
            <p>问卷总数</p>
          </div>
          <div class="admin-stat-card">
            <h3>{{ testStore.statistics.publishedQuestionnaires }}</h3>
            <p>已发布问卷</p>
          </div>
        </div>
        
        <!-- MBTI类型分布图表 -->
        <div class="chart-section" v-if="testStore.statistics">
          <h3>MBTI类型分布</h3>
          <div class="mbti-distribution">
            <div 
              v-for="(count, type) in testStore.statistics.mbtiDistribution" 
              :key="type"
              class="mbti-item"
            >
              <div class="mbti-type">{{ type }}</div>
              <div class="mbti-bar">
                <div 
                  class="mbti-fill"
                  :style="{ width: `${(count / Math.max(...Object.values(testStore.statistics.mbtiDistribution))) * 100}%` }"
                ></div>
              </div>
              <div class="mbti-count">{{ count }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑资料弹窗 -->
    <el-dialog
      v-model="showEditDialog"
      title="编辑个人资料"
      width="500px"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="80px"
      >
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" disabled />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email" type="email" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showEditDialog = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="saveProfile"
            :loading="saving"
          >
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  DocumentChecked,
  Calendar,
  Management,
  Edit,
  Document,
  Download
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/userStore'
import { useTestStore } from '@/stores/testStore'
import { userApi } from '@/api'
import type { UpdateProfileRequest } from '@/api/types'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const testStore = useTestStore()

// 响应式状态
const showEditDialog = ref(false)
const saving = ref(false)
const editFormRef = ref<any>(null)

// 编辑表单
const editForm = ref({
  username: '',
  email: ''
})

// 表单验证规则
const editRules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email' as const, message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ]
}

// 计算属性
const userInitial = computed(() => {
  return userStore.user?.username ? userStore.user.username.charAt(0).toUpperCase() : 'U'
})

const currentUid = computed(() => {
  return route.params.uid || userStore.user?.userId?.toString()
})

const daysSinceJoin = computed(() => {
  if (!userStore.user?.createdAt) return 0
  const joinDate = new Date(userStore.user.createdAt)
  const now = new Date()
  const diffTime = Math.abs(now.getTime() - joinDate.getTime())
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24))
})

// 格式化日期
const formatDate = (dateString: string | undefined) => {
  if (!dateString) return '未知'
  return new Date(dateString).toLocaleDateString('zh-CN')
}

// 生命周期
onMounted(async () => {
  // 获取最新用户信息
  await userStore.fetchUserProfile()
  
  // 获取测试结果
  await testStore.fetchTestResults({ page: 0, size: 1 })
  
  // 如果是管理员，获取统计信息
  if (userStore.user?.role === 'ADMIN') {
    await testStore.fetchTestStatistics()
  }
})

// 事件处理
const editProfile = () => {
  if (userStore.user) {
    editForm.value = {
      username: userStore.user.username,
      email: userStore.user.email
    }
  }
  showEditDialog.value = true
}

const saveProfile = async () => {
  try {
    await editFormRef.value?.validate()
    
    saving.value = true
    
    const updateData: UpdateProfileRequest = {
      email: editForm.value.email
    }
    
    const updatedUser = await userApi.updateProfile(updateData)
    userStore.user = updatedUser
    
    ElMessage.success('个人资料更新成功')
    showEditDialog.value = false
  } catch (error: any) {
    console.error('更新个人资料失败:', error)
    ElMessage.error(error.message || '更新失败')
  } finally {
    saving.value = false
  }
}

const goToQuestionnaires = () => {
  if (currentUid.value) {
    router.push({ name: 'questionnaires', params: { uid: currentUid.value } })
  } else {
    router.push({ name: 'questionnaires' })
  }
}

const goToResults = () => {
  if (currentUid.value) {
    router.push({ name: 'results', params: { uid: currentUid.value } })
  } else {
    router.push({ name: 'results' })
  }
}

const goToAdmin = () => {
  if (currentUid.value) {
    router.push({ name: 'admin-questionnaires', params: { uid: currentUid.value } })
  } else {
    router.push({ name: 'admin-questionnaires' })
  }
}

const exportData = async () => {
  try {
    ElMessageBox.confirm(
      '是否导出您的所有测试数据和报告？',
      '导出确认',
      {
        confirmButtonText: '导出',
        cancelButtonText: '取消',
        type: 'info'
      }
    ).then(async () => {
      // 获取所有测试结果
      await testStore.fetchTestResults({ page: 0, size: 1000 })
      
      // 生成导出数据
      const exportData = {
        user: userStore.user,
        testResults: testStore.testResults,
        exportTime: new Date().toISOString()
      }
      
      // 创建下载链接
      const blob = new Blob([JSON.stringify(exportData, null, 2)], { 
        type: 'application/json' 
      })
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `MBTI_Data_${userStore.user?.username}_${new Date().toISOString().split('T')[0]}.json`
      link.click()
      URL.revokeObjectURL(url)
      
      ElMessage.success('数据导出成功')
    })
  } catch (error) {
    console.log('用户取消导出')
  }
}
</script>

<style scoped>
.profile-container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 2rem;
  background-color: var(--color-background-soft);
}

.profile-card {
  max-width: 90rem;
  width: 100%;
  background-color: var(--color-background);
  border-radius: 1.6rem;
  box-shadow: 0 0.8rem 3.2rem rgba(0, 0, 0, 0.1);
  padding: 4rem;
  border: 1px solid var(--color-border);
}

/* 页面头部 */
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

.user-avatar-section {
  display: flex;
  align-items: center;
  gap: 2rem;
}

.profile-avatar {
  background-color: var(--primary-teal) !important;
  color: #ffffff !important;
  font-weight: bold;
  font-size: 2.4rem !important;
}

.user-info h1 {
  margin: 0 0 0.5rem 0;
  font-size: 2.4rem;
  color: var(--color-text-primary);
}

.user-role {
  margin: 0 0 0.5rem 0;
  color: var(--primary-teal);
  font-weight: 600;
}

.join-date {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: 1.3rem;
}

.profile-actions {
  display: flex;
  gap: 1rem;
  flex-shrink: 0;
}

/* 统计卡片 */
.stats-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(20rem, 1fr));
  gap: 2rem;
  margin-bottom: 3rem;
}

.stat-card {
  background-color: var(--color-background-soft);
  border-radius: 1.2rem;
  padding: 2rem;
  display: flex;
  align-items: center;
  gap: 1.5rem;
  border: 1px solid var(--color-border);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-0.2rem);
  box-shadow: 0 0.4rem 1.2rem rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 5rem;
  height: 5rem;
  border-radius: 1rem;
  background-color: var(--primary-teal-light);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon .el-icon {
  font-size: 2rem;
  color: var(--primary-teal);
}

.mbti-icon {
  background-color: var(--primary-teal);
  color: #ffffff;
  font-weight: bold;
  font-size: 1.6rem;
}

.stat-content h3 {
  margin: 0 0 0.5rem 0;
  font-size: 2rem;
  color: var(--color-text-primary);
}

.stat-content p {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: 1.3rem;
}

/* 快速操作 */
.actions-section {
  margin-bottom: 3rem;
}

.section-title {
  font-size: 2rem;
  color: var(--color-text-primary);
  margin-bottom: 2rem;
  border-bottom: 2px solid var(--primary-teal);
  padding-bottom: 0.5rem;
  display: inline-block;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(25rem, 1fr));
  gap: 2rem;
}

.action-item {
  background-color: var(--color-background-soft);
  border-radius: 1.2rem;
  padding: 2.5rem;
  text-align: center;
  cursor: pointer;
  border: 1px solid var(--color-border);
  transition: all 0.3s ease;
}

.action-item:hover {
  transform: translateY(-0.3rem);
  box-shadow: 0 0.6rem 2rem rgba(32, 178, 170, 0.2);
  border-color: var(--primary-teal);
}

.action-icon {
  width: 6rem;
  height: 6rem;
  border-radius: 50%;
  background-color: var(--primary-teal-light);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1.5rem;
}

.action-icon .el-icon {
  font-size: 2.5rem;
  color: var(--primary-teal);
}

.action-item h3 {
  margin: 0 0 1rem 0;
  font-size: 1.8rem;
  color: var(--color-text-primary);
}

.action-item p {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: 1.3rem;
  line-height: 1.5;
}

/* 管理员统计 */
.admin-section {
  border-top: 1px solid var(--color-border);
  padding-top: 3rem;
}

.admin-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(15rem, 1fr));
  gap: 2rem;
  margin-bottom: 3rem;
}

.admin-stat-card {
  background-color: var(--primary-teal-light);
  border-radius: 1rem;
  padding: 2rem;
  text-align: center;
  border: 1px solid var(--primary-teal);
}

.admin-stat-card h3 {
  margin: 0 0 0.5rem 0;
  font-size: 2.4rem;
  color: var(--primary-teal);
  font-weight: bold;
}

.admin-stat-card p {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: 1.3rem;
}

/* MBTI分布图表 */
.chart-section h3 {
  margin-bottom: 2rem;
  color: var(--color-text-primary);
}

.mbti-distribution {
  display: grid;
  gap: 1rem;
}

.mbti-item {
  display: grid;
  grid-template-columns: 4rem 1fr 4rem;
  gap: 1rem;
  align-items: center;
  padding: 1rem;
  background-color: var(--color-background-soft);
  border-radius: 0.8rem;
}

.mbti-type {
  font-weight: bold;
  color: var(--primary-teal);
  text-align: center;
}

.mbti-bar {
  height: 2rem;
  background-color: var(--color-border);
  border-radius: 1rem;
  overflow: hidden;
}

.mbti-fill {
  height: 100%;
  background-color: var(--primary-teal);
  transition: width 0.3s ease;
}

.mbti-count {
  text-align: center;
  font-weight: 600;
  color: var(--color-text-primary);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-container {
    padding: 1rem;
  }
  
  .profile-card {
    padding: 2rem;
  }
  
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 1.5rem;
  }
  
  .user-avatar-section {
    flex-direction: column;
    align-items: center;
    text-align: center;
    gap: 1rem;
  }
  
  .profile-actions {
    align-self: stretch;
  }
  
  .stats-section {
    grid-template-columns: 1fr;
  }
  
  .action-grid {
    grid-template-columns: 1fr;
  }
  
  .admin-stats {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .profile-card {
    padding: 1.5rem;
  }
  
  .user-info h1 {
    font-size: 2rem;
  }
  
  .admin-stats {
    grid-template-columns: 1fr;
  }
  
  .mbti-item {
    grid-template-columns: 3rem 1fr 3rem;
    gap: 0.5rem;
    font-size: 1.2rem;
  }
}
</style>
