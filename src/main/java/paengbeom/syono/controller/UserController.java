package paengbeom.syono.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import paengbeom.syono.dto.codef.CreateAccountResponseDto;
import paengbeom.syono.dto.user.CodefAccountRequestDto;
import paengbeom.syono.dto.user.SignUpRequestDto;
import paengbeom.syono.dto.user.SignUpResponseDto;
import paengbeom.syono.service.UserService;
import paengbeom.syono.util.CodefUtil;
import reactor.core.publisher.Mono;

@Slf4j
@RequestMapping("/user")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final CodefUtil codefUtil;
    private final UserService userService;

    @Value("${codef.sandbox.connected-id}")
    private String CONNECTED_ID;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
        log.info("signup: {}", signUpRequestDto);
        SignUpResponseDto signUpResponseDto = userService.signUp(signUpRequestDto.getEmail(), signUpRequestDto.getPassword(), signUpRequestDto.getPhone());
        return new ResponseEntity<>(signUpResponseDto, HttpStatus.OK);
    }

    /**
     * 새로운 계정을 등록하는 메서드.
     *
     * @param codefAccountRequestDto 계정 등록에 필요한 요청 데이터를 담은 DTO
     * @return 계정 등록 결과를 담은 CreateAccountResponseDto를 Mono로 반환
     */
    @PostMapping("/account/create")
    public Mono<ResponseEntity<CreateAccountResponseDto>> createAccount(@AuthenticationPrincipal UserDetails securityUserDto, @Valid @RequestBody CodefAccountRequestDto codefAccountRequestDto) {
        Mono<Boolean> success = codefUtil.createConnectedId(securityUserDto.getUsername(), codefAccountRequestDto.getName(), codefAccountRequestDto.getId(), codefAccountRequestDto.getPassword());
        return success.map(isSuccess -> new ResponseEntity<>(new CreateAccountResponseDto(isSuccess), HttpStatus.OK));
    }


//    /**
//     * 계정을 추가하는 메서드. 이미 존재하는 연결된 계정(CONNECTED_ID)에 새로운 계정을 추가합니다.
//     *
//     * @param codefAccountRequestDto 계정 추가에 필요한 요청 데이터를 담은 DTO
//     * @return 계정 추가 결과를 담은 문자열을 Mono로 반환
//     */
//    @PostMapping("/accounts")
//    public Mono<String> addAccount(@RequestBody CodefAccountRequestDto codefAccountRequestDto) {
//        return codefUtil.addAccount(codefAccountRequestDto, CONNECTED_ID);
//    }
//
//    /**
//     * 계정을 수정하는 메서드. 기존에 연결된 계정(CONNECTED_ID)의 정보를 업데이트합니다.
//     *
//     * @param codefAccountRequestDto 계정 수정에 필요한 요청 데이터를 담은 DTO
//     * @return 계정 수정 결과를 담은 문자열을 Mono로 반환
//     */
//    @PutMapping("/accounts")
//    public Mono<String> updateAccount(@RequestBody CodefAccountRequestDto codefAccountRequestDto) {
//        return codefUtil.updateAccount(codefAccountRequestDto, CONNECTED_ID);
//    }
//
//    /**
//     * 계정을 삭제하는 메서드. 기존에 연결된 계정(CONNECTED_ID)을 삭제합니다.
//     *
//     * @param codefAccountRequestDto 계정 삭제에 필요한 요청 데이터를 담은 DTO
//     * @return 계정 삭제 결과를 담은 문자열을 Mono로 반환
//     */
//    @DeleteMapping("/accounts")
//    public Mono<String> deleteAccount(@RequestBody CodefAccountRequestDto codefAccountRequestDto) {
//        return codefUtil.deleteAccount(codefAccountRequestDto, CONNECTED_ID);
//    }
//
//    /**
//     * 등록된 계정 목록을 조회하는 메서드.
//     *
//     * @return 계정 목록을 담은 리스트를 Mono로 반환
//     */
//    @GetMapping("/accounts")
//    public Mono<List<Map<String, String>>> getAccountList() {
//        return codefUtil.getAccountList(CONNECTED_ID);
//    }

}
