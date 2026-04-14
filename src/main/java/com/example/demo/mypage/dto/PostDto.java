// 패키지 선언: 이 클래스가 속한 패키지를 지정
package com.example.demo.mypage.dto;

// 필요한 클래스 import
import com.example.demo.post.post.Post; // 게시글 엔티티
import lombok.Builder; // 빌더 패턴 자동 생성
import lombok.Getter;  // Getter 자동 생성

/**
 * 게시글 정보를 클라이언트에 전달할 때 사용하는 DTO
 */
@Getter
@Builder
public class PostDto {

    private Long id;         // 게시글 ID
    private String title;    // 게시글 제목
    private String content;  // 게시글 내용
    private int likeCount;   // 좋아요 수
    private int dislikeCount;// 싫어요 수
    private String myReaction; // 현재 로그인 사용자의 반응: "LIKE", "DISLIKE", 또는 null

    /**
     * Post 엔티티와 사용자의 반응 정보를 기반으로 DTO 생성
     * @param post 게시글 엔티티
     * @param myReaction 로그인 사용자의 반응
     * @return PostDto
     */
    public static PostDto from(Post post, String myReaction){
        return PostDto.builder()
                .id(post.getId()) // 게시글 ID 설정
                .title(post.getTitle()) // 게시글 제목 설정
                .content(post.getContent()) // 게시글 내용 설정
                // 좋아요 수 계산: reactions 중 LIKE 타입 개수 카운트
                .likeCount((int) post.getReactions().stream()
                        .filter(r -> r.getType().name().equals("LIKE")).count())
                // 싫어요 수 계산: reactions 중 DISLIKE 타입 개수 카운트
                .dislikeCount((int) post.getReactions().stream()
                        .filter(r -> r.getType().name().equals("DISLIKE")).count())
                .myReaction(myReaction) // 로그인 사용자의 반응 설정
                .build();
    }
}
