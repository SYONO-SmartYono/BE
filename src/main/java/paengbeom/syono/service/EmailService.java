package paengbeom.syono.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import paengbeom.syono.exception.CustomException;
import paengbeom.syono.repository.UserRepository;
import paengbeom.syono.util.RedisUtil;

import java.security.SecureRandom;

import static paengbeom.syono.exception.ExceptionResponseCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;

    @Value("${spring.data.redis.before.expiration.time}")
    private long BEFORE_EXPIRATION_TIME;

    @Value("${spring.data.redis.after.expiration.time}")
    private long AFTER_EXPIRATION_TIME;

    /**
     * 6자리 인증 번호를 생성하는 메서드.
     * SecureRandom을 사용하여 임의의 숫자를 생성.
     *
     * @return 6자리 인증번호
     */
    public String createCertificationCode() {
        int upperLimit = (int) Math.pow(10, 6);
        return String.valueOf(new SecureRandom().nextInt(upperLimit));
    }

    /**
     * 이메일로 회원가입 인증 번호를 발송하는 메서드.
     * 이미 등록된 이메일은 오류를 발생시킴.
     * 인증번호를 Redis에 저장하고, 이메일로 발송.
     *
     * @param toEmail 인증번호를 발송할 대상 이메일
     * @return 생성된 인증번호
     */
    public String sendEmailForCertification(String toEmail) {
        userRepository.findByEmail(toEmail)
                .ifPresent(user -> {
                    throw new CustomException(DUPLICATED_EMAIL.getCode(), DUPLICATED_EMAIL.getMessage());
                });

        String certificationCode = createCertificationCode();
        String title = "[Web 발신] SYONO 회원 가입 인증 이메일 입니다.";
        String content = "인증 번호 : " + certificationCode;
        sendMail(toEmail, title, content);
        redisUtil.setDataExpire(toEmail, certificationCode, BEFORE_EXPIRATION_TIME);
        return certificationCode;
    }

    /**
     * 이메일을 발송하는 메서드.
     * 이메일 발송에 실패하면 CustomException을 발생시킴.
     *
     * @param toEmail 수신자 이메일 주소
     * @param title   이메일 제목
     * @param content 이메일 내용
     */
    public void sendMail(String toEmail, String title, String content) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setTo(toEmail);
            helper.setSubject(title);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("email send error", e);
            throw new CustomException(EMAIL_SEND_FAILURE.getCode(), EMAIL_SEND_FAILURE.getMessage());
        }
    }

    /**
     * 이메일 인증 번호를 검증하는 메서드.
     * Redis에 저장된 인증 번호와 사용자가 입력한 인증 번호를 비교하여 일치 여부를 확인.
     * - 입력된 이메일이 Redis에 존재하지 않으면 CustomException을 발생시킴 (유효하지 않은 이메일).
     * - 인증 번호가 일치하지 않으면 CustomException을 발생시킴 (유효하지 않은 인증 번호).
     * - 인증이 성공하면 해당 이메일을 "verified" 상태로 변경하고, 만료 시간을 AFTER_EXPIRATION_TIME으로 설정.
     *
     * @param email 이메일 주소 (인증하려는 대상 이메일)
     * @param certificationCode 사용자가 입력한 인증 번호
     * @return 인증 성공 시 true 반환
     * @throws CustomException 유효하지 않은 이메일이거나 인증 번호가 일치하지 않을 때 발생
     */
    public boolean verifyEmail(String email, String certificationCode) {
        log.info("email : {}, certificationCode : {}", email, certificationCode);

        boolean validatedEmail = redisUtil.isExistData(email);
        if (!validatedEmail) {
            throw new CustomException(INVALID_EMAIL.getCode(), INVALID_EMAIL.getMessage());
        }

        if (!redisUtil.getData(email).equals(certificationCode)) {
            log.info("code = {}", redisUtil.getData(email));
            log.info("code2 = {}", certificationCode);
            throw new CustomException(INVALID_EMAIL_CODE.getCode(), INVALID_EMAIL_CODE.getMessage());
        }

        redisUtil.setDataExpire(email, "verified", AFTER_EXPIRATION_TIME);
        return true;
    }
}
