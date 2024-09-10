package paengbeom.syono.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import paengbeom.syono.util.RedisUtil;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final RedisUtil redisUtil;

    @Value("${spring.data.redis.expiration.time}")
    private long EXPIRATION_TIME;

    public String createCertificationNumber() {
        int upperLimit = (int) Math.pow(10, 6);
        return String.valueOf(new SecureRandom().nextInt(upperLimit));
    }

    public String sendEmailForCertification(String toEmail) {
        Instant start = Instant.now(); // 시작 시간 기록
        String certificationNumber = createCertificationNumber();
        String title = "회원 가입 인증 이메일 입니다.";
        String content =
                "SYONO에 방문해주셔서 감사합니다." +
                        "<br><br>" +
                        "인증 번호는 " + certificationNumber + "입니다." +
                        "<br>" +
                        "인증번호를 입력해주세요";
        sendMail(toEmail, title, content);
        redisUtil.setDataExpire(toEmail, certificationNumber, EXPIRATION_TIME);
        Instant end = Instant.now(); // 종료 시간 기록
        log.info("실행시간 : {}", Duration.between(start, end).toSeconds());
        return certificationNumber;
    }

    public void sendMail(String toEmail, String title, String cotent) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setTo(toEmail);
            helper.setSubject(title);
            helper.setText(cotent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("email send error", e);
            throw new RuntimeException(e);
        }
    }

    public boolean verifyEmail(String email, String certificationNumber) {
        boolean validatedEmail = redisUtil.isExistData(email);
        if (!validatedEmail) {
            throw new RuntimeException("email not exist");
        }
        return redisUtil.getData(email).equals(certificationNumber);
    }
}
