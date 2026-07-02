package com.example.demo.post.service;

import com.example.demo.image.image.Image;
import com.example.demo.image.service.ImageService;
import com.example.demo.post.dto.PostRequestDto;
import com.example.demo.post.dto.PostResponseDto;
import com.example.demo.post.post.Post;
import com.example.demo.post.repository.PostRepository;
import com.example.demo.reaction.reaction.Reaction;
import com.example.demo.reaction.reaction.ReactionType;
import com.example.demo.reaction.reactionRepository.ReactionRepository;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserRole;
import com.example.demo.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * PostService 클래스
 *
 * 게시글(Post)과 관련된 핵심 비즈니스 로직 처리.
 * - 게시글 CRUD(Create, Read, Update, Delete)
 * - 이미지 업로드
 * - 좋아요/싫어요 상태 관리
 * - 페이징 및 검색 기능
 */
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;          // 게시글 데이터 접근 객체
    private final ReactionRepository reactionRepository;  // 좋아요/싫어요 DB 접근
    private final UserService userService;                // 사용자 관련 서비스

    /**
     * 페이징 처리된 전체 게시글 조회
     * @param page 조회할 페이지 번호 (0부터 시작)
     * @param size 페이지당 게시글 개수
     * @return Page<PostResponseDto> DTO로 변환된 페이징 게시글
     */
    public Page<PostResponseDto> findAllPaged(int page, int size) {
        // Pageable 객체 생성: page 번호, size, 정렬 기준(id 내림차순)
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        // DB에서 페이징 처리 후 Post → PostResponseDto 변환
        return postRepository.findAll(pageable)
                .map(PostResponseDto::new); // DTO 변환
    }

    /**
     * 키워드 검색 + 페이징 처리 게시글 조회
     * @param keyword 검색어
     * @param page 페이지 번호
     * @param size 페이지당 게시글 개수
     * @return Page<PostResponseDto> 검색 결과 DTO
     */
    public Page<PostResponseDto> searchPostsPaged(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        // 제목 또는 내용에 keyword 포함된 게시글 검색 후 DTO 변환
        return postRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable)
                .map(PostResponseDto::form);
    }

    /**
     * 게시글 ID로 단일 게시글 조회
     * @param id 게시글 ID
     * @return PostResponseDto 조회된 게시글 DTO
     * @throws IllegalArgumentException 게시글이 존재하지 않으면 예외 발생
     */
    public PostResponseDto findById(long id) {
        return PostResponseDto.form(findEntityById(id));
    }

    public Post findEntityById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
    }

    /**
     * 게시글 생성
     * @param dto 게시글 요청 DTO (제목, 내용)
     * @param user 작성자 정보
     * @param files 업로드 이미지 파일 목록
     */
    @Transactional
    public void create(PostRequestDto dto, User user, List<MultipartFile> files) {
        // Post 엔티티 생성
        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(user)
                .authorName(user.getNickname())
                .build();

        // 이미지 파일 처리
        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) { // 빈 파일이면 무시
                    try {
                        // Image 엔티티 생성
                        Image image = Image.builder()
                                .contentType(file.getContentType()) // MIME 타입
                                .data(file.getBytes())            // 실제 파일 데이터
                                .build();
                        // Post 객체에 이미지 추가 (cascade로 DB 저장 가능)
                        post.addImage(image);
                    } catch (IOException e) {
                        // 파일 읽기 실패 시 런타임 예외 발생
                        e.printStackTrace();
                        throw new RuntimeException("이미지 업로드 실패", e);
                    }
                }
            }
        }

        // DB에 게시글 저장 (이미지도 cascade 적용으로 함께 저장됨)
        postRepository.save(post);
    }

    /**
     * 게시글 수정
     * @param id 수정할 게시글 ID
     * @param dto 수정 내용 DTO
     * @param loginUserId 현재 로그인 사용자 ID (작성자 검증)
     * @throws IllegalArgumentException 게시글 존재하지 않거나 작성자가 아닐 경우 예외
     */
    @Transactional
    public void update(Long id, PostRequestDto dto, Long loginUserId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        // 작성자 검증
        if (!post.getAuthor().getId().equals(loginUserId)) {
            throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
        }

        // 제목, 내용 수정
        post.update(dto.getTitle(), dto.getContent());
    }

    /**
     * 게시글 삭제
     * @param id 삭제할 게시글 ID
     * @param loginUserId 현재 로그인 사용자 ID (작성자 검증)
     */
    @Transactional
    public void delete(Long id, Long loginUserId, UserRole role) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        boolean isAuthor = post.getAuthor().getId().equals(loginUserId);
        if (!isAuthor && role != UserRole.ADMIN) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        postRepository.delete(post);
    }

    @Transactional
    public void delete(Long id, Long loginUserId) {
        delete(id, loginUserId, UserRole.USER);
    }

    /**
     * 게시글 좋아요/싫어요 토글 처리
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @param isLike true = 좋아요, false = 싫어요
     * @return Map<String,Object> 좋아요/싫어요 상태 및 카운트
     */
    @Transactional
    public Map<String,Object> toggleLike(Long postId, Long userId, boolean isLike) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        // TODO: 실제 DB 연동 필요
        boolean likedByUser = false;  // 사용자가 이미 좋아요 눌렀는지 확인
        boolean dislikedByUser = false; // 사용자가 이미 싫어요 눌렀는지 확인
        Long likeCount = 0L;           // 총 좋아요 수
        Long dislikeCount = 0L;        // 총 싫어요 수

        Map<String,Object> result = new HashMap<>();
        result.put("likeCount", likeCount);
        result.put("dislikeCount", dislikeCount);
        result.put("likedByUser", likedByUser);
        result.put("dislikedByUser", dislikedByUser);

        return result;
    }

    /**
     * 게시글 좋아요/싫어요 상태 조회
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return Map<String,Object> 내 반응, 총 좋아요/싫어요 수
     */
    public Map<String, Object> getReactionStatus(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        // 사용자 정보 조회
        User user = userService.findById(userId);

        // 사용자의 반응 조회
        Optional<Reaction> myReactionOpt = reactionRepository.findByUserAndPost(user, post);
        String myReaction = myReactionOpt.map(r -> r.getType().name()).orElse(null);

        // 좋아요/싫어요 총합 조회
        long likeCount = reactionRepository.countByPostAndType(post, ReactionType.LIKE);
        long dislikeCount = reactionRepository.countByPostAndType(post, ReactionType.DISLIKE);

        Map<String, Object> result = new HashMap<>();
        result.put("myReaction", myReaction);
        result.put("likeCount", likeCount);
        result.put("dislikeCount", dislikeCount);

        return result;
    }

}
