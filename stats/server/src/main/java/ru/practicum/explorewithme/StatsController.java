package ru.practicum.explorewithme;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        statsService.saveHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatsDto>> getStats(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Dates are incorrect: start should be before end");
        }
        return new ResponseEntity<>(statsService.getStats(start, end, uris, unique), HttpStatus.OK);
    }

    @GetMapping("/view")
    public ResponseEntity<EventView> getEventView(@RequestParam String uri,
                                                  @RequestParam String ip) {
        return new ResponseEntity<>(statsService.getEventView(uri, ip), HttpStatus.OK);
    }
}
