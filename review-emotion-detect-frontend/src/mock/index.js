// Mock.js dev-only setup. Disabled in production via .env.production
import Mock from 'mockjs'

export function setupMock() {
  Mock.setup({ timeout: '300-800' })

  // Health (for basic connectivity)
  Mock.mock(/\/api\/health$/, 'get', () => ({
    code: 0,
    message: 'ok',
    data: { service: 'moodlens', time: Date.now() }
  }))

  // Future: mock /api/analyze here to simulate UI without backend.
  // Mock.mock(/\/api\/analyze$/, 'post', (req) => ({ ... }))
}