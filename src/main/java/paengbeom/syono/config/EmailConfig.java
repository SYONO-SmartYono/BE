package paengbeom.syono.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    @Value("${spring.mail.protocol}")
    private String PROTOCOL;
    @Value("${spring.mail.host}")
    private String HOST;
    @Value("${spring.mail.port}")
    private int PORT;
    @Value("${spring.mail.username}")
    private String USERNAME;
    @Value("${spring.mail.password}")
    private String PASSWORD;
    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String AUTH;
    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String STARTTLS_ENABLE;
    @Value("${spring.mail.properties.mail.smtp.timeout}")
    private int TIMEOUT;
    @Value("${spring.mail.properties.mail.smtp.debug}")
    private boolean DEBUG;

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setProtocol(PROTOCOL);
        mailSender.setHost(HOST);
        mailSender.setPort(PORT);
        mailSender.setUsername(USERNAME);
        mailSender.setPassword(PASSWORD);
        mailSender.setDefaultEncoding("UTF-8");
        mailSender.setJavaMailProperties(getMailProperties());

        return mailSender;
    }

    private Properties getMailProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", AUTH);
        properties.put("mail.smtp.starttls.enable", STARTTLS_ENABLE);
        properties.put("mail.smtp.timeout", TIMEOUT);
        properties.put("mail.smtp.debug", DEBUG);
        
        return properties;
    }
}
