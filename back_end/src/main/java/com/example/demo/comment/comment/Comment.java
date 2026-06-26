package com.example.demo.comment.comment;

import com.example.demo.post.post.Post;
import com.example.demo.reaction.reaction.Reaction;
import com.example.demo.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity // JPA 엔티티임을 명시. DB 테이블과 매핑됨
@Table(name = "comment") // DB에서 매핑될 테이블 이름을 지정
@Getter // Lombok을 사용하여 모든 필드에 대한 getter 자동 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 기본 생성자를 protected로 생성. 외부에서 직접 객체 생성 방지, createComment() 같은 팩토리 메서드 사용 유도
public class Comment {

    @Id // Primary Key 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Auto Increment 전략 사용. DB에서 ID 값을 자동 생성
    private Long id;

    private String content; // 댓글 내용

    @ManyToOne // 다대일 관계: 여러 댓글이 하나의 게시글에 속함
    @JoinColumn(name = "post_id")
    // 외래키 이름 지정. comment 테이블에서 post_id 컬럼이 Post 테이블 참조
    private Post post;

    @ManyToOne // 다대일 관계: 여러 댓글이 하나의 사용자에 속함
    @JoinColumn(name = "user_id")
    // 외래키 이름 지정. comment 테이블에서 user_id 컬럼이 User 테이블 참조
    private User user;

    private LocalDateTime createdAt; // 댓글 생성 시각
    private LocalDateTime updatedAt; // 댓글 수정 시각

    // 실제 댓글 객체 생성 시 사용하는 생성자 (외부에서는 직접 접근 불가)
    private Comment(String content, Post post, User user){
        this.content = content;
        this.post = post;
        this.user = user;
        this.createdAt = LocalDateTime.now(); // 생성 시점 기록
        this.updatedAt = LocalDateTime.now(); // 생성 시점 기록
    }

    // 댓글 생성용 정적 팩토리 메서드
    // 생성 로직을 한 곳에 모아두고, 필요한 경우 후처리 추가 가능
    public static Comment createComment(String content, Post post, User user) {
        return new Comment(content, post, user);
    }

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    // comment가 삭제되거나 변경되면 연관된 Reaction도 같이 처리
    // mappedBy="comment"는 Reaction 엔티티의 comment 필드와 매핑됨
    // orphanRemoval=true: comment에서 삭제된 Reaction 객체는 DB에서도 삭제됨
    private List<Reaction> reactions = new ArrayList<>();
}
