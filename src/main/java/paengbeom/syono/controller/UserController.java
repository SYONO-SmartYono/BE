package paengbeom.syono.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class UserController {
    @GetMapping("/login")
    public String login() {
        return "login"; // 로그인 페이지의 템플릿 이름
    }

    @GetMapping("/userInfo")
    public String userInfo(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        log.info("user={}", userDetails);
        // 사용자 정보를 모델에 추가
        model.addAttribute("email", userDetails.getUsername());
         model.addAttribute("authorities", userDetails.getAuthorities());

        return "userInfo"; // 사용자 정보 페이지의 템플릿 이름
    }
}
