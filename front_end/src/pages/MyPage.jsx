import { useState } from 'react'
import ProfileEditPage from './ProfileEditPage'

function MyPage({ profile, myPosts, myComments, currentUser, onNavigate, onUpdateProfile }) {
  const safeProfile = profile || { username: '-', nickname: '-', role: '-' }
  const safePosts = Array.isArray(myPosts) ? myPosts : []
  const safeComments = Array.isArray(myComments) ? myComments : []
  const [editingProfile, setEditingProfile] = useState(false)

  if (editingProfile) {
    return (
      <ProfileEditPage
        currentUser={currentUser}
        onUpdateProfile={onUpdateProfile}
        onCancel={() => setEditingProfile(false)}
      />
    )
  }

  return (
    <div className="page-wrap">
      <section className="card">
        <h1>마이페이지</h1>

        <div className="section-block">
          <h2>프로필</h2>
          <p>아이디: {safeProfile.username}</p>
          <p>닉네임: {safeProfile.nickname}</p>
          <p>권한: {safeProfile.role}</p>
          <div className="row">
            <button className="btn btn-primary" type="button" onClick={() => setEditingProfile(true)}>
              정보 수정
            </button>
          </div>
        </div>

        <div className="section-block">
          <h2>내가 쓴 글</h2>
          {safePosts.length === 0 ? (
            <p>작성한 글이 없습니다.</p>
          ) : (
            <ul className="list-stack">
              {safePosts.map((post) => (
                <li key={post.id}>
                  <button className="link-button" type="button" onClick={() => onNavigate('detail', post.id)}>
                    {post.title}
                  </button>
                  {' '}
                  ({post.likeCount} 👍, {post.dislikeCount} 👎)
                </li>
              ))}
            </ul>
          )}
        </div>

        <div className="section-block">
          <h2>내가 쓴 댓글</h2>
          {safeComments.length === 0 ? (
            <p>작성한 댓글이 없습니다.</p>
          ) : (
            <ul className="list-stack">
              {safeComments.map((comment) => (
                <li key={comment.id}>
                  <button className="link-button" type="button" onClick={() => onNavigate('detail', comment.postId)}>
                    게시글
                  </button>
                  : {comment.content} ({comment.likeCount} 👍, {comment.dislikeCount} 👎)
                </li>
              ))}
            </ul>
          )}
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
