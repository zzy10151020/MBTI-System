import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/userStore'
import { ElMessage } from 'element-plus'
import HomeView from '../views/HomeView.vue'
import QuestionnaireView from '../views/QuestionnaireView.vue'
import TestView from '../views/TestView.vue'
import ResultsView from '../views/ResultsView.vue'
import UserProfileView from '../views/UserProfileView.vue'
import AdminQuestionnairesView from '../views/AdminQuestionnairesView.vue'
import AdminQuestionsView from '../views/AdminQuestionsView.vue'
import NotFoundView from '../views/NotFoundView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/:uid(\\d+)?',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/questionnaires/:uid(\\d+)?',
      name: 'questionnaires',
      component: QuestionnaireView,
    },
    {
      path: '/test/:uid(\\d+)?',
      name: 'test',
      component: TestView,
    },
    {
      path: '/results/:uid(\\d+)?',
      name: 'results',
      component: ResultsView,
    },
    {
      path: '/profile/:uid(\\d+)?',
      name: 'profile',
      component: UserProfileView,
    },
    {
      path: '/admin/questionnaires/:uid(\\d+)?',
      name: 'admin-questionnaires',
      component: AdminQuestionnairesView,
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/admin/questionnaires/:id/questions/:uid(\\d+)?',
      name: 'admin-questions',
      component: AdminQuestionsView,
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: NotFoundView,
    }
  ],
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  
  // 检查登录状态
  if (!userStore.isLoggedIn) {
    await userStore.initialize()
  }
  
  // 检查是否需要认证
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    ElMessage.error('请先登录')
    next({ name: 'questionnaires' })
    return
  }
  
  // 检查是否需要管理员权限
  if (to.meta.requiresAdmin && userStore.user?.role !== 'ADMIN') {
    ElMessage.error('权限不足，需要管理员权限')
    next({ name: 'questionnaires' })
    return
  }
  
  next()
})

export default router
