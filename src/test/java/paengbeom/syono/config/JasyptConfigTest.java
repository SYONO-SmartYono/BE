package paengbeom.syono.config;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class JasyptConfigTest {

    private final StringEncryptor encryptor;

    @Autowired
    public JasyptConfigTest(@Qualifier("jasyptStringEncryptor") StringEncryptor encryptor) {
        this.encryptor = encryptor;
    }

    @Test
    @DisplayName("testEncryptAndDecrypt")
    void jasyptTest() {

        String password = "password";
        String encryptedPassword = encryptor.encrypt(password);
        String decryptedPassword = encryptor.decrypt(encryptedPassword);

        log.info("encryptedPassword={}", encryptedPassword);
        log.info("decryptedPassword={}", decryptedPassword);

        Assertions.assertThat(decryptedPassword).isEqualTo(password);
    }
}