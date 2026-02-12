## 웹 프로젝트
___

## 프로젝트 소개

사용자 인증, 게시글 CRUD, 댓글/대댓글, 좋아요/싫어요 기능을 포함한 커뮤니티 게시판입니다.

---

## 주요 기능

### 사용자 관리

- 회원가입 / 로그인 / 로그아웃
- 세션 기반 인증
- 마이페이지 (내 정보 조회)

### 게시글

- 게시글 작성, 수정, 삭제
- 게시글 목록 조회 (페이지네이션)
- 게시글 상세 조회

### 댓글 시스템

- 댓글 작성 / 삭제
- 재귀적 댓글 렌더링

### 리액션 (좋아요/싫어요)

- 게시글 좋아요/싫어요
- 댓글 좋아요/싫어요
- 토글 방식 (다시 클릭 시 취소)

### 이미지

- 게시글 이미지 첨부
- 이미지 미리보기

---

### 접속

```
http://localhost:7777
```

## API 엔드포인트

### 페이지 라우팅

|  Method | URL                | 설명            |
|---------| ------------------ | --------------- |
| GET     | `/`                | 메인 페이지     |
| GET     | `/user/login`      | 로그인 페이지   |
| GET     | `/user/signup`     | 회원가입 페이지 |
| GET     | `/posts/list`      | 게시글 목록     |
| GET     | `/posts/{id}`      | 게시글 상세     |
| GET     | `/posts/write`     | 게시글 작성     |
| GET     | `/posts/{id}/edit` | 게시글 수정     |
| GET     | `/mypage`          | 마이페이지      |

### REST API

| Method | URL                           | 설명                               |
| ------ | ----------------------------- | ---------------------------------- |
| POST   | `/user/signup`                | 회원가입 처리                      |
| POST   | `/user/login`                 | 로그인 처리                        |
| POST   | `/user/logout`                | 로그아웃 처리                      |
| POST   | `/posts`                      | 게시글 등록                        |
| POST   | `/posts/{id}`                 | 댓글 등록 (parentId로 대댓글 구분) |
| POST   | `/posts/{id}/edit`            | 게시글 수정                        |
| POST   | `/posts/{id}/delete`          | 게시글 삭제                        |
| POST   | `/posts/comments/{id}/delete` | 댓글 삭제                          |
| POST   | `/reactions/posts/{id}`       | 게시글 리액션 토글                 |
| POST   | `/reactions/comments/{id}`    | 댓글 리액션 토글                   |
