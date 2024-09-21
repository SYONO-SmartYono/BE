package paengbeom.syono.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import paengbeom.syono.util.CodefUtil;

@Configuration
public class WebClientConfig {

    private final CodefUtil codefUtil;

    public WebClientConfig(CodefUtil codefUtil) {
        this.codefUtil = codefUtil;
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {

        String accessToken = codefUtil.publishToken();

        return builder
                .baseUrl("https://sandbox.codef.io/v1")
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    httpHeaders.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
                    httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
                })
                .build();
    }
}
