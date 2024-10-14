package paengbeom.syono.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.contrib.org.apache.commons.codec_1_3.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import paengbeom.syono.dto.codef.CodefAccountListResponseDto;
import paengbeom.syono.dto.codef.CodefApiResponseDto;
import paengbeom.syono.dto.codef.CodefCreateAccountDto;
import paengbeom.syono.dto.codef.CodefgetCardListDto;
import paengbeom.syono.entity.Company;
import paengbeom.syono.entity.ConnectedCompany;
import paengbeom.syono.entity.User;
import paengbeom.syono.exception.CustomException;
import paengbeom.syono.repository.CompanyRepository;
import paengbeom.syono.repository.ConnectedCompanyRepository;
import paengbeom.syono.repository.UserRepository;
import reactor.core.publisher.Mono;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static paengbeom.syono.exception.ExceptionResponseCode.*;
import static paengbeom.syono.util.CodefApiConstant.*;

@Slf4j
@Component
public class CodefUtil {

    private final UserRepository userRepository;
    private final RedisUtil redisUtil;
    @Value("${codef.sandbox.public-key}")
    private String PUBLIC_KEY;

    @Value("${codef.sandbox.client-id}")
    private String CLIENT_ID;

    @Value("${codef.sandbox.client-secret}")
    private String CLIENT_SECRET;

    private ObjectMapper mapper = new ObjectMapper();
    private final WebClient webClient;
    private final CompanyRepository companyRepository;
    private final ConnectedCompanyRepository connectedCompanyRepository;

    @Autowired
    public CodefUtil(@Lazy WebClient webClient, CompanyRepository companyRepository, ConnectedCompanyRepository connectedCompanyRepository, UserRepository userRepository, RedisUtil redisUtil) {
        this.webClient = webClient;
        this.companyRepository = companyRepository;
        this.connectedCompanyRepository = connectedCompanyRepository;
        this.userRepository = userRepository;
        this.redisUtil = redisUtil;
    }

