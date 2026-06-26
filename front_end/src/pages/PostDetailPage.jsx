import { useMemo } from 'react'

function PostDetailPage({ post, comments, currentUser, onNavigate }) {
  const canEditPost = currentUser && currentUser.nickname === post.authorName

  const commentList = useMemo(() => comments, [comments])

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
          <button className="btn btn-ghost" type="button">좋아요</button>
          <span>{post.likeCount}</span>
          <button className="btn btn-ghost" type="button">싫어요</button>
          <span>{post.dislikeCount}</span>
        </div>

        {canEditPost ? (
          <div className="row">
            <button className="btn btn-primary" type="button" onClick={() => onNavigate('edit', post.id)}>
              수정
            </button>
            <button className="btn btn-ghost" type="button">
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
                {currentUser && (currentUser.nickname === comment.authorName || currentUser.role === 'ADMIN') ? (
                  <button className="btn btn-ghost" type="button">
                    삭제
                  </button>
                ) : null}
              </li>
            ))}
          </ul>
        )}

        {currentUser ? (
          <div className="section-block">
            <h3>댓글 작성</h3>
            <textarea rows="3" placeholder="댓글을 입력하세요..." />
            <div className="row">
              <button className="btn btn-primary" type="button">
                등록
              </button>
            </div>
          </div>
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
