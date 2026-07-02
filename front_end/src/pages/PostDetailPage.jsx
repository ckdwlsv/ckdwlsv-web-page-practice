import { useMemo, useState } from 'react'

function PostDetailPage({ post, comments, currentUser, postReaction, onNavigate, onCreateComment, onDeleteComment, onToggleReaction, onToggleCommentReaction, onDeletePost }) {
  const [commentText, setCommentText] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const canEditPost = currentUser && post && (currentUser.role === 'ADMIN' || currentUser.nickname === post.authorName)
  const commentList = useMemo(() => comments || [], [comments])

  const handleSubmitComment = async (event) => {
    event.preventDefault()
    if (!commentText.trim()) return

    setSubmitting(true)
    const success = await onCreateComment(commentText.trim())
    setSubmitting(false)
    if (success) {
      setCommentText('')
    }
  }

  if (!post) {
    return (
      <div className="page-wrap">
        <section className="card">
          <h1>게시글 상세</h1>
          <p>게시글 정보를 불러오는 중입니다.</p>
        </section>
      </div>
    )
  }

  return (
    <div className="page-wrap">
      <section className="card">
        <h1>게시글 상세</h1>

        <p><strong>제목 :</strong> {post.title}</p>
        <p><strong>작성자 :</strong> {post.authorName}</p>
        <p><strong>내용 :</strong></p>
        <p>{post.content}</p>

        {post.images && post.images.length > 0 ? (
          <div className="image-stack">
            {post.images.map((image) => (
              <img key={image.id} src={`/images/${image.id}`} alt="첨부 이미지" className="post-image" />
            ))}
          </div>
        ) : null}

        <div className="reaction-row">
          <button className="btn btn-ghost" type="button" onClick={() => currentUser && onToggleReaction('LIKE')}>
            좋아요
          </button>
          <span>{postReaction?.likeCount ?? 0}</span>
          <button className="btn btn-ghost" type="button" onClick={() => currentUser && onToggleReaction('DISLIKE')}>
            싫어요
          </button>
          <span>{postReaction?.dislikeCount ?? 0}</span>
        </div>

        {canEditPost ? (
          <div className="row">
            <button className="btn btn-primary" type="button" onClick={() => onNavigate('edit', post.id)}>
              수정
            </button>
            <button className="btn btn-ghost" type="button" onClick={() => onDeletePost(post.id)}>
              삭제
            </button>
          </div>
        ) : null}

        <h2>댓글</h2>
        {commentList.length === 0 ? (
          <p>아직 댓글이 없습니다.</p>
        ) : (
          <ul className="list-stack">
            {commentList.map((comment) => (
              <li key={comment.id}>
                <strong>{comment.nickname}</strong> : {comment.content}
                <small>{comment.createdAt}</small>
                <div className="row">
                  <button className="btn btn-ghost" type="button" onClick={() => currentUser && onToggleCommentReaction(comment.id, 'LIKE')}>
                    👍 {comment.likeCount ?? 0}
                  </button>
                  <button className="btn btn-ghost" type="button" onClick={() => currentUser && onToggleCommentReaction(comment.id, 'DISLIKE')}>
                    👎 {comment.dislikeCount ?? 0}
                  </button>
                </div>
                {currentUser && (currentUser.id === comment.userId || currentUser.role === 'ADMIN') ? (
                  <button className="btn btn-ghost" type="button" onClick={() => onDeleteComment(comment.id)}>
                    삭제
                  </button>
                ) : null}
              </li>
            ))}
          </ul>
        )}

        {currentUser ? (
          <form onSubmit={handleSubmitComment} className="section-block">
            <h3>댓글 작성</h3>
            <textarea rows="3" value={commentText} onChange={(event) => setCommentText(event.target.value)} placeholder="댓글을 입력하세요..." />
            <div className="row">
              <button className="btn btn-primary" type="submit" disabled={submitting}>
                {submitting ? '등록 중...' : '등록'}
              </button>
            </div>
          </form>
        ) : (
          <p>댓글과 👍는 로그인해 주세요.</p>
        )}

        <div className="row">
          <button className="btn btn-ghost" type="button" onClick={() => onNavigate('posts')}>
            목록으로
          </button>
        </div>
      </section>
    </div>
  )
}

export default PostDetailPage
