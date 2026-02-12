// 패키지 선언: 이 클래스가 속한 패키지를 지정
package com.example.demo.image.dto;

// Lombok 라이브러리 import
import lombok.AllArgsConstructor; // 모든 필드를 매개변수로 받는 생성자 자동 생성
import lombok.Getter;             // 모든 필드에 대해 Getter 메서드 자동 생성

/**
 * 이미지 정보를 클라이언트에 전달할 때 사용하는 DTO (Data Transfer Object)
 * DB 엔티티를 직접 노출하지 않고 필요한 정보만 전달하기 위해 사용
 */
@Getter
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자를 자동 생성
public class ImageResponseDto {

    // 원본 파일명 (사용자가 업로드한 파일 이름)
    private String originalName;

    // 서버에 저장된 경로 (파일 시스템이나 클라우드에 저장된 위치)
    private String storedPath;
}
