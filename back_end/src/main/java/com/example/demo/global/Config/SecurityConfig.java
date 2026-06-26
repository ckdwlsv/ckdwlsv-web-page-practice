// 패키지 선언: 이 클래스가 속한 패키지를 지정
package com.example.demo.global.Config;

// Spring Security와 관련된 클래스 import
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @Configuration
 * 이 클래스가 Spring 설정 클래스임을 나타냅니다.
 * 스프링 컨테이너가 이 클래스를 읽고 Bean 등록 및 보안 설정을 수행합니다.
 */
@Configuration
public class SecurityConfig {

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

                // 모든 요청을 인증 없이 허용
                // auth.anyRequest().permitAll()은 모든 URL에 대해 로그인 없이 접근을 허용합니다.
                // 실제 서비스에서는 권한별 접근 제한 설정이 필요합니다.
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())

                // 스프링 시큐리티 기본 제공 폼 로그인 비활성화
                // formLogin().disable()을 사용하면 /login 페이지 자동 생성이 중단됩니다.
                .formLogin(login -> login.disable())

                // HTTP Basic 인증 비활성화
                // httpBasic().disable()을 통해 브라우저 기본 인증 창이 뜨는 것을 막습니다.
                .httpBasic(httpBasic -> httpBasic.disable());

        // 설정이 완료된 SecurityFilterChain 객체를 생성하여 반환
        return http.build();
    }
}
