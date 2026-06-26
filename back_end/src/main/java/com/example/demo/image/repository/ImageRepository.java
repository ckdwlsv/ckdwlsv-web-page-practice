// 패키지 선언: 이 인터페이스가 속한 패키지를 지정
package com.example.demo.image.repository;

// 필요한 클래스 import
import com.example.demo.image.image.Image; // Image 엔티티
import org.springframework.data.jpa.repository.JpaRepository; // JPA Repository 상속

/**
 * 이미지 데이터를 DB에서 조회/저장/삭제하기 위한 Repository 인터페이스
 * JpaRepository를 상속하면 기본적인 CRUD 기능을 자동으로 제공
 */
public interface ImageRepository extends JpaRepository<Image, Long> {
    // JpaRepository<Image, Long>
    // - Image: 관리할 엔티티 타입
    // - Long: 엔티티의 ID 타입

    // 추가적인 쿼리가 필요하다면 이 인터페이스에 메서드 선언 가능
    // 예: List<Image> findByPostId(Long postId);
}
