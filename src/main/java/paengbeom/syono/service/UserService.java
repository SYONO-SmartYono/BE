package paengbeom.syono.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import paengbeom.syono.dto.UserFormDto;
import paengbeom.syono.entity.User;
import paengbeom.syono.entity.UserMapper;
import paengbeom.syono.repository.UserRepository;
import paengbeom.syono.util.RedisUtil;
import paengbeom.syono.util.SmsUtil;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final SmsUtil smsUtil;
    private final RedisUtil redisUtil;

    @Value("${spring.data.redis.expiration.time}")
    private long EXPIRATION_TIME;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User findedUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return UserMapper.INSTANCE.userToSecurityUserDto(findedUser);
    }

    public String signUp(UserFormDto userFormDto) {
        log.info("userFormDto={}", userFormDto);
        User user = UserMapper.INSTANCE.userFormDtoToUser(userFormDto);
        log.info("email={}", user.getEmail());
        log.info("password={}", user.getPassword());
        userRepository.save(user);

        return "ok";
    }

    public String sendSmsForCertification(String to) {
        float balance = smsUtil.getBalance();
        if (balance < 20) {
            throw new RuntimeException("잔액이 부족합니다!");
        }
        String certificationNumber = smsUtil.createCertificationNumber();
        smsUtil.sendOne(to, certificationNumber);
        redisUtil.setDataExpire(to, certificationNumber, EXPIRATION_TIME);
        return certificationNumber;
    }

    public boolean verifySms(String phone, String certificationNumber) {
        boolean validatedEmail = redisUtil.isExistData(phone);
        if (!validatedEmail) {
            throw new RuntimeException("phoneNumber not exist");
        }
        return redisUtil.getData(phone).equals(certificationNumber);
    }


}
