package com.example.demo.comment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDto {
    // ==============================
    // ⚠️ 주의: 현재 변수명이 'pastId'로 되어 있음
    // 원래 의미는 'postId' (게시글 ID)여야 함
    // ==============================
    private Long pastId; // 게시글 ID를 담는 필드 (이름 오타)

    private String content; // 댓글 내용

    // Getter와 Setter를 Lombok이 자동 생성해주지만, 별도로 커스텀 getter/setter 작성
    public Long getPostId() {
        return pastId; // 외부에서 getPostId() 호출 시 pastId 반환
    }

    public void setPostId(Long postId) {
        this.pastId = postId; // 외부에서 setPostId() 호출 시 pastId에 값 저장
    }
}
