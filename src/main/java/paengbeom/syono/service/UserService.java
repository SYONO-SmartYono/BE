package paengbeom.syono.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import paengbeom.syono.dto.user.SignUpResponseDto;
import paengbeom.syono.entity.Role;
import paengbeom.syono.entity.User;
import paengbeom.syono.entity.UserMapper;
import paengbeom.syono.exception.CustomException;
import paengbeom.syono.repository.UserRepository;
import paengbeom.syono.util.CodefUtil;
import paengbeom.syono.util.RedisUtil;

import java.util.UUID;

import static paengbeom.syono.exception.ExceptionResponseCode.UNVERIFIED_EMAIL;
import static paengbeom.syono.exception.ExceptionResponseCode.UNVERIFIED_PHONE;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RedisUtil redisUtil;
    private final CodefUtil codefUtil;
    private final PasswordEncoder passwordEncoder;

    @Value("${default.profile.img}")
    private String DEFAULT_PROFILE_IMG;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User findedUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return UserMapper.INSTANCE.userToSecurityUserDto(findedUser);
    }

    /**
     * 사용자의 이메일, 비밀번호, 전화번호를 받아 회원가입을 처리하는 메서드.
     * 이메일과 전화번호가 인증된 상태인지 확인한 후, 새로운 사용자를 생성하고 저장합니다.
     * 저장된 사용자 정보를 바탕으로 회원가입 응답 DTO(SignUpResponseDto)를 반환합니다.
     *
     * @param email    사용자의 이메일 (인증된 상태여야 함)
     * @param password 사용자가 설정할 비밀번호 (암호화되어 저장됨)
     * @param phone    사용자의 전화번호 (인증된 상태여야 함)
     * @return SignUpResponseDto 생성된 사용자의 이메일, 전화번호, 닉네임, 역할, 프로필 이미지 정보를 포함한 DTO
     * @throws CustomException 이메일 또는 전화번호가 인증되지 않은 경우 예외를 발생시킴
     */
    public SignUpResponseDto signUp(String email, String password, String phone) {
        log.info("email: {}, password: {}, phone: {}", email, password, phone);
        if (!"verified".equals(redisUtil.getData(email))) {
            throw new CustomException(UNVERIFIED_EMAIL.getCode(), UNVERIFIED_EMAIL.getMessage());
        }
        if (!"verified".equals(redisUtil.getData(phone))) {
            throw new CustomException(UNVERIFIED_PHONE.getCode(), UNVERIFIED_PHONE.getMessage());
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .phone(phone)
                .nickname(UUID.randomUUID().toString().substring(0, 8))
                .role(Role.ROLE_USER)
                .profileImg(DEFAULT_PROFILE_IMG)
                .build();
        User savedUser = userRepository.save(user);

        redisUtil.delData(email);
        redisUtil.delData(phone);

        return SignUpResponseDto.builder()
                .email(savedUser.getEmail())
                .phone(savedUser.getPhone())
                .nickname(savedUser.getNickname())
                .role(savedUser.getRole().getDescription())
                .profileImg(savedUser.getProfileImg())
                .build();
    }

}
