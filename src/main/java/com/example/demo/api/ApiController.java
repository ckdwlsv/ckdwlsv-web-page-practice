package com.example.demo.api;

import com.example.demo.mypage.dto.CommentDto;
import com.example.demo.mypage.dto.PostDto;
import com.example.demo.mypage.dto.UserProfileDto;
import com.example.demo.mypage.service.MyPageService;
import com.example.demo.post.dto.PostResponseDto;
import com.example.demo.post.service.PostService;
import com.example.demo.user.dto.UserLoginRequestDto;
import com.example.demo.user.dto.UserResponseDto;
import com.example.demo.user.dto.UserSignupRequestDto;
import com.example.demo.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {

    private final UserService userService;
    private final PostService postService;
    private final MyPageService myPageService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserSignupRequestDto dto) {
        try {
            userService.signup(dto);
            return ResponseEntity.ok(Map.of("message", "회원가입이 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequestDto dto, HttpSession session) {
        try {
            UserResponseDto user = userService.login(dto);
            session.setAttribute("loginUser", user);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "로그아웃되었습니다."));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        Object loginUser = session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.ok(Map.of("loggedIn", false));
        }
        return ResponseEntity.ok(Map.of("loggedIn", true, "user", loginUser));
    }

    @GetMapping("/posts")
    public ResponseEntity<?> posts(@RequestParam(defaultValue = "0") int page) {
        Page<PostResponseDto> result = postService.findAllPaged(page, 10);
        return ResponseEntity.ok(result.getContent());
    }

    @GetMapping("/mypage")
    public ResponseEntity<?> mypage(HttpSession session) {
        Object loginUser = session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        UserResponseDto user = (UserResponseDto) loginUser;
        UserProfileDto profile = myPageService.getUserProfile(user.getId());
        List<PostDto> posts = myPageService.getMyPosts(user.getId());
        List<CommentDto> comments = myPageService.getMyComments(user.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("profile", profile);
        response.put("myPosts", posts);
        response.put("myComments", comments);
        return ResponseEntity.ok(response);
    }
}
