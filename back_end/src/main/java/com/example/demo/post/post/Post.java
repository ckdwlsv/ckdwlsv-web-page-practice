package com.example.demo.post.post;

import com.example.demo.comment.comment.Comment;
import com.example.demo.image.image.Image;
import com.example.demo.reaction.reaction.Reaction;
import com.example.demo.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/*
 * 게시글(Post) 엔티티 클래스
 * 데이터베이스의 "posts" 테이블과 매핑됨
 */
@Entity// JPA 엔티티로 지정
@Table(name = "posts")// 매핑할 테이블 이름을 "posts"로 지정
@Getter// 모든 필드에 대한 getter 메서드 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED)// 기본 생성자 자동 생성 (외부에서 직접 생성 못하도록 보호)
@AllArgsConstructor// 모든 필드를 인자로 받는 생성자 자동 생성
@Builder// 빌더 패턴 지원
public class Post {
    /*
     * 게시글 ID (기본 키)
     * 자동 증가 전략 사용 (IDENTITY)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /*
     * 게시글 제목
     * null 불가, 최대 길이 100자
     */
    @Column(nullable = false, length = 100)
    private String title;
    /*
     * 게시글 본문 내용
     * null 불가, text 타입으로 저장
     */
    @Column(nullable = false, columnDefinition = "text")
    private String content;
    /*
     * 게시글 작성자 (User 엔티티와 다대일 관계)
     * 지연 로딩(FetchType.LAZY)으로 설정
     * author_id 컬럼을 외래키로 사용
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id") // 외래키 이름도 author_id로 지정
    private User author;
    /*
     * 작성자의 닉네임
     * 실제로는 User 엔티티의 nickname 필드 값을 복사해서 저장
     */
    @Column(nullable = false)
    private String authorName; // 작성자의 닉네임 저장
    /*
     * 게시글 수정 메서드
     * 제목과 내용을 변경
     * @param title 새 제목
     * @param content 새 내용
     */
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
    /*
     * 작성자 설정 메서드
     * @param author 작성자 (User 엔티티)
     */
    public void  SetAuthor(User author) {
        this.author = author;
    }

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reaction> reactions = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

}
