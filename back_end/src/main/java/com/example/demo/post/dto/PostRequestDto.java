// 패키지 선언: 이 클래스가 속한 패키지를 지정
package com.example.demo.post.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 게시글 생성 또는 수정을 위한 요청 데이터를 담는 DTO 클래스
 * 클라이언트에서 전달한 게시글 제목과 내용을 서버에서 받을 때 사용
 */
@Getter // Lombok: 모든 필드에 대한 Getter 메서드 자동 생성
@Setter // Lombok: 모든 필드에 대한 Setter 메서드 자동 생성
public class PostRequestDto {

    private String title;   // 게시글 제목
    private String content; // 게시글 내용

}
