package com.example.demo.user.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 사용자 로그인 요청 데이터를 담는 DTO 클래스
 *
 * 클라이언트에서 로그인 요청 시 전달되는 정보를 서버에서 받을 때 사용.
 * DTO는 엔티티와 달리 DB와 직접 매핑되지 않으며, 요청/응답 데이터를 간단히 담는 역할.
 */
@Getter // Lombok: 모든 필드에 대한 getter 메서드를 자동 생성
@Setter // Lombok: 모든 필드에 대한 setter 메서드를 자동 생성
public class UserLoginRequestDto {

    /**
     * 사용자가 로그인 시 입력하는 사용자 이름 또는 아이디
     * 예: "myusername"
     */
    private String username;

    /**
     * 사용자가 로그인 시 입력하는 비밀번호
     * 예: "mypassword123"
     */
    private String password;

    // 필요시 기본 생성자, 매개변수 생성자는 Lombok @Getter/@Setter로 인해 생략 가능
    // 추가로 @AllArgsConstructor, @NoArgsConstructor를 사용하면 편리하게 객체 생성 가능
}
