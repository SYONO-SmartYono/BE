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
import paengbeom.syono.dto.email.EmailCertificationRequestDto;
import paengbeom.syono.dto.email.EmailCertificationResponseDto;
import paengbeom.syono.dto.email.EmailVerificationRequestDto;
import paengbeom.syono.dto.email.EmailVerificationResponseDto;
import paengbeom.syono.service.EmailService;

@Slf4j
@RequestMapping("/email")
@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/certification")
    public ResponseEntity<EmailCertificationResponseDto> sendJoinEmail(@Valid @RequestBody EmailCertificationRequestDto emailCertificationRequestDto) {
        log.info("emailRequestDto = {}", emailCertificationRequestDto);
        String certificationCode = emailService.sendEmailForCertification(emailCertificationRequestDto.getEmail());
        return new ResponseEntity<>(new EmailCertificationResponseDto(certificationCode), HttpStatus.OK);
    }

    @PostMapping("/verification")
    public ResponseEntity<EmailVerificationResponseDto> verifyCertificationNumber(@Valid @RequestBody EmailVerificationRequestDto emailVerificationRequestDto) {
        log.info("emailResponseDto = {}", emailVerificationRequestDto);
        boolean verifyEmailCode = emailService.verifyEmail(emailVerificationRequestDto.getEmail(), emailVerificationRequestDto.getCertificationCode());
        return new ResponseEntity<>(new EmailVerificationResponseDto(verifyEmailCode), HttpStatus.OK);
    }
}
