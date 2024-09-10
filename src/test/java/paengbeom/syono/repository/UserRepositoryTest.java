package paengbeom.syono.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import paengbeom.syono.entity.Role;
import paengbeom.syono.entity.User;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class UserRepositoryTest {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    UserRepositoryTest(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Test
    @DisplayName("user 등록 및 비밀번화 암호화 테스트")
    void register() {

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

        User savedUser = userRepository.findByEmail(user.getEmail()).orElseThrow();

        assertThat(savedUser).isEqualTo(user);
        assertThat(encoder.matches("password", user.getPassword())).isTrue();
    }
}