// 패키지 선언: 이 클래스가 속한 패키지를 지정
package com.example.demo.post.controller;

// 필요한 클래스 import
import com.example.demo.comment.dto.CommentResponseDto;
import com.example.demo.comment.service.CommentService;
import com.example.demo.post.dto.PostRequestDto;
import com.example.demo.post.dto.PostResponseDto;
import com.example.demo.post.post.Post;
import com.example.demo.post.service.PostService;
import com.example.demo.user.domain.User;
import com.example.demo.user.dto.UserResponseDto;
import com.example.demo.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 게시글 관련 요청을 처리하는 컨트롤러 클래스
 */
@Controller
@RequiredArgsConstructor // final 필드를 매개변수로 받는 생성자 자동 생성
@RequestMapping("/posts") // "/posts"로 시작하는 URL 처리
public class PostController {

    private final PostService postService; // 게시글 서비스 의존성 주입
    private final UserService userService; // 사용자 서비스 의존성 주입
    private final CommentService commentService; // 댓글 서비스 주입

    /**
     * 게시글 목록 페이지 요청 처리
     * @param keyword 검색어 (선택)
     * @param page 페이지 번호 (기본 0)
     * @param model 뷰에 전달할 데이터
     * @return list.html
     */
    @GetMapping("/list")
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       Model model){
        int pageSize = 10; // 한 페이지당 게시글 개수
        Page<PostResponseDto> postsPage;

        if(keyword != null && !keyword.isBlank()){ // 키워드 검색
            postsPage = postService.searchPostsPaged(keyword, page, pageSize);
        } else { // 전체 게시글 조회
            postsPage = postService.findAllPaged(page, pageSize);
        }

        // 모델에 데이터 담기
        model.addAttribute("posts", postsPage.getContent()); // 현재 페이지 게시글 리스트
        model.addAttribute("Keyword", keyword);             // 검색어 유지
        model.addAttribute("postsPage", postsPage);         // 페이징 정보

        return "posts/list"; // list.html 반환
    }

    /**
     * 게시글 작성 폼 페이지 요청
     */
    @GetMapping("/write")
    public String write(){
        return "posts/write"; // write.html 반환
    }

    /**
     * 게시글 작성 요청 처리
     */
    @PostMapping("/write")
    public String write(@ModelAttribute PostRequestDto dto,
                        @RequestParam("files") List<MultipartFile> files,
                        HttpSession session){

        UserResponseDto loginUser = (UserResponseDto) session.getAttribute("loginUser"); // 세션 확인
        if(loginUser == null){
            return "redirect:/user/login"; // 로그인 안 했으면 로그인 페이지로 이동
        }

        User author = userService.findById(loginUser.getId()); // 작성자 엔티티 조회

        postService.create(dto, author, files); // 게시글 생성
        return "redirect:/posts/list"; // 게시글 목록 페이지로 이동
    }

    /**
     * 게시글 상세 페이지 요청 처리
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, HttpSession session){

        PostResponseDto post = postService.findById(id); // ID로 게시글 조회
        model.addAttribute("post", post); // 모델에 게시글 추가

        List<CommentResponseDto> comments = commentService.getCommentsByPostId(id);

        UserResponseDto loginUser = (UserResponseDto) session.getAttribute("loginUser");
        if (loginUser != null) {
            Long userId = loginUser.getId();

            // 댓글 좋아요/싫어요 상태 세팅
            commentService.setReactionSummary(comments, userId);

            // 게시글 좋아요/싫어요 상태 세팅
            Map<String, Object> postReaction = postService.getReactionStatus(id, userId);
            model.addAttribute("reactionSummary", postReaction);
        }

        model.addAttribute("comments", comments); // 댓글 모델에 추가
        model.addAttribute("session", session);    // 세션 전달

        return "posts/detail"; // detail.html 반환
    }

    /**
     * 게시글 수정 폼 요청
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, HttpSession session){
        UserResponseDto loginUser = (UserResponseDto) session.getAttribute("loginUser"); // 로그인 확인
        if(loginUser == null) {
            return "redirect:/user/login"; // 로그인 안 했으면 로그인 페이지로
        }

        PostResponseDto post = postService.findById(id); // 게시글 조회

        if(!post.getAuthorName().equals(loginUser.getNickname())){ // 작성자만 수정 가능
            return "redirect:/posts/" + id; // 권한 없으면 상세 페이지로 이동
        }

        model.addAttribute("post", post); // 모델에 게시글 추가
        return "posts/edit"; // edit.html 반환
    }

    /**
     * 게시글 수정 요청 처리
     */
    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @ModelAttribute PostRequestDto dto,
                       HttpSession session){
        UserResponseDto loginUser = (UserResponseDto) session.getAttribute("loginUser"); // 로그인 확인
        postService.update(id, dto, loginUser.getId()); // 게시글 수정
        return "redirect:/posts/" + id; // 수정 후 상세 페이지로 이동
    }

    /**
     * 게시글 삭제 요청 처리
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session){
        UserResponseDto loginUser = (UserResponseDto) session.getAttribute("loginUser"); // 로그인 확인
        postService.delete(id, loginUser.getId()); // 게시글 삭제 (작성자 확인 포함)
        return "redirect:/posts/list"; // 목록 페이지로 이동
    }

    /**
     * 게시글 좋아요
     */
    @PostMapping("/{id}/like")
    @ResponseBody
    public Map<String,Object> likePost(@PathVariable Long id, HttpSession session){
        UserResponseDto loginUser = (UserResponseDto) session.getAttribute("loginUser");
        if(loginUser == null){
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        return postService.toggleLike(id, loginUser.getId(), true); // true = 좋아요
    }

    /**
     * 게시글 싫어요
     */
    @PostMapping("/{id}/dislike")
    @ResponseBody
    public Map<String,Object> dislikePost(@PathVariable Long id, HttpSession session){
        UserResponseDto loginUser = (UserResponseDto) session.getAttribute("loginUser");
        if(loginUser == null){
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        return postService.toggleLike(id, loginUser.getId(), false); // false = 싫어요
    }
}
