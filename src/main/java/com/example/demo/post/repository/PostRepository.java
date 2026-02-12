// 패키지 선언: 이 클래스가 속한 패키지를 지정
package com.example.demo.post.repository;

import com.example.demo.post.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 게시글(Post) 엔티티에 대한 JPA 리포지토리 인터페이스
 *
 * JpaRepository 상속으로 기본 CRUD, 페이징, 정렬 기능 제공
 * @param Post 엔티티 타입
 * @param Long 엔티티 기본키 타입
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 제목 또는 내용에 특정 키워드가 포함된 게시글을 페이징 처리하여 조회
     *
     * - Spring Data JPA Query Method 이름 규칙으로 자동 쿼리 생성
     * - "Containing" → SQL의 LIKE '%keyword%' 와 동일
     * - OR 조건 사용 → 제목 또는 내용 중 하나라도 키워드 포함 시 조회
     *
     * 예시 JPQL:
     * SELECT p FROM Post p
     * WHERE p.title LIKE %:titleKeyword%
     *    OR p.content LIKE %:contentKeyword%
     *
     * @param titleKeyword 제목에서 검색할 키워드
     * @param contentKeyword 내용에서 검색할 키워드
     * @param pageable 페이지 번호, 크기, 정렬 조건 포함 Pageable 객체
     * @return Page<Post> 페이징된 Post 엔티티 목록
     */
    Page<Post> findByTitleContainingOrContentContaining(String titleKeyword, String contentKeyword, Pageable pageable);

    /**
     * 특정 사용자가 작성한 모든 게시글 조회
     *
     * @param userId 작성자 ID
     * @return List<Post> 작성자 게시글 목록
     */
    List<Post> findAllByAuthorId(Long userId);

}
