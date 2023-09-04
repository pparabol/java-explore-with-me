package ru.practicum.explorewithme;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.exception.ValidationException;
import ru.practicum.explorewithme.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        log.info("Creating hit: endpointHitDto={}", endpointHitDto);
        statsService.saveHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatsDto>> getStats(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique) {
        if (start.isAfter(end)) {
            throw new ValidationException("Dates are incorrect: start should be before end");
        }
        log.info("Get stats: start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return new ResponseEntity<>(statsService.getStats(start, end, uris, unique), HttpStatus.OK);
    }

    @GetMapping("/view")
    public ResponseEntity<EventView> getEventView(@RequestParam String uri,
                                                  @RequestParam String ip) {
        return new ResponseEntity<>(statsService.getEventView(uri, ip), HttpStatus.OK);
    }
}
