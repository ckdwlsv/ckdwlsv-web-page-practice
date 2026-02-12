// 패키지 선언: 이 클래스가 속한 패키지를 지정
package com.example.demo.global.Config;

// Spring에서 제공하는 어노테이션과 BCryptPasswordEncoder를 사용하기 위해 import
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @Configuration
 * 이 클래스가 Spring의 설정 클래스임을 나타냅니다.
 * 즉, 스프링 컨테이너에 Bean을 등록하거나, 애플리케이션 설정을 정의할 때 사용됩니다.
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * @Bean
     * 이 메서드가 반환하는 객체를 스프링 컨테이너에 Bean으로 등록합니다.
     * Bean으로 등록되면, 다른 클래스에서 @Autowired 또는 생성자 주입으로 쉽게 사용할 수 있습니다.
     *
     * passwordEncoder()
     * Spring Security에서 사용자 비밀번호를 암호화할 때 사용되는 BCryptPasswordEncoder 객체를 생성하여 반환합니다.
     * BCryptPasswordEncoder는 단방향 해시 함수를 사용하여 비밀번호를 안전하게 암호화하며,
     * 동일한 비밀번호라도 매번 다른 해시 값을 생성하여 보안을 강화합니다.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        // BCryptPasswordEncoder 객체 생성 후 반환
        return new BCryptPasswordEncoder();
    }
}
