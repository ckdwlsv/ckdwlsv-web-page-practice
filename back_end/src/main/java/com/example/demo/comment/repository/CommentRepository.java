package com.example.demo.comment.repository;

import com.example.demo.comment.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // Spring Data JPA Repository임을 명시. 스프링이 Bean으로 자동 등록
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // JpaRepository 상속:
    // - Comment 엔티티를 대상으로 함
    // - Primary Key 타입은 Long

    // ---------------------- 커스텀 쿼리 메서드 ----------------------

    // 특정 게시글(postId)에 속한 댓글들을 생성일(createdAt) 기준 오름차순으로 조회
    // URL에서 postId를 받아 댓글 리스트 출력할 때 사용
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);

    // 특정 사용자가 작성한 모든 댓글 조회
    // 마이페이지에서 내가 쓴 댓글 확인 등 사용
    List<Comment> findAllByUserId(Long userId);
}
