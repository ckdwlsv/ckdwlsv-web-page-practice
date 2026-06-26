package com.example.demo.reaction.reaction;

/**
 * ReactionType 열거형
 *
 * 게시글(Post) 또는 댓글(Comment)에 대한 사용자의 반응 타입을 정의
 * - LIKE: 좋아요
 * - DISLIKE: 싫어요
 *
 * Reaction 엔티티의 type 필드에 사용됨
 * DB에는 문자열(String) 형태로 저장됨 (예: "LIKE", "DISLIKE")
 */
public enum ReactionType {
    /**
     * 좋아요 반응
     */
    LIKE,

    /**
     * 싫어요 반응
     */
    DISLIKE
}
