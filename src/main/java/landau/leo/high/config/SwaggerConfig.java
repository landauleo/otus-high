package landau.leo.high.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("App API")
                        .description("Otus highload architect homework")
                        .contact(new Contact().url("https://otus.ru/lessons/highloadarchitect/")))
                .externalDocs(new ExternalDocumentation()
                        .description("Own github")
                        .url("https://github.com/landauleo/otus-high"));
    }

}
