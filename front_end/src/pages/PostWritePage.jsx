import { useState } from 'react'

function PostWritePage({ onNavigate, onCreatePost }) {
  const [form, setForm] = useState({ title: '', content: '' })
  const [submitting, setSubmitting] = useState(false)
  const [message, setMessage] = useState('')

  const handleSubmit = async (event) => {
    event.preventDefault()

    if (!form.title.trim() || !form.content.trim()) {
      setMessage('제목과 내용을 입력해주세요.')
      return
    }

    const formData = new FormData()
    formData.append('title', form.title)
    formData.append('content', form.content)

    const fileInput = event.currentTarget.elements.files
    if (fileInput?.files) {
      Array.from(fileInput.files).forEach((file) => formData.append('files', file))
    }

    setSubmitting(true)
    const success = await onCreatePost(formData)
    setSubmitting(false)

    if (success) {
      setForm({ title: '', content: '' })
      setMessage('')
    }
  }

  return (
    <div className="page-wrap">
      <section className="card">
        <h1>글 쓰기</h1>

        <form onSubmit={handleSubmit}>
          {message ? <p className="error">{message}</p> : null}

          <div className="input-group">
            <label htmlFor="title">제목</label>
            <input
              id="title"
              type="text"
              name="title"
              value={form.title}
              onChange={(event) => setForm({ ...form, title: event.target.value })}
              required
            />
          </div>

          <div className="input-group">
            <label htmlFor="content">내용</label>
            <textarea
              id="content"
              name="content"
              rows="10"
              value={form.content}
              onChange={(event) => setForm({ ...form, content: event.target.value })}
              required
            />
          </div>

          <div className="input-group">
            <label htmlFor="files">이미지 첨부</label>
            <input id="files" type="file" name="files" multiple />
          </div>

          <div className="row">
            <button className="btn btn-primary" type="submit" disabled={submitting}>
              {submitting ? '등록 중...' : '작성 완료'}
            </button>
            <button className="btn btn-ghost" type="button" onClick={() => onNavigate('posts')}>
              목록으로
            </button>
          </div>
        </form>
      </section>
    </div>
  )
}

export default PostWritePage
