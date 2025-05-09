package id.ac.ui.cs.gatherlove.auth;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
        "APP_JWT_SECRET=testsecretkeyfortests",
        "APP_JWT_ISSUER=test-issuer",
        "APP_JWT_ALGORITHM=HS256",
        "APP_JWT_EXPIRES_IN=PT15M",
        "DB_HOST=localhost",
        "DB_PORT=5432",
        "DB_NAME=testdb",
        "DB_USERNAME=testuser",
        "DB_PASSWORD=testpass"
})
@ActiveProfiles("test")
@SpringBootTest
class AuthApplicationTests {

    @Test
    void contextLoads() {
    }

}
