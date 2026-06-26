import { useState } from 'react'

function LoginPage({ onLogin, onNavigate }) {
  const [form, setForm] = useState({ username: '', password: '' })
  const [error, setError] = useState('')

  const handleSubmit = async (event) => {
    event.preventDefault()

    if (!form.username.trim() || !form.password.trim()) {
      setError('아이디와 비밀번호를 모두 입력해주세요.')
      return
    }

    try {
      await onLogin(form.username, form.password)
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div className="page-wrap">
      <section className="card form-card">
        <h1>로그인</h1>

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
          <button className="btn btn-primary" type="submit">
            로그인
          </button>
        </form>

        <p className="extra-link">
          계정이 없으신가요?{' '}
          <button className="link-button" type="button" onClick={() => onNavigate('signup')}>
            회원가입
          </button>
        </p>
      </section>
    </div>
  )
}

export default LoginPage
