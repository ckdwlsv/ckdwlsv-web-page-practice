package com.example.demo.reaction.reactionController;

import com.example.demo.reaction.reaction.ReactionType;
import com.example.demo.reaction.reactionService.ReactionService;
import com.example.demo.user.domain.User;
import com.example.demo.user.dto.UserResponseDto;
import com.example.demo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * ReactionController
 *
 * 게시글(Post)과 댓글(Comment)에 대한 사용자의 좋아요/싫어요 반응을 처리하는 REST 컨트롤러
 *
 * 주요 기능:
 *  - 게시글 좋아요/싫어요 토글
 *  - 댓글 좋아요/싫어요 토글
 *
 * 요청 URL 예시:
 *  - POST /reactions/posts/{postId}?type=LIKE
 *  - POST /reactions/comments/{commentId}?type=DISLIKE
 */
@RestController
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성, 의존성 주입 지원
@RequestMapping("/reactions") // "/reactions"로 시작하는 모든 요청을 처리
public class ReactionController {

    private final ReactionService reactionService; // 반응 비즈니스 로직 처리 서비스
    private final UserService userService; // 사용자 관련 서비스

    /**
     * 게시글에 대한 좋아요/싫어요 토글
     *
     * @param postId 토글할 게시글 ID
     * @param type 반응 타입 (LIKE 또는 DISLIKE)
     * @param loginUserDto 세션에서 가져온 로그인 사용자 정보
     * @return ReactionService.ReactionSummary 해당 게시글의 최신 좋아요/싫어요 상태 및 총합
     *
     * 동작:
     *  1. 로그인 여부 확인
     *  2. 로그인 사용자 객체(User) 조회
     *  3. reactionService.togglePost() 호출하여 반응 토글 처리
     *  4. 토글 후 최신 상태 반환
     */
    @PostMapping("/posts/{postId}")
    public ReactionService.ReactionSummary togglePost(
            @PathVariable Long postId,
            @RequestParam ReactionType type,
            @SessionAttribute(value = "loginUser", required = false) UserResponseDto loginUserDto) {

        if (loginUserDto == null) {
            // 로그인하지 않은 경우 → 401 Unauthorized 반환
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        // User 엔티티 조회
        User user = userService.findById(loginUserDto.getId());

        // 게시글 반응 토글 처리 후 최신 상태 반환
        return reactionService.togglePost(user, postId, type);
    }

    /**
     * 댓글에 대한 좋아요/싫어요 토글
     *
     * @param commentId 토글할 댓글 ID
     * @param type 반응 타입 (LIKE 또는 DISLIKE)
     * @param loginUserDto 세션에서 가져온 로그인 사용자 정보
     * @return ReactionService.ReactionSummary 해당 댓글의 최신 좋아요/싫어요 상태 및 총합
     *
     * 동작:
     *  1. 로그인 여부 확인
     *  2. 로그인 사용자 객체(User) 조회
     *  3. reactionService.toggleComment() 호출하여 반응 토글 처리
     *  4. 토글 후 최신 상태 반환
     */
    @PostMapping("/comments/{commentId}")
    public ReactionService.ReactionSummary toggleComment(
            @PathVariable Long commentId,
            @RequestParam ReactionType type,
            @SessionAttribute("loginUser") UserResponseDto loginUserDto) {

        if (loginUserDto == null) {
            // 로그인하지 않은 경우 → 401 Unauthorized 반환
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        // User 엔티티 조회
        User user = userService.findById(loginUserDto.getId());

        // 댓글 반응 토글 처리 후 최신 상태 반환
        return reactionService.toggleComment(user, commentId, type);
    }
}
