import { useEffect, useMemo, useState } from 'react'
import './App.css'
import HomePage from './pages/HomePage'
import LoginPage from './pages/LoginPage'
import SignupPage from './pages/SignupPage'
import MyPage from './pages/MyPage'
import PostListPage from './pages/PostListPage'
import PostWritePage from './pages/PostWritePage'
import PostDetailPage from './pages/PostDetailPage'
import PostEditPage from './pages/PostEditPage'
import { getMe, getMyPageData, getPosts, loginUser, logoutUser, signupUser } from './api'

function App() {
  const [loggedIn, setLoggedIn] = useState(false)
  const [currentPage, setCurrentPage] = useState('home')
  const [selectedPostId, setSelectedPostId] = useState(1)
  const [currentUser, setCurrentUser] = useState(null)
  const [posts, setPosts] = useState([])
  const [mypageData, setMypageData] = useState({ profile: null, myPosts: [], myComments: [] })
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    async function bootstrap() {
      try {
        const me = await getMe()
        if (me.loggedIn) {
          setLoggedIn(true)
          setCurrentUser(me.user)
        }
        const postData = await getPosts()
        setPosts(postData)
      } catch (err) {
        setError(err.message)
      } finally {
        setLoading(false)
      }
    }

    bootstrap()
  }, [])

  useEffect(() => {
    if (!loggedIn || currentPage !== 'mypage') return

    async function loadMypage() {
      try {
        const data = await getMyPageData()
        setMypageData(data)
      } catch (err) {
        setError(err.message)
      }
    }

    loadMypage()
  }, [loggedIn, currentPage])

  const navigateTo = (page, id = null) => {
    setCurrentPage(page)
    if (id !== null) {
      setSelectedPostId(id)
    }
  }

  const onLogin = async (username, password) => {
    try {
      const user = await loginUser({ username, password })
      setLoggedIn(true)
      setCurrentUser(user)
      setCurrentPage('home')
      setError('')
    } catch (err) {
      setError(err.message)
    }
  }

  const onSignup = async (username, password, nickname) => {
    try {
      await signupUser({ username, password, nickname })
      setCurrentPage('login')
      setError('')
    } catch (err) {
      setError(err.message)
    }
  }

  const onLogout = async () => {
    try {
      await logoutUser()
      setLoggedIn(false)
      setCurrentUser(null)
      setCurrentPage('home')
      setError('')
    } catch (err) {
      setError(err.message)
    }
  }

  const selectedPost = useMemo(
    () => posts.find((post) => post.id === selectedPostId) ?? posts[0],
    [posts, selectedPostId],
  )

  const renderPage = () => {
    if (loading) {
      return <div className="page-wrap"><section className="card"><h1>불러오는 중...</h1></section></div>
    }

    switch (currentPage) {
      case 'login':
        return <LoginPage onLogin={onLogin} onNavigate={navigateTo} />
      case 'signup':
        return <SignupPage onNavigate={navigateTo} onSignup={onSignup} />
      case 'mypage':
        return (
          <MyPage
            profile={mypageData.profile}
            myPosts={mypageData.myPosts}
            myComments={mypageData.myComments}
            onNavigate={navigateTo}
          />
        )
      case 'posts':
        return <PostListPage posts={posts} onNavigate={navigateTo} />
      case 'write':
        return <PostWritePage onNavigate={navigateTo} />
      case 'detail':
        return (
          <PostDetailPage
            post={selectedPost}
            comments={[]}
            currentUser={currentUser}
            onNavigate={navigateTo}
          />
        )
      case 'edit':
        return <PostEditPage post={selectedPost} onNavigate={navigateTo} />
      case 'home':
      default:
        return (
          <HomePage
            loggedIn={loggedIn}
            user={currentUser}
            onNavigate={navigateTo}
            onLogout={onLogout}
          />
        )
    }
  }

  return (
    <>
      {error ? <div className="error global-error">{error}</div> : null}
      {renderPage()}
    </>
  )
}

export default App
