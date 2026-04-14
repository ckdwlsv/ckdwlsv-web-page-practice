// 패키지 선언: 이 클래스가 속한 패키지를 지정
package com.example.demo.mypage.dto;

// 필요한 클래스 import
import com.example.demo.comment.comment.Comment; // 댓글 엔티티
import lombok.Builder; // 빌더 패턴 자동 생성
import lombok.Getter;  // Getter 자동 생성

/**
 * 댓글 정보를 클라이언트에 전달할 때 사용하는 DTO
 */
@Getter
@Builder
public class CommentDto {

    private Long id;          // 댓글 ID
    private Long postId;      // 댓글이 속한 게시글 ID
    private String content;   // 댓글 내용
    private int likeCount;    // 좋아요 수
    private int dislikeCount; // 싫어요 수
    private String myReaction; // 현재 로그인 사용자의 반응: "LIKE", "DISLIKE", 또는 null

    /**
     * Comment 엔티티와 사용자의 반응 정보를 기반으로 DTO 생성
     * @param comment 댓글 엔티티
     * @param myReaction 로그인 사용자의 반응
     * @return CommentDto
     */
    public static CommentDto from(Comment comment, String myReaction){
        return CommentDto.builder()
                .id(comment.getId()) // 댓글 ID 설정
                .postId(comment.getPost().getId()) // 댓글이 속한 게시글 ID 설정
                .content(comment.getContent()) // 댓글 내용 설정
                // 좋아요 수 계산: reactions가 null이 아니면 LIKE 타입 개수 카운트
                .likeCount(comment.getReactions() != null ?
                        (int) comment.getReactions().stream()
                                .filter(r -> r.getType().name().equals("LIKE"))
                                .count() : 0)
                // 싫어요 수 계산: reactions가 null이 아니면 DISLIKE 타입 개수 카운트
                .dislikeCount(comment.getReactions() != null ?
                        (int) comment.getReactions().stream()
                                .filter(r -> r.getType().name().equals("DISLIKE"))
                                .count() : 0)
                .myReaction(myReaction) // 로그인 사용자의 반응 설정
                .build();
    }
}
