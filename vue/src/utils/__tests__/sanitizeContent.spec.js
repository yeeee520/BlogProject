import { describe, expect, it } from 'vitest'
import { renderSafeContent } from '@/utils/sanitizeContent'

describe('renderSafeContent', () => {
  it('removes script tags and event attributes', () => {
    const html = renderSafeContent('<script>alert(1)</script>\n<img src=x onerror=alert(2)>')

    expect(html).not.toContain('<script')
    expect(html).not.toContain('onerror')
  })

  it('removes javascript URLs from markdown images', () => {
    const html = renderSafeContent('![test](javascript:evil)')

    expect(html).not.toMatch(/src=["']javascript:/i)
  })

  it('keeps allowed headings and https images', () => {
    const html = renderSafeContent('# 标题\n![photo](https://example.com/photo.jpg)')

    expect(html).toContain('<h1>标题</h1>')
    expect(html).toContain('https://example.com/photo.jpg')
  })
})
