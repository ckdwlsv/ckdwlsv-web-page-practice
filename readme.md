# 웹 페이지 프로젝트

## 프로젝트 소개

Spring Boot 백엔드와 React 프런트엔드를 함께 사용하는 커뮤니티 게시판 프로젝트입니다.

## 구조

- back_end: Spring Boot 서버와 REST API
- front_end: React + Vite 프런트엔드

## 실행 방법

### 1. 백엔드 실행

```bash
cd back_end
./gradlew bootRun
```

- 백엔드 기본 포트: http://localhost:8080

### 2. 프런트엔드 실행

```bash
cd front_end
npm install
npm run dev
```

- 프런트엔드 기본 포트: http://localhost:3000

## 현재 연결된 API

- POST /api/signup
- POST /api/login
- POST /api/logout
- GET /api/me
- GET /api/posts
- GET /api/mypage

## 주요 기능

- 회원가입 / 로그인 / 로그아웃
- 게시글 목록 및 상세 조회
- 게시글 작성 / 수정 / 삭제
- 마이페이지 조회
- 세션 기반 인증

## 참고

- 프런트엔드는 Vite로 구동됩니다.
- 백엔드는 Spring Boot + Thymeleaf 기반 구조를 유지하면서 React API 연동이 가능합니다.
