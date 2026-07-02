package com.example.demo.user.service;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserRole;
import com.example.demo.user.dto.UserLoginRequestDto;
import com.example.demo.user.dto.UserResponseDto;
import com.example.demo.user.dto.UserSignupRequestDto;
import com.example.demo.user.dto.UserUpdateRequestDto;
import com.example.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 사용자(User) 관련 비즈니스 로직을 처리하는 서비스 클래스
 *
 * - 회원가입, 로그인, 사용자 조회 등의 핵심 기능 제공
 * - UserRepository를 통해 DB 접근
 * - BCryptPasswordEncoder로 비밀번호 암호화 및 검증
 */
@Service
@RequiredArgsConstructor // final 필드(userRepository, passwordEncoder)를 생성자로 자동 주입
public class UserService {

    private final UserRepository userRepository; // 사용자 정보를 DB에서 관리
    private final BCryptPasswordEncoder passwordEncoder; // 비밀번호 암호화 및 검증

    /**
     * 사용자 ID로 User 엔티티 조회
     *
     * @param id 조회할 사용자 ID
     * @return User 조회된 사용자 엔티티
     * @throws IllegalArgumentException 사용자 존재하지 않으면 예외 발생
     */
    public User findById(long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    /**
     * 회원가입 처리 메서드
     *
     * - username 중복 확인
     * - 비밀번호 암호화
     * - 기본 역할(USER) 부여
     * - DB에 사용자 정보 저장
     *
     * @param dto 회원가입 요청 데이터(UserSignupRequestDto)
     * @throws IllegalArgumentException 이미 존재하는 사용자일 경우 예외 발생
     */
    public void signup(UserSignupRequestDto dto) {
        // 1. username 중복 확인
        Optional<User> existing = userRepository.findByUsername(dto.getUsername());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        // 2. 비밀번호 암호화
        String encodedPw = passwordEncoder.encode(dto.getPassword());

        // 3. User 객체 생성 및 기본 역할 설정
        UserRole role = Boolean.TRUE.equals(dto.getAdmin()) ? UserRole.ADMIN : UserRole.USER;

        User user = User.builder()
                .username(dto.getUsername())
                .password(encodedPw)
                .nickname(dto.getNickname())
                .role(role)
                .build();

        // 4. DB에 저장
        userRepository.save(user);
    }

    /**
     * 로그인 처리 메서드
     *
     * - username으로 사용자 조회
     * - 비밀번호 일치 여부 확인
     * - 로그인 성공 시 UserResponseDto 반환
     *
     * @param dto 로그인 요청 데이터(UserLoginRequestDto)
     * @return UserResponseDto 로그인 성공 사용자 정보 DTO
     * @throws IllegalArgumentException 사용자 없거나 비밀번호 불일치 시 예외 발생
     */
    public UserResponseDto login(UserLoginRequestDto dto) {
        // 1. username으로 사용자 조회
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. DTO 변환 후 반환
        return UserResponseDto.from(user);
    }

    public UserResponseDto findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return UserResponseDto.from(user);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        userRepository.delete(user);
    }

    public UserResponseDto updateProfile(Long userId, UserUpdateRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (dto.getNickname() != null && !dto.getNickname().isBlank()) {
            user.setNickname(dto.getNickname());
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        userRepository.save(user);
        return UserResponseDto.from(user);
    }
}
