package paengbeom.syono.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import paengbeom.syono.dto.user.SignInResponseDto;
import paengbeom.syono.entity.User;
import paengbeom.syono.exception.CustomException;
import paengbeom.syono.repository.UserRepository;

import java.io.IOException;

import static paengbeom.syono.exception.ExceptionResponseCode.NOT_EXISTED_EMAIL;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(NOT_EXISTED_EMAIL.getCode(), NOT_EXISTED_EMAIL.getMessage()));

        log.info("role : {}", user.getRole());
        log.info("roleDetail : {}", user.getRole().getDescription());
        SignInResponseDto signInResponseDto = SignInResponseDto.builder()
                .email(user.getEmail())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .role(user.getRole().getDescription())
                .profileImg(user.getProfileImg())
                .build();

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(signInResponseDto));
    }
}
