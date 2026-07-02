import { useState } from 'react'

function PostEditPage({ post, onNavigate, onUpdatePost }) {
  const [form, setForm] = useState({ title: post?.title || '', content: post?.content || '' })
  const [submitting, setSubmitting] = useState(false)

  const handleSubmit = async (event) => {
    event.preventDefault()
    setSubmitting(true)
    const success = await onUpdatePost(post.id, {
      title: form.title,
      content: form.content,
    })
    setSubmitting(false)
    if (!success) {
      setForm({ title: post?.title || '', content: post?.content || '' })
    }
  }

  if (!post) {
    return (
      <div className="page-wrap">
        <section className="card">
          <h1>글 수정</h1>
          <p>게시글 정보를 불러오는 중입니다.</p>
        </section>
      </div>
    )
  }

  return (
    <div className="page-wrap">
      <section className="card">
        <h1>글 수정</h1>

        <form onSubmit={handleSubmit}>
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

          {post.images && post.images.length > 0 ? (
            <div className="section-block">
              <p>현재 이미지:</p>
              {post.images.map((image) => (
                <div key={image.id} className="image-item">
                  <img src={`/images/${image.id}`} alt="첨부 이미지" className="post-image" />
                  <label>
                    <input type="checkbox" name="deleteImageIds" value={image.id} /> 삭제
                  </label>
                  <input type="file" name="replaceImageFiles" accept="image/*" />
                </div>
              ))}
            </div>
          ) : null}

          <div className="input-group">
            <label htmlFor="newImages">새 이미지 추가</label>
            <input id="newImages" type="file" name="newImages" multiple accept="image/*" />
          </div>

          <div className="row">
            <button className="btn btn-primary" type="submit" disabled={submitting}>
              {submitting ? '수정 중...' : '수정 완료'}
            </button>
            <button className="btn btn-ghost" type="button" onClick={() => onNavigate('detail', post.id)}>
              취소
            </button>
          </div>
        </form>
      </section>
    </div>
  )
}

export default PostEditPage
