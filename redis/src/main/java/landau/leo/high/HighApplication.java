package landau.leo.high;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class HighApplication {

    public static void main(String[] args) {
        SpringApplication.run(HighApplication.class, args);
    }

}
