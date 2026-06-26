// 패키지 선언: 이 클래스가 속한 패키지를 지정
package com.example.demo.image.service;

// 필요한 클래스 import
import com.example.demo.image.image.Image;       // 이미지 엔티티
import com.example.demo.image.repository.ImageRepository; // 이미지 레포지토리
import com.example.demo.post.post.Post;          // 게시글 엔티티
import lombok.RequiredArgsConstructor;           // final 필드 생성자 자동 생성
import org.springframework.stereotype.Service;   // Service 어노테이션
import org.springframework.web.multipart.MultipartFile; // 업로드 파일 처리

import java.io.IOException; // 파일 처리 예외
import java.util.List;      // 리스트 컬렉션

/**
 * 이미지 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor // final 필드(imageRepository)에 대한 생성자 자동 생성
public class ImageService {

    // 이미지 DB 작업을 담당하는 레포지토리 주입
    private final ImageRepository imageRepository;

    /**
     * 여러 이미지를 업로드 받아 DB에 저장
     * @param files 업로드된 파일 리스트
     * @param post 이미지가 연결될 게시글
     */
    public void saveImages(List<MultipartFile> files, Post post) {
        for (MultipartFile file : files) { // 파일 하나씩 반복 처리
            if (!file.isEmpty()) { // 비어있는 파일은 무시
                try {
                    // MultipartFile → Image 엔티티 변환
                    Image image = Image.builder()
                            .data(file.getBytes())          // 파일 내용을 byte 배열로 저장
                            .contentType(file.getContentType()) // MIME 타입 저장
                            .post(post)                     // 연관된 게시글 설정
                            .build();

                    // DB에 이미지 저장
                    imageRepository.save(image);
                } catch (IOException e) {
                    // 파일 읽기 실패 시 RuntimeException으로 처리
                    throw new RuntimeException("이미지 저장 실패", e);
                }
            }
        }
    }

    /**
     * ID로 이미지 조회
     * @param id 이미지 ID
     * @return Image 조회된 이미지 엔티티
     * @throws IllegalArgumentException 이미지가 없을 경우
     */
    public Image findById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("이미지를 찾을 수 없습니다."));
    }
}
