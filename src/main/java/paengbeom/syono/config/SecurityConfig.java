package paengbeom.syono.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import paengbeom.syono.util.LoginSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final LoginSuccessHandler loginSuccessHandler;

    public SecurityConfig(LoginSuccessHandler loginSuccessHandler) {
        this.loginSuccessHandler = loginSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement((session) -> session
//                        .sessionFixation().changeSessionId()
                                .maximumSessions(1)
                                .maxSessionsPreventsLogin(false)
                )

                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/test").authenticated()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )

                .formLogin(form -> form
                        .loginProcessingUrl("/user/signin")
                        .usernameParameter("email")
                        .successHandler(loginSuccessHandler))

                .logout(logout -> logout
                        .logoutUrl("/user/logout")
//                        .logoutSuccessUrl("/login?logout").
                        .deleteCookies("JSESSIONID"));


        return http.build();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
