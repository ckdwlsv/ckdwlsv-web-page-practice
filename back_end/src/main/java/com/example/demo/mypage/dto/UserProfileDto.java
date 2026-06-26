// 패키지 선언: 이 클래스가 속한 패키지를 지정
package com.example.demo.mypage.dto;

// 필요한 클래스 import
import com.example.demo.user.domain.User; // User 엔티티
import lombok.Getter; // Getter 자동 생성

/**
 * 사용자 프로필 정보를 클라이언트에 전달할 때 사용하는 DTO
 */
@Getter
public class UserProfileDto {

    private Long id;        // 사용자 ID
    private String username; // 사용자 계정명
    private String nickname; // 사용자 닉네임
    private String role;     // 사용자 권한(role) 이름 문자열

    /**
     * User 엔티티를 기반으로 DTO 생성
     * @param user User 엔티티
     */
    public UserProfileDto(User user){
        this.id = user.getId();            // 엔티티의 ID 설정
        this.username = user.getUsername(); // 엔티티의 계정명 설정
        this.nickname = user.getNickname(); // 엔티티의 닉네임 설정
        this.role = user.getRole().name();  // 엔티티의 권한 Enum을 문자열로 변환
    }
}
