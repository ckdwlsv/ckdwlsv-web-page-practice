package com.example.demo.user.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 회원가입 요청 시 클라이언트로부터 전달받는 데이터 DTO
 *
 * - Data Transfer Object(DTO): 데이터 전송을 위한 객체
 * - 클라이언트에서 서버로 사용자 입력 정보를 안전하게 전달
 * - 엔티티(User)와 분리하여 필요한 정보만 포함
 */
@Getter // Lombok: 모든 필드에 대한 getter 메서드 자동 생성
@Setter // Lombok: 모든 필드에 대한 setter 메서드 자동 생성
public class UserSignupRequestDto {

    /** 회원가입 시 입력받는 사용자 계정명(아이디) */
    private String username;

    /** 회원가입 시 입력받는 비밀번호 */
    private String password;

    /** 회원가입 시 입력받는 사용자 닉네임 */
    private String nickname;
}
