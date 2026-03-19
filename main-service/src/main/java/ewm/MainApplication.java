package ewm;

import client.StatClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
@ComponentScan(value = {"ewm", "client"})
public class MainApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MainApplication.class, args);
        StatClient statClient = context.getBean(StatClient.class);
        statClient.hit(new HitDto());
        List<StatsDto> stat = statClient.get(new ParamDto(LocalDateTime.now(), LocalDateTime.now().plusDays(1), List.of(""), true));
        System.out.println(stat);
    }
}