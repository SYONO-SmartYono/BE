package paengbeom.syono.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import paengbeom.syono.dto.SignUpRequestDto;
import paengbeom.syono.entity.User;
import paengbeom.syono.entity.UserMapper;
import paengbeom.syono.repository.UserRepository;
import paengbeom.syono.util.RedisUtil;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final SmsService smsService;
    private final RedisUtil redisUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User findedUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return UserMapper.INSTANCE.userToSecurityUserDto(findedUser);
    }

    public String signUp(SignUpRequestDto signUpRequestDto) {
        log.info("userFormDto={}", signUpRequestDto);
        User user = UserMapper.INSTANCE.userFormDtoToUser(signUpRequestDto);
        log.info("email={}", user.getEmail());
        log.info("password={}", user.getPassword());
        userRepository.save(user);

        return "ok";
    }


}
