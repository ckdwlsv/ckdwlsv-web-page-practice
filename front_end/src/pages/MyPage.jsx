function MyPage({ profile, myPosts, myComments, onNavigate }) {
  return (
    <div className="page-wrap">
      <section className="card">
        <h1>마이페이지</h1>

        <div className="section-block">
          <h2>프로필</h2>
          <p>아이디: {profile.username}</p>
          <p>닉네임: {profile.nickname}</p>
          <p>권한: {profile.role}</p>
        </div>

        <div className="section-block">
          <h2>내가 쓴 글</h2>
          <ul className="list-stack">
            {myPosts.map((post) => (
              <li key={post.id}>
                <button className="link-button" type="button" onClick={() => onNavigate('detail', post.id)}>
                  {post.title}
                </button>
                {' '}
                ({post.likeCount} 👍, {post.dislikeCount} 👎)
              </li>
            ))}
          </ul>
        </div>

        <div className="section-block">
          <h2>내가 쓴 댓글</h2>
          <ul className="list-stack">
            {myComments.map((comment) => (
              <li key={comment.id}>
                <button className="link-button" type="button" onClick={() => onNavigate('detail', comment.postId)}>
                  게시글
                </button>
                : {comment.content} ({comment.likeCount} 👍, {comment.dislikeCount} 👎)
              </li>
            ))}
          </ul>
        </div>

        <div className="row">
          <button className="btn btn-ghost" type="button" onClick={() => onNavigate('home')}>
            홈으로
          </button>
        </div>
      </section>
    </div>
  )
}

export default MyPage
