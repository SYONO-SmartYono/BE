package paengbeom.syono.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import paengbeom.syono.dto.CodefAccountRequestDto;
import paengbeom.syono.util.CodefUtil;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("/home")
@RequiredArgsConstructor
@RestController
public class HomeController {
    private final CodefUtil codefUtil;

    @Value("${codef.sandbox.connected-id}")
    private String CONNECTED_ID;

    @GetMapping("/accessToken")
    public String userInfo() {
        String accessToken = codefUtil.publishToken();
        log.info("access token={}", accessToken);
        return accessToken; // 사용자 정보 페이지의 템플릿 이름
    }

    @PostMapping("/create-account")
    public Mono<String> createAccount(@RequestBody CodefAccountRequestDto codefAccountRequestDto) {
        return codefUtil.createConnectedId(codefAccountRequestDto);
    }

    @PostMapping("/add-account")
    public Mono<String> addAccount(@RequestBody CodefAccountRequestDto codefAccountRequestDto) {
        return codefUtil.addAccount(codefAccountRequestDto, CONNECTED_ID);
    }

    @PostMapping("/update-account")
    public Mono<String> updateAccount(@RequestBody CodefAccountRequestDto codefAccountRequestDto) {
        return codefUtil.addAccount(codefAccountRequestDto, CONNECTED_ID);
    }

    @PostMapping("/delete-account")
    public Mono<String> deleteAccount(@RequestBody CodefAccountRequestDto codefAccountRequestDto) {
        return codefUtil.addAccount(codefAccountRequestDto, CONNECTED_ID);
    }

    @GetMapping("/get-account-list")
    public Mono<List<Map<String, String>>> getAccountList() {
        return codefUtil.getAccountList(CONNECTED_ID);
    }
}
