import { useState } from 'react'

function ProfileEditPage({ currentUser, onUpdateProfile, onCancel }) {
  const [nickname, setNickname] = useState(currentUser?.nickname || '')
  const [password, setPassword] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSubmitting(true)
    const ok = await onUpdateProfile({ nickname, password })
    if (ok) {
      onCancel()
    }
    setSubmitting(false)
  }

  return (
    <div className="page-wrap">
      <section className="card form-card">
        <h1>회원 정보 수정</h1>
        <p className="subtitle">닉네임과 비밀번호를 변경할 수 있습니다.</p>

        <form onSubmit={handleSubmit}>
          <div className="input-group">
            <label htmlFor="username">아이디</label>
            <input id="username" value={currentUser?.username || ''} disabled />
          </div>

          <div className="input-group">
            <label htmlFor="nickname">닉네임</label>
            <input id="nickname" value={nickname} onChange={(e) => setNickname(e.target.value)} required />
          </div>

          <div className="input-group">
            <label htmlFor="password">새 비밀번호</label>
            <input id="password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="변경하지 않으면 비워두세요" />
          </div>

          <div className="row">
            <button className="btn btn-primary" type="submit" disabled={submitting}>
              {submitting ? '저장 중...' : '저장하기'}
            </button>
            <button className="btn btn-ghost" type="button" onClick={onCancel}>
              취소
            </button>
          </div>
        </form>
      </section>
    </div>
  )
}

export default ProfileEditPage
