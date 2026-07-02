package com.example.demo.user.domain;

import com.example.demo.post.post.Post;
import jakarta.persistence.*;
import jakarta.servlet.http.HttpSession;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
// User 엔티티를 데이터베이스 테이블 "users"에 매핑
@Entity
@Table(name = "users")
// Lombok 어노테이션들
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

public class User {

    // 기본 키, 자동 증가 전략 사용 (MYSQL AUTO_INCREMENT와 대응)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//자동 증가 방식(MySQL의 AUTO_INCREMENT와 대응)
    private Long id;

    //사용자 이름, null 불가, 중복 불가, 최대 30자 제한
    @Column(nullable = false, unique = true, length = 30)//Not Null, 유니크
    private String username;

    // 비밀번호, null불가
    @Column(nullable = false)//Not Null
    private String password;

    // 닉네임, null불가, 최대 20자 제한
    @Column(nullable = false, length = 20)//Not Null
    private String nickname;

    @Enumerated(EnumType.ORDINAL)//DB에는 0(USER), 1(ADMIN) 형태로 저장
    @Column(nullable = false)
    private UserRole role; //사용자 권한(ROLE,ADMIN)

    // ---------------------- 연관관계 ----------------------
    /*
     * User와 Post는 1:N 관계
     * Post의 author 필드가 연관관계 주인
     * Cascade.ALL: User가 변경되면 관련 Post도 함께 변경
     * orphanRemoval=true: User에서 제거된 Post는 DB에서 삭제
     */
    @Builder.Default
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    // ---------------------- 편의 메서드 ----------------------
    /*
     * Post를 User에 추가하고, 양방향 연관관계 설정
     * @param post 추가할 게시글
     */
    public void addPost(Post post){
        posts.add(post); // User의 posts 리스트에 post추가
        post.SetAuthor(this); // Post의 author 필드를 현제 User로 설정
    }

}
