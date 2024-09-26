package paengbeom.syono.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import paengbeom.syono.dto.SmSCertificationRequestDto;
import paengbeom.syono.dto.SmsCertificationResponseDto;
import paengbeom.syono.dto.user.CodefAccountRequestDto;
import paengbeom.syono.exception.CustomException;
import paengbeom.syono.exception.ExceptionResponseCode;
import paengbeom.syono.service.UserService;
import paengbeom.syono.util.CodefUtil;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("/user")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final CodefUtil codefUtil;
    private final UserService userService;

    @Value("${codef.sandbox.connected-id}")
    private String CONNECTED_ID;

    @GetMapping("/test")
    public ResponseEntity<?> sendSmsForJoin() {
        throw new CustomException(ExceptionResponseCode.NOT_EXISTED_EMAIL.getCode(), ExceptionResponseCode.NOT_EXISTED_EMAIL.getMessage());
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


    /**
     * 새로운 계정을 등록하는 메서드.
     *
     * @param codefAccountRequestDto 계정 등록에 필요한 요청 데이터를 담은 DTO
     * @return 계정 등록 결과를 담은 문자열을 Mono로 반환
     */
    @PostMapping("/accounts/create")
    public Mono<String> createAccount(@Valid @RequestBody CodefAccountRequestDto codefAccountRequestDto) {
        return codefUtil.createConnectedId(codefAccountRequestDto);
    }

    /**
     * 계정을 추가하는 메서드. 이미 존재하는 연결된 계정(CONNECTED_ID)에 새로운 계정을 추가합니다.
     *
     * @param codefAccountRequestDto 계정 추가에 필요한 요청 데이터를 담은 DTO
     * @return 계정 추가 결과를 담은 문자열을 Mono로 반환
     */
    @PostMapping("/accounts")
    public Mono<String> addAccount(@RequestBody CodefAccountRequestDto codefAccountRequestDto) {
        return codefUtil.addAccount(codefAccountRequestDto, CONNECTED_ID);
    }

    /**
     * 계정을 수정하는 메서드. 기존에 연결된 계정(CONNECTED_ID)의 정보를 업데이트합니다.
     *
     * @param codefAccountRequestDto 계정 수정에 필요한 요청 데이터를 담은 DTO
     * @return 계정 수정 결과를 담은 문자열을 Mono로 반환
     */
    @PutMapping("/accounts")
    public Mono<String> updateAccount(@RequestBody CodefAccountRequestDto codefAccountRequestDto) {
        return codefUtil.updateAccount(codefAccountRequestDto, CONNECTED_ID);
    }

    /**
     * 계정을 삭제하는 메서드. 기존에 연결된 계정(CONNECTED_ID)을 삭제합니다.
     *
     * @param codefAccountRequestDto 계정 삭제에 필요한 요청 데이터를 담은 DTO
     * @return 계정 삭제 결과를 담은 문자열을 Mono로 반환
     */
    @DeleteMapping("/accounts")
    public Mono<String> deleteAccount(@RequestBody CodefAccountRequestDto codefAccountRequestDto) {
        return codefUtil.deleteAccount(codefAccountRequestDto, CONNECTED_ID);
    }

    /**
     * 등록된 계정 목록을 조회하는 메서드.
     *
     * @return 계정 목록을 담은 리스트를 Mono로 반환
     */
    @GetMapping("/accounts")
    public Mono<List<Map<String, String>>> getAccountList() {
        return codefUtil.getAccountList(CONNECTED_ID);
    }

}
