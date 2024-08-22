package paengbeom.syono.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import paengbeom.syono.entity.Users;

@SpringBootTest
@Transactional
@RequiredArgsConstructor
class UserRepositoryTest {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    Users user = Users.builder()
            .email("dlsqja970210@gmail.com")
            .password(encoder.encode("password"))
            .phone("01093799366")
            .

}