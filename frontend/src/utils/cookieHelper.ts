/**
 * Cookie 工具类
 */
export class CookieHelper {
  /**
   * 获取指定名称的Cookie值
   * @param name Cookie名称
   * @returns Cookie值或null
   */
  static getCookie(name: string): string | null {
    const value = `; ${document.cookie}`
    const parts = value.split(`; ${name}=`)
    if (parts.length === 2) {
      return parts.pop()?.split(';').shift() || null
    }
    return null
  }

  /**
   * 获取JSESSIONID
   * @returns JSESSIONID值或null
   */
  static getSessionId(): string | null {
    return this.getCookie('JSESSIONID')
  }

  /**
   * 检查是否存在有效的session
   * @returns 是否存在session
   */
  static hasValidSession(): boolean {
    const sessionId = this.getSessionId()
    return sessionId !== null && sessionId.length > 0
  }

  /**
   * 获取所有Cookie信息
   * @returns Cookie对象
   */
  static getAllCookies(): Record<string, string> {
    const cookies: Record<string, string> = {}
    document.cookie.split(';').forEach(cookie => {
      const [name, value] = cookie.trim().split('=')
      if (name && value) {
        cookies[name] = value
      }
    })
    return cookies
  }

  /**
   * 打印Cookie调试信息（保留但默认不输出）
   */
  static debugCookies(): void {
    // 可在开发者工具中进行调试
    console.group('🍪 Cookie 调试信息')
    console.log('原始Cookie字符串:', document.cookie)
    console.log('所有Cookie:', this.getAllCookies())
    console.log('JSESSIONID:', this.getSessionId())
    console.log('是否有有效Session:', this.hasValidSession())
    console.groupEnd()
  }
}

// 全局调试函数
(window as any).debugCookies = CookieHelper.debugCookies

// 注意：debugSession函数将在main.ts中注册，因为需要访问userStore
