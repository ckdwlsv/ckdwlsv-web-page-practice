// 패키지 선언: 이 클래스가 속한 패키지를 지정
package com.example.demo.post.dto;

import com.example.demo.image.image.Image;
import com.example.demo.post.post.Post;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 게시글 응답 정보를 담는 DTO 클래스
 * 클라이언트에게 게시글 정보를 전달할 때 사용
 */
@Getter // Lombok: 모든 필드의 getter 메서드를 자동 생성
public class PostResponseDto {

    private Long id;           // 게시글 ID
    private String title;      // 게시글 제목
    private String content;    // 게시글 내용
    private String authorName; // 작성자 이름 (닉네임 등)

    private Long likeCount;       // 좋아요 수
    private Long dislikeCount;    // 싫어요 수
    private boolean likedByUser;  // 현재 로그인 사용자가 좋아요했는지 여부
    private boolean dislikedByUser; // 현재 로그인 사용자가 싫어요했는지 여부

    private List<Image> images = new ArrayList<>(); // 게시글에 첨부된 이미지 리스트
    private List<Long> imageIds = new ArrayList<>(); // Thymeleaf용 이미지 ID 리스트

    /**
     * Post 엔티티로부터 데이터를 추출하여 DTO에 매핑하는 생성자
     * @param post Post 엔티티 객체
     */
    public PostResponseDto(Post post){
        this.id = post.getId();           // 게시글 ID 설정
        this.title = post.getTitle();     // 게시글 제목 설정
        this.content = post.getContent(); // 게시글 내용 설정
        this.authorName = post.getAuthorName(); // 작성자 이름 설정

        // 게시글 이미지가 존재하면 리스트에 추가
        if (post.getImages() != null) {
            this.images.addAll(post.getImages());

            // 이미지 ID 리스트 생성 (템플릿에서 imageIds로도 사용 가능)
            for (Image img : post.getImages()) {
                this.imageIds.add(img.getId());
            }
        }
    }

    /**
     * 정적 팩토리 메서드: Post 객체로부터 PostResponseDto 생성
     * @param post Post 엔티티
     * @return PostResponseDto 객체
     */
    public static PostResponseDto form(Post post) {
        return new PostResponseDto(post); // 내부 생성자 호출
    }
}
