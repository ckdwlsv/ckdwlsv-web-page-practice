// 패키지 선언: 이 클래스가 속한 패키지를 지정
package com.example.demo.image.controller;

// 필요한 클래스 import
import com.example.demo.image.image.Image;         // 이미지 엔티티
import com.example.demo.image.service.ImageService; // 이미지 서비스
import lombok.RequiredArgsConstructor;             // final 필드 자동 생성자 생성
import org.springframework.http.HttpHeaders;       // HTTP 헤더 설정
import org.springframework.http.ResponseEntity;   // HTTP 응답 객체
import org.springframework.stereotype.Controller;  // Spring MVC 컨트롤러
import org.springframework.web.bind.annotation.GetMapping; // GET 요청 매핑
import org.springframework.web.bind.annotation.PathVariable; // URL 경로 변수

/**
 * 이미지 관련 요청을 처리하는 컨트롤러
 */
@Controller
@RequiredArgsConstructor // final 필드(imageService)에 대한 생성자를 자동 생성
public class ImageController {

    // 이미지 관련 비즈니스 로직을 수행하는 서비스 객체 주입
    private final ImageService imageService;

    /**
     * 특정 ID의 이미지를 조회하여 클라이언트에 반환
     * @param id 이미지 ID (URL 경로 변수)
     * @return ResponseEntity<byte[]> HTTP 응답: 이미지 데이터 + Content-Type
     */
    @GetMapping("/images/{id}") // GET /images/{id} 요청을 처리
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {

        // 서비스에서 ID로 이미지 조회
        Image image = imageService.findById(id);

        // ResponseEntity를 사용하여 HTTP 응답 구성
        // Content-Type 헤더를 이미지 타입으로 설정하고, 실제 이미지 데이터를 바디에 담아 반환
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, image.getContentType()) // MIME 타입 설정 (ex: image/png)
                .body(image.getData()); // 이미지 바이트 배열 반환
    }
}
