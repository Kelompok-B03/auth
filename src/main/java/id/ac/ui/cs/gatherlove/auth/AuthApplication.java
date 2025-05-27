package id.ac.ui.cs.gatherlove.auth;

import id.ac.ui.cs.gatherlove.auth.config.AppJwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.sql.Connection;
import java.sql.SQLException;

@SpringBootApplication
@EntityScan(basePackages = "id.ac.ui.cs.gatherlove.auth.model")
@EnableConfigurationProperties(AppJwtProperties.class)
public class AuthApplication {
    @Autowired
    private DataSource dataSource;

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        System.setProperty("DB_HOST", dotenv.get("DB_HOST"));
        System.setProperty("DB_PORT", dotenv.get("DB_PORT"));
        System.setProperty("DB_NAME", dotenv.get("DB_NAME"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("APP_JWT_SECRET", dotenv.get("APP_JWT_SECRET"));
        System.setProperty("APP_JWT_ISSUER", dotenv.get("APP_JWT_ISSUER"));
        System.setProperty("APP_JWT_EXPIRES_IN", dotenv.get("APP_JWT_EXPIRES_IN"));
        System.setProperty("APP_JWT_ALGORITHM", dotenv.get("APP_JWT_ALGORITHM"));
        System.setProperty("WALLET_SERVICE_BASE_URL", dotenv.get("WALLET_SERVICE_BASE_URL"));

        SpringApplication.run(AuthApplication.class, args);
    }

    @PostConstruct
    public void testConnection() {
        try (Connection connection = dataSource.getConnection()) {
            System.out.println("SUCCESS! Connected to database: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("URL: " + connection.getMetaData().getURL());
            System.out.println("Username: " + connection.getMetaData().getUserName());
        } catch (SQLException e) {
            System.err.println("ERROR: Could not connect to database");
            e.printStackTrace();
        }
    }
}