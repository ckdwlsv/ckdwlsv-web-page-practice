// 패키지 선언: 이 클래스가 속한 패키지를 지정
package com.example.demo.image.image;

// 필요한 클래스 import
import com.example.demo.post.post.Post; // 이미지가 연결된 게시글 엔티티
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;           // JPA 어노테이션
import lombok.*;                        // Lombok 어노테이션

/**
 * 이미지 정보를 DB에 저장하기 위한 JPA 엔티티
 */
@Entity
@Table(name = "images") // DB 테이블 이름을 'images'로 지정
@Getter
@Setter
@NoArgsConstructor      // 파라미터 없는 기본 생성자 자동 생성
@AllArgsConstructor     // 모든 필드를 파라미터로 받는 생성자 자동 생성
@Builder                // 빌더 패턴 사용 가능
public class Image {

    // 기본 키(ID) 필드
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment 전략
    private Long id;

    // MIME 타입 (ex: image/png, image/jpeg)
    private String contentType;

    // 실제 이미지 데이터 저장
    @Lob // Large Object: BLOB(Binary Large Object)로 저장
    @Column(nullable = false, columnDefinition = "LONGBLOB")
    private byte[] data;

    // 이미지와 게시글 간 N:1 관계 설정
    @ManyToOne(fetch = FetchType.LAZY) // Lazy 로딩: 필요할 때만 DB 조회
    @JoinColumn(name = "post_id")      // 연결할 외래키 컬럼 이름 지정
    private Post post;
}
