// src/api/modules/test.js
import {testHttp} from '@/api/http'

// get Questionnaire
export async function getQuestionnaire(mode) {
    try {
      const res = await testHttp.get('/emotion/questionnaire', { params: { mode } })
      console.log('[getQuestionnaire] mode:', mode, '=> response:', res)
      return res
    } catch (err) {
      console.error('[getQuestionnaire] failed:', err)
      throw err
    }
  }

// fetch feedback by level (1-4). Some backends expect level, others l — we pass both.
export async function getLevelFeedback(level) {
    try {
      const res = await testHttp.get('/emotion/level-feedback', { params: { level } })
      console.log('[getLevelFeedback] level:', level, '=> response:', res)
      return res
    } catch (err) {
      console.error('[getLevelFeedback] failed:', err)
      throw err
    }
  }