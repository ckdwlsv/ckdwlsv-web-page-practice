package com.example.demo.user.controller;

import com.example.demo.user.dto.UserLoginRequestDto;
import com.example.demo.user.dto.UserResponseDto;
import com.example.demo.user.dto.UserSignupRequestDto;
import com.example.demo.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * UserController
 *
 * 사용자 관련 HTTP 요청을 처리하는 컨트롤러
 * - 회원가입(Signup)
 * - 로그인(Login)
 * - 로그아웃(Logout)
 *
 * 세션 기반 인증을 사용하며, 뷰 템플릿과 연동
 */
@Getter
@Controller // Spring MVC 컨트롤러임을 표시
@RequiredArgsConstructor // final 필드(userService)를 자동 생성자로 주입
@RequestMapping("/user") // 이 컨트롤러의 모든 URL 경로는 /user로 시작
public class UserController {

    private final UserService userService; // 사용자 관련 비즈니스 로직 서비스

    /**
     * 회원가입 페이지 요청(GET)
     *
     * @return 회원가입 뷰 이름 "user/signup"
     */
    @GetMapping("/signup")
    public String signup() {
        return "user/signup"; // 회원가입 페이지 반환
    }

    /**
     * 회원가입 처리 요청(POST)
     *
     * @param dto 회원가입 요청 DTO (username, password, nickname 등)
     * @param model 뷰에 전달할 데이터 저장
     * @return 성공 시 로그인 페이지로 리다이렉트, 실패 시 회원가입 페이지 유지
     */
    @PostMapping("/signup")
    public String signup(@ModelAttribute UserSignupRequestDto dto, Model model) {
        System.out.println("nick:" + dto.getNickname()); // 디버깅용 닉네임 출력
        try {
            userService.signup(dto); // 회원가입 서비스 호출
            return "redirect:/user/login"; // 가입 성공 → 로그인 페이지로 이동
        } catch (IllegalArgumentException e) {
            // 예외 발생 시 메시지를 모델에 추가 → 뷰에서 에러 표시 가능
            model.addAttribute("error", e.getMessage());
            return "user/signup"; // 가입 실패 → 다시 회원가입 페이지
        }
    }

    /**
     * 로그인 페이지 요청(GET)
     *
     * @return 로그인 뷰 이름 "user/login"
     */
    @GetMapping("/login")
    public String login() {
        return "user/login"; // 로그인 페이지 반환
    }

    /**
     * 로그인 처리 요청(POST)
     *
     * @param dto 로그인 요청 DTO (username, password)
     * @param session 세션 객체, 로그인 성공 시 사용자 정보를 세션에 저장
     * @param model 뷰에 전달할 데이터 저장
     * @return 성공 시 홈 화면으로 리다이렉트, 실패 시 로그인 페이지 유지
     */
    @PostMapping("/login")
    public String login(@ModelAttribute UserLoginRequestDto dto, HttpSession session, Model model) {
        try {
            // 로그인 시도
            UserResponseDto user = userService.login(dto); // 로그인 성공 시 사용자 정보 반환
            session.setAttribute("loginUser", user); // 세션에 로그인 사용자 정보 저장
            return "redirect:/"; // 홈 화면으로 리다이렉트
        } catch (IllegalArgumentException e) {
            // 로그인 실패 시 에러 메시지 전달
            model.addAttribute("error", e.getMessage());
            return "user/login"; // 로그인 페이지 유지
        }
    }

    /**
     * 로그아웃 처리 요청(POST)
     *
     * @param session 현재 사용자 세션
     * @return 홈 화면으로 리다이렉트
     */
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 무효화 → 로그아웃 처리
        return "redirect:/"; // 홈 화면으로 이동
    }

}
