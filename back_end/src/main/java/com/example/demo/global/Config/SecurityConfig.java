// 패키지 선언: 이 클래스가 속한 패키지를 지정
package com.example.demo.global.Config;

// Spring Security와 관련된 클래스 import
import com.example.demo.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @Configuration
 * 이 클래스가 Spring 설정 클래스임을 나타냅니다.
 * 스프링 컨테이너가 이 클래스를 읽고 Bean 등록 및 보안 설정을 수행합니다.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * @Bean
     * 이 메서드가 반환하는 SecurityFilterChain 객체를 스프링 컨테이너에 Bean으로 등록합니다.
     * SecurityFilterChain은 HTTP 요청에 대한 보안 필터 체인을 구성합니다.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // HttpSecurity 객체를 사용해 웹 보안 정책을 설정
        http
                // CSRF 보호 비활성화
                // CSRF는 웹에서 요청 위조 공격을 막기 위한 기능입니다.
                // REST API 서버나 토큰 기반 인증 환경에서는 종종 비활성화합니다.
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/login", "/api/signup", "/api/logout").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts", "/api/posts/*").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/api/me").permitAll()
                        .anyRequest().authenticated())
                .formLogin(login -> login.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // 설정이 완료된 SecurityFilterChain 객체를 생성하여 반환
        return http.build();
    }
}
