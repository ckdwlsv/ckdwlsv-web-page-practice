// 패키지 선언: 이 클래스가 속한 패키지를 지정
package com.example.demo.mypage.controller;

// 필요한 클래스 import
import com.example.demo.mypage.dto.CommentDto;       // 댓글 DTO
import com.example.demo.mypage.dto.PostDto;          // 게시글 DTO
import com.example.demo.mypage.dto.UserProfileDto;   // 유저 프로필 DTO
import com.example.demo.mypage.service.MyPageService; // 마이페이지 서비스
import com.example.demo.user.domain.User;           // User 엔티티
import com.example.demo.user.dto.UserResponseDto;    // User DTO
import jakarta.servlet.http.HttpSession;            // 세션 처리
import lombok.RequiredArgsConstructor;              // final 필드 생성자 자동 생성
import org.springframework.stereotype.Controller;    // Spring MVC Controller
import org.springframework.ui.Model;                // 뷰(Model)에 데이터 전달
import org.springframework.web.bind.annotation.GetMapping; // GET 요청 매핑

import java.util.List; // 리스트 컬렉션

/**
 * 마이페이지 관련 요청을 처리하는 컨트롤러
 */
@Controller
@RequiredArgsConstructor // final 필드(myPageService)에 대한 생성자 자동 생성
public class MyPageController {

    // 마이페이지 비즈니스 로직을 처리하는 서비스 주입
    private final MyPageService myPageService;

    /**
     * GET /mypage/mypage 요청 처리
     * 로그인한 사용자의 프로필, 게시글, 댓글 정보를 모델에 담아 뷰로 전달
     * @param session HttpSession에서 로그인 정보 조회
     * @param model 뷰에 데이터를 전달
     * @return mypage/mypage.html 뷰 이름
     */
    @GetMapping("/mypage/mypage")
    public String getMyPage(HttpSession session, Model model) {

        // 세션에서 로그인 사용자 정보 조회
        Object sessionUser = session.getAttribute("loginUser");

        // 로그인하지 않은 경우 예외 처리
        if (sessionUser == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        UserResponseDto loginUser;

        // 세션 사용자 타입에 따라 DTO 생성
        if (sessionUser instanceof UserResponseDto) {
            // 이미 DTO인 경우 바로 사용
            loginUser = (UserResponseDto) sessionUser;
        } else if (sessionUser instanceof User) {
            // 엔티티인 경우 DTO로 변환
            User user = (User) sessionUser;
            loginUser = UserResponseDto.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .nickname(user.getNickname())
                    .role(user.getRole())
                    .build();
        } else {
            // 알 수 없는 타입이면 예외 처리
            throw new IllegalStateException("알 수 없는 세션 사용자 타입: " + sessionUser.getClass());
        }

        // 사용자 ID 추출
        Long userId = loginUser.getId();

        // 서비스에서 사용자 프로필, 게시글, 댓글 조회
        UserProfileDto profile = myPageService.getUserProfile(userId);
        List<PostDto> posts = myPageService.getMyPosts(userId);
        List<CommentDto> comments = myPageService.getMyComments(userId);

        // 모델에 데이터를 담아 뷰로 전달
        model.addAttribute("profile", profile);
        model.addAttribute("myPosts", posts);       // 템플릿 변수명에 맞춤
        model.addAttribute("myComments", comments);

        // 반환값: 마이페이지 뷰 이름
        return "mypage/mypage";
    }
}
