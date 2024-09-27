package paengbeom.syono.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Balance;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import paengbeom.syono.exception.CustomException;
import paengbeom.syono.repository.UserRepository;
import paengbeom.syono.util.RedisUtil;

import java.security.SecureRandom;

import static paengbeom.syono.exception.ExceptionResponseCode.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class SmsService {

    @Value("${coolsms.api.key}")
    private String API_KEY;
    @Value("${coolsms.api.secret}")
    private String API_SECRET;
    @Value("${coolsms.api.from}")
    private String FROM;
    @Value("${coolsms.api.domain}")
    private String DOMAIN;
    @Value("${spring.data.redis.before.expiration.time}")
    private long BEFORE_EXPIRATION_TIME;
    @Value("${spring.data.redis.after.expiration.time}")
    private long AFTER_EXPIRATION_TIME;

    private final RedisUtil redisUtil;
    private final UserRepository userRepository;
    private DefaultMessageService messageService;

    /**
     * 초기화 메서드.
     * API_KEY, API_SECRET, DOMAIN을 사용하여 CoolSMS 서비스 인스턴스(messageService)를 초기화.
     */
    @PostConstruct
    private void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET, DOMAIN);
    }

    /**
     * 핸드폰 번호로 SMS 인증 코드를 전송하는 메서드.
     * - 중복된 핸드폰 번호는 허용되지 않음.
     * - 잔액이 부족하면 예외 발생.
     * - Redis에 인증 코드를 저장하고 만료 시간을 설정.
     *
     * @param toPhone 인증 코드를 전송할 핸드폰 번호
     * @return 생성된 인증 코드
     */
    public String sendSmsForCertification(String toPhone) {
        userRepository.findByphone(toPhone)
                .ifPresent(user -> {
                    throw new CustomException(DUPLICATED_PHONE.getCode(), DUPLICATED_PHONE.getMessage());
                });

        float balance = getBalance();
        if (balance < 20) {
            throw new CustomException(INSUFFICIENT_BALANCE.getCode(), INSUFFICIENT_BALANCE.getMessage());
        }

        String certificationCode = createCertificationCode();
        sendOne(toPhone, certificationCode);
        redisUtil.setDataExpire(toPhone, certificationCode, BEFORE_EXPIRATION_TIME);
        return certificationCode;
    }

    /**
     * 사용자가 입력한 인증 코드를 검증하는 메서드.
     * - Redis에 저장된 인증 코드와 비교하여 검증.
     * - 인증 성공 시 Redis에 'verified' 상태로 저장하고 만료 시간을 갱신.
     *
     * @param phone             검증할 핸드폰 번호
     * @param certificationCode 사용자가 입력한 인증 코드
     * @return 인증 성공 여부 (성공 시 true 반환)
     */
    public boolean verifySms(String phone, String certificationCode) {
        log.info("phone : {}, certificationCode : {}", phone, certificationCode);

        boolean validatedEmail = redisUtil.isExistData(phone);
        if (!validatedEmail) {
            throw new CustomException(INVALID_PHONE.getCode(), INVALID_PHONE.getMessage());
        }

        if (!redisUtil.getData(phone).equals(certificationCode)) {
            log.info("code = {}", redisUtil.getData(phone));
            log.info("code2 = {}", certificationCode);
            throw new CustomException(INVALID_PHONE_CODE.getCode(), INVALID_PHONE_CODE.getMessage());
        }

        redisUtil.setDataExpire(phone, "verified", AFTER_EXPIRATION_TIME);
        return true;
    }

    /**
     * 핸드폰 번호로 SMS 메시지를 전송하는 메서드.
     *
     * @param to                메시지를 전송할 대상 핸드폰 번호
     * @param certificationCode 전송할 인증 번호
     * @return 전송 결과 (SingleMessageSentResponse)
     */
    public SingleMessageSentResponse sendOne(String to, String certificationCode) {
        Message message = new Message();
        message.setFrom(FROM);
        message.setTo(to);
        message.setText("[SYONO] 회원가입 인증번호: " + certificationCode);

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        log.info("response = {}", response);
        return response;
    }

    /**
     * CoolSMS API에서 잔액을 조회하는 메서드.
     *
     * @return 남은 잔액 (포인트)
     */
    public float getBalance() {
        Balance balance = this.messageService.getBalance();
        log.info("balance = {}", balance);

        return balance.getPoint() == null ? 0 : balance.getPoint();
    }

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
}
