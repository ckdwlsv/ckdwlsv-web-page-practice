const API_BASE_URL = 'http://localhost:8080/api'

export async function apiRequest(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {}),
    },
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

export function loginUser(payload) {
  return apiRequest('/login', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function signupUser(payload) {
  return apiRequest('/signup', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function logoutUser() {
  return apiRequest('/logout', {
    method: 'POST',
  })
}

export function getPosts() {
  return apiRequest('/posts')
}

export function getMyPageData() {
  return apiRequest('/mypage')
}
