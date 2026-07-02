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
import { createPost, getMe, getMyPageData, getPostDetail, getPosts, loginUser, logoutUser, signupUser, createComment, deleteComment, togglePostReaction, toggleCommentReaction, updatePost, deletePost, updateMyProfile } from './api'

function App() {
  const [loggedIn, setLoggedIn] = useState(false)
  const [currentPage, setCurrentPage] = useState('home')
  const [selectedPostId, setSelectedPostId] = useState(1)
  const [currentUser, setCurrentUser] = useState(null)
  const [posts, setPosts] = useState([])
  const [postPage, setPostPage] = useState({ page: 0, totalPages: 1, hasNext: false, hasPrevious: false })
  const [selectedPost, setSelectedPost] = useState(null)
  const [comments, setComments] = useState([])
  const [postReaction, setPostReaction] = useState({ likeCount: 0, dislikeCount: 0, myReaction: null })
  const [mypageData, setMypageData] = useState({ profile: null, myPosts: [], myComments: [] })
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const refreshPosts = async (page = 0) => {
    try {
      const postData = await getPosts(page)
      setPosts(postData.content || [])
      setPostPage({
        page: postData.page ?? 0,
        totalPages: postData.totalPages ?? 1,
        hasNext: Boolean(postData.hasNext),
        hasPrevious: Boolean(postData.hasPrevious),
      })
    } catch (err) {
      setError(err.message)
    }
  }

  useEffect(() => {
    async function bootstrap() {
      try {
        const me = await getMe()
        if (me.loggedIn) {
          setLoggedIn(true)
          setCurrentUser(me.user)
        } else {
          setLoggedIn(false)
          setCurrentUser(null)
        }
        await refreshPosts()
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
    } catch (err) {
      // 서버 응답이 실패해도 로컬 인증 상태는 비운다.
    } finally {
      setLoggedIn(false)
      setCurrentUser(null)
      setCurrentPage((prev) => (prev === 'mypage' ? 'home' : prev))
      setError('')
    }
  }

  const onCreatePost = async (formData) => {
    try {
      await createPost(formData)
      await refreshPosts()
      setCurrentPage('posts')
      setError('')
      return true
    } catch (err) {
      setError(err.message)
      return false
    }
  }

  const currentPost = useMemo(
    () => selectedPost ?? posts.find((post) => post.id === selectedPostId) ?? posts[0],
    [posts, selectedPost, selectedPostId],
  )

  useEffect(() => {
    if (currentPage !== 'detail' || !selectedPostId) return

    async function loadDetail() {
      try {
        const data = await getPostDetail(selectedPostId)
        setSelectedPost(data.post)
        setComments(data.comments || [])
        setPostReaction(data.postReaction || { likeCount: 0, dislikeCount: 0, myReaction: null })
      } catch (err) {
        setError(err.message)
      }
    }

    loadDetail()
  }, [currentPage, selectedPostId])

  const onCreateComment = async (content) => {
    try {
      const createdComment = await createComment(selectedPostId, content)
      setComments((prev) => [...prev, createdComment])
      setError('')
      return true
    } catch (err) {
      setError(err.message)
      return false
    }
  }

  const onDeleteComment = async (commentId) => {
    try {
      await deleteComment(commentId)
      setComments((prev) => prev.filter((comment) => comment.id !== commentId))
      setError('')
      return true
    } catch (err) {
      setError(err.message)
      return false
    }
  }

  const onToggleReaction = async (type) => {
    try {
      const summary = await togglePostReaction(selectedPostId, type)
      setPostReaction({
        likeCount: summary.likeCount,
        dislikeCount: summary.dislikeCount,
        myReaction: summary.myReaction,
      })
      setError('')
      return true
    } catch (err) {
      setError(err.message)
      return false
    }
  }

  const onToggleCommentReaction = async (commentId, type) => {
    try {
      const summary = await toggleCommentReaction(commentId, type)
      setComments((prev) => prev.map((comment) => (comment.id === commentId ? {
        ...comment,
        likeCount: summary.likeCount,
        dislikeCount: summary.dislikeCount,
        myReaction: summary.myReaction,
      } : comment)))
      setError('')
      return true
    } catch (err) {
      setError(err.message)
      return false
    }
  }

  const onUpdatePost = async (postId, payload) => {
    try {
      const updated = await updatePost(postId, payload)
      setSelectedPost(updated.post || updated)
      setCurrentPage('detail')
      setError('')
      return true
    } catch (err) {
      setError(err.message)
      return false
    }
  }

  const onDeletePost = async (postId) => {
    try {
      await deletePost(postId)
      await refreshPosts()
      setCurrentPage('posts')
      setError('')
      return true
    } catch (err) {
      setError(err.message)
      return false
    }
  }

  const onUpdateProfile = async (payload) => {
    try {
      const result = await updateMyProfile(payload)
      setCurrentUser(result.user)
      setMypageData((prev) => ({ ...prev, profile: prev.profile ? { ...prev.profile, nickname: result.user.nickname } : prev.profile }))
      setError('')
      return true
    } catch (err) {
      setError(err.message)
      return false
    }
  }

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
            currentUser={currentUser}
            onNavigate={navigateTo}
            onUpdateProfile={onUpdateProfile}
          />
        )
      case 'posts':
        return <PostListPage posts={posts} pageInfo={postPage} onNavigate={navigateTo} onPageChange={refreshPosts} />
      case 'write':
        return <PostWritePage onNavigate={navigateTo} onCreatePost={onCreatePost} />
      case 'detail':
        return (
          <PostDetailPage
            post={currentPost}
            comments={comments}
            currentUser={currentUser}
            postReaction={postReaction}
            onNavigate={navigateTo}
            onCreateComment={onCreateComment}
            onDeleteComment={onDeleteComment}
            onToggleReaction={onToggleReaction}
            onToggleCommentReaction={onToggleCommentReaction}
            onDeletePost={onDeletePost}
          />
        )
      case 'edit':
        return <PostEditPage post={currentPost} onNavigate={navigateTo} onUpdatePost={onUpdatePost} />
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
