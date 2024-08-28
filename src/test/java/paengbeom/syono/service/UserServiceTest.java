package paengbeom.syono.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import paengbeom.syono.dto.SecurityUserDto;
import paengbeom.syono.dto.UserFormDto;
import paengbeom.syono.entity.Role;
import paengbeom.syono.entity.User;
import paengbeom.syono.repository.UserRepository;


@SpringBootTest
@Slf4j
class UserServiceTest {
    private final UserService userService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    UserServiceTest(UserService userService, UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Test
    void testLoadUserByUsername() {
        User user = User.builder()
                .email("testEmail@email.com")
                .password(encoder.encode("password"))
                .nickname("nickname")
                .phone("01000000000")
                .role(Role.ROLE_USER)
                .connectedId("testId")
                .profileImg("testImgUrl")
                .build();
        userRepository.save(user);

        SecurityUserDto securityUserDto = (SecurityUserDto) userService.loadUserByUsername(user.getEmail());
        log.info("SecurityUserDto={}", securityUserDto);
    }

    @Test
    @DisplayName("회원가입 테스트")
    void testSignUp() {
        UserFormDto userFormDto = UserFormDto.builder()
                .email("testEmail@email.com")
                .password("password")
                .phone("01044443333")
                .connectedId("connectedId")
                .build();

        String res = userService.signUp(userFormDto);
        log.info("res={}", res);

        User savedUser = userRepository.findByEmail(userFormDto.getEmail()).get();
        log.info("savedUser={}", savedUser);
    }
}