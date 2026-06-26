package com.example.demo.reaction.reaction;

import com.example.demo.comment.comment.Comment;
import com.example.demo.post.post.Post;
import com.example.demo.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * Reaction 엔티티
 *
 * 게시글(Post) 또는 댓글(Comment)에 대한 사용자의 반응(좋아요/싫어요)을 나타냄.
 * - 하나의 Reaction은 한 명의 사용자(User)와 하나의 Post 또는 Comment에 연결됨
 * - 하나의 사용자는 동일한 Post 또는 Comment에 대해 하나의 Reaction만 가질 수 있음
 */
@Entity
@Table(
        name = "reaction",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_Id","post_Id","comment_Id"})
        // user_id + post_id + comment_id 조합은 유니크
        // → 동일한 사용자, 동일한 게시글/댓글에 중복 반응 방지
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reaction {

    /**
     * Reaction 고유 ID (기본키)
     * 자동 증가 전략 사용
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 반응 타입
     * - ReactionType Enum 사용: "LIKE", "DISLIKE"
     * - DB에는 문자열로 저장
     * - 길이는 최대 10자로 제한
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ReactionType type;

    /**
     * Reaction을 한 사용자
     * - User 엔티티와 다대일 관계
     * - Lazy 로딩: 필요할 때만 조회
     * - user_id 외래키 컬럼 사용
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Reaction이 연결된 게시글
     * - Post 엔티티와 다대일 관계
     * - Lazy 로딩: 필요할 때만 조회
     * - post_id 외래키 컬럼 사용
     * - 댓글 반응이 아닌 경우에만 사용
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    /**
     * Reaction이 연결된 댓글
     * - Comment 엔티티와 다대일 관계
     * - Lazy 로딩
     * - comment_id 외래키 컬럼 사용
     * - 게시글 반응이 아닌 경우에만 사용
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    /**
     * Reaction 타입 변경
     * @param newType 새로운 ReactionType (LIKE, DISLIKE)
     */
    public void changeType(ReactionType newType){
        this.type = newType;
    }

    /**
     * 게시글(Post)에 대한 Reaction 생성 헬퍼 메서드
     * @param user Reaction을 생성한 사용자
     * @param post Reaction 대상 게시글
     * @param type Reaction 타입 (LIKE/ DISLIKE)
     * @return 새 Reaction 엔티티
     */
    public static Reaction forPost(User user, Post post, ReactionType type){
        return Reaction.builder()
                .user(user)
                .post(post)
                .type(type)
                .build();
    }

    /**
     * 댓글(Comment)에 대한 Reaction 생성 헬퍼 메서드
     * @param user Reaction을 생성한 사용자
     * @param comment Reaction 대상 댓글
     * @param type Reaction 타입 (LIKE/ DISLIKE)
     * @return 새 Reaction 엔티티
     */
    public static Reaction forComment(User user, Comment comment, ReactionType type){
        return Reaction.builder()
                .user(user)
                .comment(comment)
                .type(type)
                .build();
    }

}
