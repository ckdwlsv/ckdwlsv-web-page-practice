package com.example.demo.user.repository;

import com.example.demo.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자(User) 엔티티에 대한 데이터베이스 접근을 담당하는 리포지토리 인터페이스
 *
 * - JpaRepository<User, Long> 상속:
 *   - User 엔티티를 대상으로 기본적인 CRUD(Create, Read, Update, Delete) 기능 제공
 *   - 페이징 처리, 정렬, JPA 쿼리 메서드 지원
 * - @Repository 어노테이션:
 *   - 스프링 빈으로 등록되어 의존성 주입 가능
 *   - 데이터 액세스 예외를 스프링의 DataAccessException으로 변환
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 사용자 이름(username)으로 User 엔티티를 조회
     *
     * - Optional<User> 반환:
     *   - 결과가 없으면 null 대신 Optional.empty() 반환
     *   - 호출하는 쪽에서 null 체크 없이 안전하게 처리 가능
     *
     * @param username 조회할 사용자 계정명
     * @return Optional<User> 조회 결과(User 존재 시)
     */
    Optional<User> findByUsername(String username);
}
