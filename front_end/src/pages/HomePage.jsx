function HomePage({ loggedIn, user, onNavigate, onLogout }) {
  return (
    <div className="page-wrap">
      <section className="card">
        <h1>홈페이지에 오신 것을 환영합니다.</h1>
        <p className="subtitle">
          오늘의 생각과 일상을 게시판에 남기고, 다른 사람의 이야기도 함께 나눠보세요.
        </p>

        {loggedIn ? (
          <>
            <p>안녕하세요, {user.nickname}님</p>
            <div className="row">
              <button className="btn btn-primary" type="button" onClick={() => onNavigate('mypage')}>
                마이페이지
              </button>
              <button className="btn btn-ghost" type="button" onClick={() => onNavigate('posts')}>
                게시판 바로가기
              </button>
              <button className="btn btn-ghost" type="button" onClick={onLogout}>
                로그아웃
              </button>
            </div>
          </>
        ) : (
          <div className="row">
            <button className="btn btn-primary" type="button" onClick={() => onNavigate('login')}>
              로그인
            </button>
            <button className="btn btn-ghost" type="button" onClick={() => onNavigate('signup')}>
              회원가입
            </button>
            <button className="btn btn-ghost" type="button" onClick={() => onNavigate('posts')}>
              게시판 둘러보기
            </button>
          </div>
        )}
      </section>
    </div>
  )
}

export default HomePage
