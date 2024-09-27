package paengbeom.syono.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import paengbeom.syono.dto.sms.SmSCertificationRequestDto;
import paengbeom.syono.dto.sms.SmsCertificationResponseDto;
import paengbeom.syono.dto.sms.SmsVerificationRequestDto;
import paengbeom.syono.dto.sms.SmsVerificationResponseDto;
import paengbeom.syono.service.SmsService;

@Slf4j
@RequestMapping("/sms")
@RequiredArgsConstructor
@RestController
public class SmsController {

    private final SmsService smsService;

    @PostMapping("/certification")
    public ResponseEntity<SmsCertificationResponseDto> sendSmsForJoin(@Valid @RequestBody SmSCertificationRequestDto smsRequestDto) {
        log.info("smsRequestDto = {}", smsRequestDto);
        String certificationCode = smsService.sendSmsForCertification(smsRequestDto.getPhone());
        return new ResponseEntity<>(new SmsCertificationResponseDto(certificationCode), HttpStatus.OK);
    }

    @PostMapping("/verification")
    public ResponseEntity<?> verifyCertificationNumber(@Valid @RequestBody SmsVerificationRequestDto smsResponseDto) {
        log.info("smsResponseDto = {}", smsResponseDto);
        boolean verifySmsCode = smsService.verifySms(smsResponseDto.getPhone(), smsResponseDto.getCertificationCode());
        return new ResponseEntity<>(new SmsVerificationResponseDto(verifySmsCode), HttpStatus.OK);
    }
}
