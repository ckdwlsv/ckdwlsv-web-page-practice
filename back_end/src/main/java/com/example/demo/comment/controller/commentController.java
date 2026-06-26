package com.example.demo.comment.controller;

import com.example.demo.comment.dto.CommentRequestDto;
import com.example.demo.comment.dto.CommentResponseDto;
import com.example.demo.comment.service.CommentService;
import com.example.demo.user.domain.User;
import com.example.demo.user.dto.UserResponseDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller // Spring MVC에서 Controller임을 나타냄. View(HTML) 반환용
@RequiredArgsConstructor // final 필드(CommentService)를 생성자 주입
@RequestMapping("/posts") // 공통 URL prefix
public class commentController {

    private final CommentService commentService; // 댓글 관련 비즈니스 로직을 처리하는 서비스

    // ---------------------- 댓글 등록 ----------------------
    @PostMapping("/{postId}")
    // URL 예: POST /posts/5
    public String addComment(@PathVariable Long postId, // PathVariable로 게시글 ID 추출
                             @RequestParam String content, // 댓글 내용은 요청 파라미터로 받음
                             HttpSession session){ // 세션에서 로그인 정보 확인

        // 로그인한 사용자 정보 가져오기
        UserResponseDto loginUserDto = (UserResponseDto) session.getAttribute("loginUser");

        if (loginUserDto == null) {
            // 로그인 안 되어 있으면 예외 발생
            throw new RuntimeException("로그인이 필요합니다.");
        }

        // 댓글 요청 DTO 생성
        CommentRequestDto dto = new CommentRequestDto();
        dto.setPostId(postId);
        dto.setContent(content);

        // 댓글 생성 서비스 호출
        commentService.createComment(dto, loginUserDto);

        // 댓글 작성 후 게시글 상세 페이지로 리다이렉트
        return "redirect:/posts/" + postId;
    }

    // ---------------------- 댓글 삭제 ----------------------
    @PostMapping("/comments/{commentId}/delete")
    // URL 예: POST /posts/comments/3/delete
    public String deleteComment(@PathVariable Long commentId, HttpSession session){
        // 로그인 체크
        UserResponseDto loginUserDto = (UserResponseDto) session.getAttribute("loginUser");
        if(loginUserDto == null) throw new RuntimeException("로그인이 필요합니다.");

        // 댓글 삭제 서비스 호출
        // 삭제 후 댓글이 속한 게시글 ID 반환
        Long postId = commentService.deleteComment(commentId, loginUserDto.getId(), loginUserDto.getRole());

        // 삭제 후 원래 게시글로 리다이렉트
        return "redirect:/posts/"+postId;
    }

    // ---------------------- 댓글 좋아요 ----------------------
    @PostMapping("/comments/{id}/like")
    // URL 예: POST /posts/comments/3/like
    public Map<String,Object> likeComment(@PathVariable Long id, HttpSession session){
        UserResponseDto loginUser = (UserResponseDto) session.getAttribute("loginUser");
        if(loginUser == null){
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        // 댓글 좋아요 토글 서비스 호출
        // true는 '좋아요' 표시
        return commentService.toggleLike(id, loginUser.getId(), true);
    }

    // ---------------------- 댓글 싫어요 ----------------------
    @PostMapping("/comments/{id}/dislike")
    // URL 예: POST /posts/comments/3/dislike
    public Map<String,Object> dislikeComment(@PathVariable Long id, HttpSession session){
        UserResponseDto loginUser = (UserResponseDto) session.getAttribute("loginUser");
        if(loginUser == null){
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        // 댓글 싫어요 토글 서비스 호출
        // false는 '싫어요' 표시
        return commentService.toggleLike(id, loginUser.getId(), false);
    }

}