    public String publishToken() {

        BufferedReader br = null;
        try {
            // HTTP 요청을 위한 URL 오브젝트 생성
            URL url = new URL(TOKEN_DOMAIN + GET_TOKEN);
            String params = "grant_type=client_credentials&scope=read";

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // 클라이언트아이디, 시크릿코드 Base64 인코딩
            String auth = CLIENT_ID + ":" + CLIENT_SECRET;
            byte[] authEncBytes = Base64.encodeBase64(auth.getBytes());
            String authStringEnc = new String(authEncBytes);
            String authHeader = "Basic " + authStringEnc;
            log.info("authHeader : {}", authHeader);

            con.setRequestProperty("Authorization", authHeader);
            con.setDoInput(true);
            con.setDoOutput(true);

            // 리퀘스트 바디 전송
            OutputStream os = con.getOutputStream();
            os.write(params.getBytes());
            os.flush();
            os.close();

            // 응답 코드 확인
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {    // 정상 응답
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {     // 에러 발생
                return null;
            }

            // 응답 바디 read
            String inputLine;
            StringBuffer responseStr = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                responseStr.append(inputLine);
            }
            br.close();

            HashMap<String, Object> tokenMap = mapper.readValue(URLDecoder.decode(responseStr.toString(), "UTF-8"), new TypeReference<>() {
            });

            return tokenMap.get("access_token").toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 주어진 이메일과 회사 정보, ID, 비밀번호를 사용하여 연결된 계정을 생성합니다.
     *
     * @param email       사용자의 이메일
     * @param companyName 회사 이름
     * @param id          사용자 ID
     * @param password    사용자 비밀번호
     * @return 계정 생성 성공 여부를 나타내는 Mono<Boolean>
     */
    public Mono<Boolean> createConnectedId(String email, String companyName, String id, String password) {
        log.info("email: {}, companyName: {}, id: {}, password: {}", email, companyName, id, password);
        Company company = companyRepository.findByName(companyName)
                .orElseThrow(() -> new CustomException(NOT_EXISTED_COMPANY.getCode(), NOT_EXISTED_COMPANY.getMessage()));

        Map<String, List<Map<String, String>>> bodyMap = new HashMap<>();
        List<Map<String, String>> accountList = new ArrayList<>();

        HashMap<String, String> accountMap = new HashMap<>();
        accountMap.put("countryCode", "KR");
        accountMap.put("businessType", company.getType());
        accountMap.put("clientType", "P");
        accountMap.put("organization", company.getCode());
        accountMap.put("loginType", "1");
        accountMap.put("id", id);
        try {
            accountMap.put("password", encryptRSA(password, PUBLIC_KEY));
        } catch (Exception e) {
            throw new CustomException(ENCRYPTION_FAILURE.getCode(), ENCRYPTION_FAILURE.getMessage());
        }

        accountList.add(accountMap);
        bodyMap.put("accountList", accountList);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(NOT_EXISTED_EMAIL.getCode(), NOT_EXISTED_EMAIL.getMessage()));

        return webClient.post()
                .uri(CREATE_ACCOUNT)
                .bodyValue(bodyMap)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CodefApiResponseDto<CodefCreateAccountDto>>() {
                })
                .map(responseDto -> {
                    CodefCreateAccountDto data = responseDto.getData();
                    log.info("data = {}", data);

                    userRepository.save(
                            user.toBuilder()
                                    .connectedId(data.getConnectedId())
                                    .build());

                    connectedCompanyRepository.save(
                            ConnectedCompany.builder()
                                    .user(user)
                                    .company(company)
                                    .build());

                    return true;  // 성공 시 true 반환
                })
                .onErrorMap(e -> {
                    throw new CustomException(ACCOUNT_CREATION_FAILURE.getCode(), ACCOUNT_CREATION_FAILURE.getMessage());
                });
    }

//    public Mono<String> addAccount(CodefAccountRequestDto accountInfo, String connectedId) {
//        log.info("accoutInfo = {}", accountInfo);
//        Map<String, Object> bodyMap = new HashMap<>();
//        List<Map<String, String>> accountList = new ArrayList<>();
//
//        HashMap<String, String> accountMap = new HashMap<>();
//        accountMap.put("countryCode", "KR");
//        accountMap.put("businessType", accountInfo.getBusinessType());
//        accountMap.put("clientType", "P");
//        accountMap.put("organization", accountInfo.getOrganization());
//        accountMap.put("loginType", "1");
//        accountMap.put("id", accountInfo.getId());
//        try {
//            accountMap.put("password", encryptRSA(accountInfo.getPassword(), PUBLIC_KEY));
//        } catch (Exception e) {
//            throw new RuntimeException("비밀번호 암호화에 실패했습니다.", e);
//        }
//
//        accountList.add(accountMap);
//        bodyMap.put("accountList", accountList);
//        bodyMap.put("connectedId", connectedId);
//
//        return webClient.post()
//                .uri(ADD_ACCOUNT)
//                .body(Mono.just(bodyMap), Map.class)
//                .retrieve()
//                .bodyToMono(new ParameterizedTypeReference<CodefApiResponseDto<CodefCreateAccountDto>>() {
//                })
//                .map(responseDto -> {
//                    CodefCreateAccountDto data = responseDto.getData();
//                    log.info("data = {}", data);
//                    return data.getConnectedId();
//                });
//    }
//
//    public Mono<String> updateAccount(CodefAccountRequestDto accountInfo, String connectedId) {
//        log.info("accoutInfo = {}", accountInfo);
//        Map<String, Object> bodyMap = new HashMap<>();
//        List<Map<String, String>> accountList = new ArrayList<>();
//
//        HashMap<String, String> accountMap = new HashMap<>();
//        accountMap.put("countryCode", "KR");
//        accountMap.put("businessType", accountInfo.getBusinessType());
//        accountMap.put("clientType", "P");
//        accountMap.put("organization", accountInfo.getOrganization());
//        accountMap.put("loginType", "1");
//        accountMap.put("id", accountInfo.getId());
//        try {
//            accountMap.put("password", encryptRSA(accountInfo.getPassword(), PUBLIC_KEY));
//        } catch (Exception e) {
//            throw new RuntimeException("비밀번호 암호화에 실패했습니다.", e);
//        }
//
//        accountList.add(accountMap);
//        bodyMap.put("accountList", accountList);
//        bodyMap.put("connectedId", connectedId);
//
//        return webClient.post()
//                .uri(UPDATE_ACCOUNT)
//                .body(Mono.just(bodyMap), Map.class)
//                .retrieve()
//                .bodyToMono(new ParameterizedTypeReference<CodefApiResponseDto<CodefCreateAccountDto>>() {
//                })
//                .map(responseDto -> {
//                    CodefCreateAccountDto data = responseDto.getData();
//                    log.info("data = {}", data);
//                    return data.getConnectedId();
//                });
//    }
//
//    public Mono<String> deleteAccount(CodefAccountRequestDto accountInfo, String connectedId) {
//        log.info("accoutInfo = {}", accountInfo);
//        Map<String, Object> bodyMap = new HashMap<>();
//        List<Map<String, String>> accountList = new ArrayList<>();
//
//        HashMap<String, String> accountMap = new HashMap<>();
//        accountMap.put("countryCode", "KR");
//        accountMap.put("businessType", accountInfo.getBusinessType());
//        accountMap.put("clientType", "P");
//        accountMap.put("organization", accountInfo.getOrganization());
//        accountMap.put("loginType", "1");
//        accountMap.put("id", accountInfo.getId());
//        try {
//            accountMap.put("password", encryptRSA(accountInfo.getPassword(), PUBLIC_KEY));
//        } catch (Exception e) {
//            throw new RuntimeException("비밀번호 암호화에 실패했습니다.", e);
//        }
//
//        accountList.add(accountMap);
//        bodyMap.put("accountList", accountList);
//        bodyMap.put("connectedId", connectedId);
//
//        return webClient.post()
//                .uri(DELETE_ACCOUNT)
//                .body(Mono.just(bodyMap), Map.class)
//                .retrieve()
//                .bodyToMono(new ParameterizedTypeReference<CodefApiResponseDto<CodefCreateAccountDto>>() {
//                })
//                .map(responseDto -> {
//                    CodefCreateAccountDto data = responseDto.getData();
//                    log.info("data = {}", data);
//                    return data.getConnectedId();
//                });
//    }

