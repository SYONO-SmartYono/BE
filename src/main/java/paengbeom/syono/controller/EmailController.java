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

    /**
     * 이메일 인증 코드를 전송하는 API
     * 다양한 상황(회원가입, 비밀번호 찾기 등)에서 이메일 인증 코드를 요청하는 경우 사용됩니다.
     *
     * @param emailCertificationRequestDto 이메일 인증 요청 정보를 담은 DTO
     * @return 이메일로 전송된 인증 코드를 담은 응답 DTO와 HTTP 상태 코드 OK (200)
     */
    @PostMapping("/certification")
    public ResponseEntity<EmailCertificationResponseDto> sendJoinEmail(@Valid @RequestBody EmailCertificationRequestDto emailCertificationRequestDto) {
        log.info("emailRequestDto = {}", emailCertificationRequestDto);
        String certificationCode = emailService.sendEmailForCertification(emailCertificationRequestDto.getEmail());
        return new ResponseEntity<>(new EmailCertificationResponseDto(certificationCode), HttpStatus.OK);
    }

    /**
     * 이메일로 전송된 인증 코드를 검증하는 API
     * 다양한 상황(회원가입, 비밀번호 찾기 등)에서 전송된 이메일 인증 코드를 검증하는 경우 사용됩니다.
     *
     * @param emailVerificationRequestDto 이메일 검증 요청 정보를 담은 DTO
     * @return 인증 코드 검증 결과(true/false)를 담은 응답 DTO와 HTTP 상태 코드 OK (200)
     */
    @PostMapping("/verification")
    public ResponseEntity<EmailVerificationResponseDto> verifyCertificationNumber(@Valid @RequestBody EmailVerificationRequestDto emailVerificationRequestDto) {
        log.info("emailResponseDto = {}", emailVerificationRequestDto);
        boolean verifyEmailCode = emailService.verifyEmail(emailVerificationRequestDto.getEmail(), emailVerificationRequestDto.getCertificationCode());
        return new ResponseEntity<>(new EmailVerificationResponseDto(verifyEmailCode), HttpStatus.OK);
    }
}
