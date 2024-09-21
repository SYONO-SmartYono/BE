package paengbeom.syono.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.contrib.org.apache.commons.codec_1_3.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import paengbeom.syono.dto.CodefAccountListDto;
import paengbeom.syono.dto.CodefCreateAccountDto;
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

import static paengbeom.syono.util.CodefApiConstant.*;

@Slf4j
@Component
public class CodefUtil {

    @Value("${codef.sandbox.public-key}")
    private String PUBLIC_KEY;

    @Value("${codef.sandbox.client-id}")
    private String CLIENT_ID;

    @Value("${codef.sandbox.client-secret}")
    private String CLIENT_SECRET;

    private ObjectMapper mapper = new ObjectMapper();

    private final WebClient webClient;

    public CodefUtil(@Lazy WebClient webClient) {
        this.webClient = webClient;
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

    public Mono<String> CreateConnectedId(CodefAccountListDto accountInfo) {
        log.info("accoutInfo = {}", accountInfo);
        Map<String, List<Map<String, String>>> bodyMap = new HashMap<>();
        List<Map<String, String>> accountList = new ArrayList<>();

        HashMap<String, String> accountMap = new HashMap<>();
        accountMap.put("countryCode", "KR");
        accountMap.put("businessType", accountInfo.getBusinessType());
        accountMap.put("clientType", "P");
        accountMap.put("organization", accountInfo.getCode());
        accountMap.put("loginType", "1");
        accountMap.put("id", accountInfo.getId());
        try {
            accountMap.put("password", encryptRSA(accountInfo.getPassword(), PUBLIC_KEY));
        } catch (Exception e) {
            throw new RuntimeException("비밀번호 암호화에 실패했습니다.", e);
        }

        accountList.add(accountMap);
        bodyMap.put("accountList", accountList);

        return webClient.post()
                .uri(CREATE_ACCOUNT)
                .body(Mono.just(bodyMap), Map.class)
                .retrieve()
                .bodyToMono(CodefCreateAccountDto.class)
                .map(CodefCreateAccountDto::getConnectedId);
    }

    private String encryptRSA(String planText, String base64PublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

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
