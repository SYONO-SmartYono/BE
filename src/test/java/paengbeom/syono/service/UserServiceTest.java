package paengbeom.syono.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import paengbeom.syono.dto.SecurityUserDto;
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
    public void testLoadUserByUsername() {
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
}