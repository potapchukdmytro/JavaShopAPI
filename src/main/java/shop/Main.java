package shop;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import shop.interfaces.SeedService;
import shop.storage.StorageProperties;
import shop.storage.StorageService;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
@SecurityScheme(name = "vovan-api", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
@OpenAPIDefinition(info = @Info(title = "User API", version = "2.0", description = "User Details"))
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService, SeedService seedService) {
        return args -> {
            try {
                storageService.init();
                seedService.seedRoleData();
                seedService.seedUserData();
            } catch (Exception ex) {
                System.out.println("Error" + ex.getMessage());
            }
        };
    }
}
