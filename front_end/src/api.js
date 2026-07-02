const API_BASE_URL = '/api'

function getAuthHeaders(options = {}) {
  const token = localStorage.getItem('jwtToken')
  const isFormData = typeof FormData !== 'undefined' && options.body instanceof FormData
  const headers = {
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...(!isFormData ? { 'Content-Type': 'application/json' } : {}),
    ...(options.headers || {}),
  }

  return headers
}

export async function apiRequest(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    credentials: 'include',
    headers: getAuthHeaders(options),
    ...options,
  })

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}))
    throw new Error(errorData.error || '요청에 실패했습니다.')
  }

  return response.json().catch(() => null)
}

export function getMe() {
  return apiRequest('/me')
}

export async function loginUser(payload) {
  const response = await fetch(`${API_BASE_URL}/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}))
    throw new Error(errorData.error || '로그인에 실패했습니다.')
  }

  const data = await response.json()
  if (data.token) {
    localStorage.setItem('jwtToken', data.token)
  }
  return data.user
}

export function signupUser(payload) {
  return apiRequest('/signup', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export async function logoutUser() {
  const token = localStorage.getItem('jwtToken')
  localStorage.removeItem('jwtToken')

  try {
    await fetch(`${API_BASE_URL}/logout`, {
      method: 'POST',
      credentials: 'include',
      headers: token ? { Authorization: `Bearer ${token}` } : {},
    })
  } catch (error) {
    // 서버가 내려가더라도 프런트 상태는 비워둔다.
  }

  return { ok: true }
}

export function getPosts(page = 0) {
  return apiRequest(`/posts?page=${page}`)
}

export async function createPost(formData) {
  const response = await fetch(`${API_BASE_URL}/posts`, {
    method: 'POST',
    credentials: 'include',
    headers: getAuthHeaders({ body: formData }),
    body: formData,
  })

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}))
    throw new Error(errorData.error || '게시글 작성에 실패했습니다.')
  }

  return response.json().catch(() => ({ message: '게시글이 등록되었습니다.' }))
}

export function getMyPageData() {
  return apiRequest('/mypage')
}

export async function updateMyProfile(payload) {
  const response = await fetch(`${API_BASE_URL}/mypage`, {
    method: 'PUT',
    credentials: 'include',
    headers: getAuthHeaders({ body: JSON.stringify(payload) }),
    body: JSON.stringify(payload),
  })

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}))
    throw new Error(errorData.error || '회원 정보 수정에 실패했습니다.')
  }

  return response.json()
}

export function getPostDetail(postId) {
  return apiRequest(`/posts/${postId}`)
}

export async function createComment(postId, content) {
  const response = await fetch(`${API_BASE_URL}/posts/${postId}/comments`, {
    method: 'POST',
    credentials: 'include',
    headers: getAuthHeaders({ body: JSON.stringify({ content }) }),
    body: JSON.stringify({ content }),
  })

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}))
    throw new Error(errorData.error || '댓글 작성에 실패했습니다.')
  }

  return response.json()
}

export async function deleteComment(commentId) {
  const response = await fetch(`${API_BASE_URL}/posts/comments/${commentId}`, {
    method: 'DELETE',
    credentials: 'include',
    headers: getAuthHeaders(),
  })

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}))
    throw new Error(errorData.error || '댓글 삭제에 실패했습니다.')
  }

  return response.json()
}

export async function togglePostReaction(postId, type) {
  const response = await fetch(`${API_BASE_URL}/posts/${postId}/${type === 'LIKE' ? 'like' : 'dislike'}`, {
    method: 'POST',
    credentials: 'include',
    headers: getAuthHeaders(),
  })

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}))
    throw new Error(errorData.error || '반응 처리에 실패했습니다.')
  }

  return response.json()
}

export async function toggleCommentReaction(commentId, type) {
  const response = await fetch(`${API_BASE_URL}/comments/${commentId}/${type === 'LIKE' ? 'like' : 'dislike'}`, {
    method: 'POST',
    credentials: 'include',
    headers: getAuthHeaders(),
  })

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}))
    throw new Error(errorData.error || '반응 처리에 실패했습니다.')
  }

  return response.json()
}

export async function updatePost(postId, payload) {
  const response = await fetch(`${API_BASE_URL}/posts/${postId}`, {
    method: 'PUT',
    credentials: 'include',
    headers: getAuthHeaders({ body: JSON.stringify(payload) }),
    body: JSON.stringify(payload),
  })

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}))
    throw new Error(errorData.error || '게시글 수정에 실패했습니다.')
  }

  return response.json()
}

export async function deletePost(postId) {
  const response = await fetch(`${API_BASE_URL}/posts/${postId}`, {
    method: 'DELETE',
    credentials: 'include',
    headers: getAuthHeaders(),
  })

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}))
    throw new Error(errorData.error || '게시글 삭제에 실패했습니다.')
  }

  return response.json()
}

export async function deleteUser(userId) {
  const response = await fetch(`${API_BASE_URL}/users/${userId}`, {
    method: 'DELETE',
    credentials: 'include',
    headers: getAuthHeaders(),
  })

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}))
    throw new Error(errorData.error || '사용자 삭제에 실패했습니다.')
  }

  return response.json()
}