    public Mono<List<Map<String, String>>> getAccountList(String connectedId) {
        log.info("connectedId = {}", connectedId);

        return webClient.post()
                .uri(GET_ACCOUNTS)
                .body(Mono.just(connectedId), String.class)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CodefApiResponseDto<CodefAccountListResponseDto>>() {
                })
                .map(responseDto -> {
                    CodefAccountListResponseDto data = responseDto.getData();
                    log.info("data = {}", data);
                    return data.getAccountList();
                });
    }

    public List<CodefgetCardListDto> getCardList(String email, String companyName) {

        List<CodefgetCardListDto> cardList = redisUtil.getCardList(email);
        if (cardList.get(0) != null) {
            log.info("캐시에서 카드리스트 반환");
            return cardList;
        }

        log.info("email: {}, companyName: {}", email, companyName);
        Company company = companyRepository.findByName(companyName)
                .orElseThrow(() -> new CustomException(NOT_EXISTED_COMPANY.getCode(), NOT_EXISTED_COMPANY.getMessage()));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(NOT_EXISTED_EMAIL.getCode(), NOT_EXISTED_EMAIL.getMessage()));

        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("organization", company.getCode());
        bodyMap.put("connectedId", user.getConnectedId());
        bodyMap.put("inquiryType", "1");

        return webClient.post()
                .uri(KR_CD_P_001)
                .bodyValue(bodyMap)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CodefApiResponseDto<List<CodefgetCardListDto>>>() {
                })
                .map(responseDto -> {
                    log.info("CodefApiResponseDto = {}", responseDto);
                    redisUtil.saveCardList(email, responseDto.getData());
                    return responseDto.getData();
                })
                .block();
    }

    private String encryptRSA(String planText, String base64PublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        log.info("planText = {}", planText);
        byte[] bytePublicKey = java.util.Base64.getDecoder().decode(base64PublicKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(bytePublicKey));

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] bytePlain = cipher.doFinal(planText.getBytes());
        String encrypted = java.util.Base64.getEncoder().encodeToString(bytePlain);

        return encrypted;
    }
}
