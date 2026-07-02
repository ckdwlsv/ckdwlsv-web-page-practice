package com.example.demo.user.service;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserRole;
import com.example.demo.user.dto.UserResponseDto;
import com.example.demo.user.dto.UserUpdateRequestDto;
import com.example.demo.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void updateProfile_shouldUpdateNicknameAndPassword() {
        User user = User.builder()
                .id(1L)
                .username("tester")
                .password("old-password")
                .nickname("old-nick")
                .role(UserRole.USER)
                .build();

        UserUpdateRequestDto dto = new UserUpdateRequestDto();
        dto.setNickname("new-nick");
        dto.setPassword("new-password");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-new-password");

        UserResponseDto updated = userService.updateProfile(1L, dto);

        assertThat(updated.getNickname()).isEqualTo("new-nick");
        assertThat(user.getNickname()).isEqualTo("new-nick");
        assertThat(user.getPassword()).isEqualTo("encoded-new-password");
        verify(userRepository).save(user);
    }
}
