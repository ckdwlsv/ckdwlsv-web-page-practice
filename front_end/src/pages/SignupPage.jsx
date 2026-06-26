import { useState } from 'react'

function SignupPage({ onNavigate, onSignup }) {
  const [form, setForm] = useState({ username: '', password: '', nickname: '' })
  const [error, setError] = useState('')

  const handleSubmit = async (event) => {
    event.preventDefault()

    if (!form.username.trim() || !form.password.trim() || !form.nickname.trim()) {
      setError('모든 항목을 입력해주세요.')
      return
    }

    try {
      await onSignup(form.username, form.password, form.nickname)
      setError('')
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div className="page-wrap">
      <section className="card form-card">
        <h1>회원가입</h1>

        {error ? (
          <div className="error">
            <p>{error}</p>
          </div>
        ) : null}

        <form onSubmit={handleSubmit}>
          <div className="input-group">
            <label htmlFor="username">아이디</label>
            <input
              id="username"
              type="text"
              name="username"
              value={form.username}
              onChange={(event) => setForm({ ...form, username: event.target.value })}
              placeholder="아이디를 입력하세요"
            />
          </div>
          <div className="input-group">
            <label htmlFor="password">비밀번호</label>
            <input
              id="password"
              type="password"
              name="password"
              value={form.password}
              onChange={(event) => setForm({ ...form, password: event.target.value })}
              placeholder="비밀번호를 입력하세요"
            />
          </div>
          <div className="input-group">
            <label htmlFor="nickname">닉네임</label>
            <input
              id="nickname"
              type="text"
              name="nickname"
              value={form.nickname}
              onChange={(event) => setForm({ ...form, nickname: event.target.value })}
              placeholder="닉네임을 입력하세요"
            />
          </div>
          <button className="btn btn-primary" type="submit">
            회원가입
          </button>
        </form>

        <p className="extra-link">
          이미 계정이 있나요?{' '}
          <button className="link-button" type="button" onClick={() => onNavigate('login')}>
            로그인
          </button>
        </p>
      </section>
    </div>
  )
}

export default SignupPage
