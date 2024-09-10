package paengbeom.syono.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Balance;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Slf4j
@Component
public class SmsUtil {

    @Value("${coolsms.api.key}")
    private String API_KEY;
    @Value("${coolsms.api.secret}")
    private String API_SECRET;
    @Value("${coolsms.api.from}")
    private String FROM;
    @Value("${coolsms.api.domain}")
    private String DOMAIN;

    private DefaultMessageService messageService;

    @PostConstruct
    private void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET, DOMAIN);
    }

    public SingleMessageSentResponse sendOne(String to, String certificationNumber) {
        Message message = new Message();
        // 발신번호 및 수신번호는 받드시 01012345678 형태로 입력되어야 함.
        message.setFrom(FROM);
        message.setTo(to);
        message.setText("[SYONO] 아래의 인증번호를 입력해주세요\n" + certificationNumber);

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        log.info("response = {}", response);
        return response;
    }

    public float getBalance() {
        Balance balance = this.messageService.getBalance();
        log.info("balance = {}", balance);

        return balance.getPoint() == null ? 0 : balance.getPoint();
    }

    public String createCertificationNumber() {
        int upperLimit = (int) Math.pow(10, 6);
        return String.valueOf(new SecureRandom().nextInt(upperLimit));
    }
}
