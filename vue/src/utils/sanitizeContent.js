import DOMPurify from 'dompurify'

function renderMarkdown(text) {
  if (!text) return ''
  const normalized = text.replace(/\r\n/g, '\n').replace(/\r/g, '\n')
  return normalized.split('\n').map((line) => {
    if (line.startsWith('### ')) return '<h3>' + line.slice(4) + '</h3>'
    if (line.startsWith('## ')) return '<h2>' + line.slice(3) + '</h2>'
    if (line.startsWith('# ')) return '<h1>' + line.slice(2) + '</h1>'
    const imgMatch = line.match(/^!\[([^\]]*)\]\(([^)]+)\)$/)
    if (imgMatch) return '<div class="content-image-wrap"><img src="' + imgMatch[2] + '" alt="' + imgMatch[1] + '" class="content-image" loading="lazy" /></div>'
    if (line.trim() === '') return '<br/>'
    return '<p>' + line + '</p>'
  }).join('')
}

export function renderSafeContent(text) {
  return DOMPurify.sanitize(renderMarkdown(text), {
    ALLOWED_TAGS: ['h1', 'h2', 'h3', 'p', 'br', 'div', 'img'],
    ALLOWED_ATTR: ['src', 'alt', 'class', 'loading'],
    ALLOW_DATA_ATTR: false,
  })
}
