package ru.practicum.explorewithme;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class StatsClient extends BaseClient {
    public StatsClient(RestTemplate rest) {
        super(rest);
    }

    public ResponseEntity<Object> saveHit(EndpointHitDto endpointHitDto) {
        return post("/hit", endpointHitDto);
    }

    public ResponseEntity<Object> getStats(String start, String end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );
        return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }

    public ResponseEntity<Object> getEventView(String uri, String ip) {
        Map<String, Object> parameters = Map.of(
                "uri", uri,
                "ip", ip
        );
        return get("/view?uri={uri}&ip={ip}", parameters);
    }
}
