import { useMemo, useState } from 'react'

function PostListPage({ posts, onNavigate }) {
  const [keyword, setKeyword] = useState('')

  const filteredPosts = useMemo(() => {
    if (!keyword.trim()) return posts
    return posts.filter((post) => post.title.toLowerCase().includes(keyword.toLowerCase()))
  }, [keyword, posts])

  return (
    <div className="page-wrap">
      <section className="card">
        <h1>게시글 목록</h1>

        <form onSubmit={(event) => event.preventDefault()}>
          <div className="row">
            <input
              type="text"
              name="keyword"
              value={keyword}
              onChange={(event) => setKeyword(event.target.value)}
              placeholder="검색어 입력"
            />
            <button className="btn btn-primary" type="submit">
              검색
            </button>
          </div>
        </form>

        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>번호</th>
                <th>제목</th>
                <th>작성자</th>
              </tr>
            </thead>
            <tbody>
              {filteredPosts.length === 0 ? (
                <tr>
                  <td colSpan="3">게시글이 없습니다.</td>
                </tr>
              ) : (
                filteredPosts.map((post) => (
                  <tr key={post.id}>
                    <td>{post.id}</td>
                    <td>
                      <button className="link-button" type="button" onClick={() => onNavigate('detail', post.id)}>
                        {post.title}
                      </button>
                    </td>
                    <td>{post.authorName}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        <div className="pagination">
          <span>&lt; 이전</span>
          <span>1 / 1</span>
          <span>다음 &gt;</span>
        </div>

        <div className="row">
          <button className="btn btn-primary" type="button" onClick={() => onNavigate('write')}>
            글쓰기
          </button>
          <button className="btn btn-ghost" type="button" onClick={() => onNavigate('home')}>
            메인으로
          </button>
        </div>
      </section>
    </div>
  )
}

export default PostListPage
