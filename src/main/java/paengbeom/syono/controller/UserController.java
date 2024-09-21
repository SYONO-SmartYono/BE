package paengbeom.syono.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import paengbeom.syono.dto.SmSCertificationRequestDto;
import paengbeom.syono.dto.SmsCertificationResponseDto;
import paengbeom.syono.service.UserService;

@Slf4j
@RequestMapping("/user")
@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/userInfo")
    public String userInfo(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        log.info("user={}", userDetails);
        // 사용자 정보를 모델에 추가
        model.addAttribute("email", userDetails.getUsername());
        model.addAttribute("authorities", userDetails.getAuthorities());

        return "userInfo"; // 사용자 정보 페이지의 템플릿 이름
    }

    @PostMapping("/send-certificationSms")
    public ResponseEntity<?> sendSmsForJoin(@ModelAttribute SmSCertificationRequestDto smsRequestDto) {
        log.info("smsRequestDto = {}", smsRequestDto);
        String certificationNumber = userService.sendSmsForCertification(smsRequestDto.getPhoneNumber());
        return ResponseEntity.ok().body(certificationNumber);
    }

    @PostMapping("/verifySms")
    public ResponseEntity<?> verifyCertificationNumber(@ModelAttribute SmsCertificationResponseDto smsResponseDto) {
        log.info("smsResponseDto = {}", smsResponseDto);
        boolean available = userService.verifySms(smsResponseDto.getPhoneNumber(), smsResponseDto.getCertificationNumber());
        return ResponseEntity.ok().body(available);
    }


}
