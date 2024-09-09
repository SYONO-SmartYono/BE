package paengbeom.syono.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import paengbeom.syono.dto.EmailCertificationRequestDto;
import paengbeom.syono.dto.EmailCertificationResponseDto;
import paengbeom.syono.service.EmailService;

@Slf4j
@RequestMapping("/api/email")
@Controller
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @GetMapping("/request")
    public String showRequestForm() {
        return "request";
    }

    @GetMapping("/verify")
    public String showVerifyForm() {
        return "verify";
    }

    @PostMapping("/send-certification")
    public ResponseEntity<?> sendJoinEmail(@ModelAttribute EmailCertificationRequestDto emailRequestDto) {
        log.info("emailRequestDto = {}", emailRequestDto);
        String certificationNumber = emailService.sendEmailForCertification(emailRequestDto.getEmail());
        return ResponseEntity.ok().body(certificationNumber);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyCertificationNumber(@ModelAttribute EmailCertificationResponseDto emailResponseDto) {
        log.info("emailResponseDto = {}", emailResponseDto);
        boolean available = emailService.verifyEmail(emailResponseDto.getEmail(), emailResponseDto.getCertificationNumber());
        return ResponseEntity.ok().body(available);
    }
}
