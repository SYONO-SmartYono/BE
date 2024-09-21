package paengbeom.syono.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import paengbeom.syono.dto.CodefAccountListDto;
import paengbeom.syono.util.CodefUtil;
import reactor.core.publisher.Mono;

@Slf4j
@RequestMapping("/home")
@RequiredArgsConstructor
@RestController
public class HomeController {
    private final CodefUtil codefUtil;

    @GetMapping("/accessToken")
    public String userInfo() {
        String accessToken = codefUtil.publishToken();
        log.info("access token={}", accessToken);
        return accessToken; // 사용자 정보 페이지의 템플릿 이름
    }

    @PostMapping("/create-account")
    public Mono<String> createAccount(@RequestBody CodefAccountListDto codefAccountListDto) {
        return codefUtil.CreateConnectedId(codefAccountListDto);
    }
}
