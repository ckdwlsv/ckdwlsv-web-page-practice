package com.example.demo.user.dto;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserRole;
import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 정보를 클라이언트로 전달하기 위한 DTO 클래스
 *
 * - 로그인 성공 시, 마이페이지 조회 시 등 클라이언트에게
 *   필요한 사용자 정보를 안전하게 전달하는 용도로 사용.
 * - 엔티티(User)와 달리 DB 직접 연관이 없으며, 필요한 정보만 선택적으로 담음.
 */
@Getter // Lombok: 모든 필드에 대한 getter 자동 생성
@Builder // Lombok: 빌더 패턴을 사용하여 객체 생성 가능
public class UserResponseDto {

    /** 사용자 고유 ID */
    private Long id;

    /** 사용자 계정명(아이디) */
    private String username;

    /** 사용자 닉네임 */
    private String nickname;

    /** 사용자 권한 (USER, ADMIN 등) */
    private UserRole role;

    /**
     * User 엔티티 객체를 기반으로 UserResponseDto 객체 생성
     *
     * @param user User 엔티티 객체
     * @return UserResponseDto 빌더를 통해 생성된 DTO 객체
     */
    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .id(user.getId())              // User 엔티티의 id 복사
                .username(user.getUsername())  // User 엔티티의 username 복사
                .nickname(user.getNickname())  // User 엔티티의 nickname 복사
                .role(user.getRole())          // User 엔티티의 role 복사
                .build();                      // DTO 객체 생성 후 반환
    }

    /**
     * 전체 필드를 인자로 받는 생성자
     * DTO 객체 직접 생성 시 사용 가능
     *
     * @param id 사용자 고유 ID
     * @param username 사용자 계정명
     * @param nickname 사용자 닉네임
     * @param role 사용자 권한
     */
    public UserResponseDto(Long id, String username, String nickname, UserRole role) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.role = role;
    }
}
