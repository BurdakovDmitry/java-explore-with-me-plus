package client;

import ewm.HitDto;
import ewm.ParamDto;
import ewm.StatsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
public class StatClient {
    final RestClient restClient;

    public StatClient() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:9090")
                .build();
    }

    public void hit(HitDto hitDto) {
        try {
            ResponseEntity<Void> response = restClient.post()
                    .uri("/hit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(hitDto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.info("Failed to connect to stat service");
        }
    }

    public List<StatsDto> get(ParamDto paramDto) {
        List<StatsDto> stats;
        try {
            stats = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/stats")
                            .queryParam("start ", paramDto.start())
                            .queryParam("end", paramDto.end())
                            .queryParam("uris", paramDto.uris())
                            .queryParam("unique", paramDto.unique())
                            .build())
                    .header("Content-Type", "application/json")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (Exception e) {
            log.info("Failed to connect to stat service");
            stats = List.of(new StatsDto("ewm-main-service", "/fake-uri", 0L));
        }
        return stats;

    }
}
