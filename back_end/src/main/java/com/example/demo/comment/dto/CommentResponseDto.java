package com.example.demo.comment.dto;

import com.example.demo.comment.comment.Comment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class CommentResponseDto {
    // 댓글 ID
    private Long id;

    // 작성자 ID
    private Long userId;

    // 댓글 내용
    private String content;

    // 작성자 닉네임
    private String nickname;

    // 작성자 실제 이름 또는 username
    private String authorName;

    // 댓글 작성 시간
    private LocalDateTime createdAt;

    // 댓글에 대한 반응 요약 정보
    // 예: {"myReaction":"LIKE","likeCount":5,"dislikeCount":2}
    private Map<String, Object> reactionSummary;

    // 현재 로그인한 사용자가 누른 반응 상태 ("LIKE", "DISLIKE", null)
    private String myReaction;

    // 좋아요 수
    private long likeCount;

    // 싫어요 수
    private long dislikeCount;

    // 현재 로그인한 사용자가 좋아요 눌렀는지 여부
    private boolean likedByUser;

    // 현재 로그인한 사용자가 싫어요 눌렀는지 여부
    private boolean dislikedByUser;

    // Lombok @Getter/@Setter로 자동 생성되지만,
    // 커스텀 getter/setter를 직접 정의한 경우도 있음
    public String getMyReaction() { return myReaction; }
    public void setMyReaction(String myReaction) { this.myReaction = myReaction; }

    public long getLikeCount() { return likeCount; }
    public void setLikeCount(long likeCount) { this.likeCount = likeCount; }

    public long getDislikeCount() { return dislikeCount; }
    public void setDislikeCount(long dislikeCount) { this.dislikeCount = dislikeCount; }

    // Comment 엔티티를 DTO로 변환하는 생성자
    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.userId = comment.getUser().getId();
        this.content = comment.getContent();
        this.nickname = comment.getUser().getNickname(); // 작성자 닉네임
        this.authorName = comment.getUser().getUsername(); // 작성자 이름/아이디
        this.createdAt = comment.getCreatedAt();
        // reactionSummary, myReaction, likeCount 등은 서비스 레이어에서 추가로 세팅
    }
}
