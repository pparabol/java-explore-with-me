package ru.practicum.explorewithme.publicapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.common.dto.category.CategoryDto;
import ru.practicum.explorewithme.common.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.common.dto.event.EventFullDto;
import ru.practicum.explorewithme.common.dto.event.EventShortDto;
import ru.practicum.explorewithme.publicapi.service.category.PublicCategoryService;
import ru.practicum.explorewithme.publicapi.service.compilation.PublicCompilationService;
import ru.practicum.explorewithme.publicapi.service.event.PublicEventService;
import ru.practicum.explorewithme.publicapi.util.SearchEventsPublicParams;
import ru.practicum.explorewithme.publicapi.util.Sort;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicController {
    private final PublicCategoryService categoryService;
    private final PublicCompilationService compilationService;
    private final PublicEventService eventService;

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getCategories(
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Get categories: from={}, size={}", from, size);
        return new ResponseEntity<>(categoryService.getAll(from, size), HttpStatus.OK);
    }

    @GetMapping("/categories/{catId}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable int catId) {
        log.info("Get category: catId={}", catId);
        return new ResponseEntity<>(categoryService.get(catId), HttpStatus.OK);
    }

    @GetMapping("/compilations")
    public ResponseEntity<List<CompilationDto>> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Get compilations: pinned={}, from={}, size={}", pinned, from, size);
        return new ResponseEntity<>(compilationService.getAll(pinned, from, size), HttpStatus.OK);
    }

    @GetMapping("/compilations/{compId}")
    public ResponseEntity<CompilationDto> getCompilation(@PathVariable int compId) {
        log.info("Get compilation: compId={}", compId);
        return new ResponseEntity<>(compilationService.get(compId), HttpStatus.OK);
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventShortDto>> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) Set<Integer> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size,
            HttpServletRequest request
    ) {
        Sort eventSort = Sort.ID;
        if (sort != null) {
            eventSort = Sort.from(sort).orElseThrow(() ->
                    new IllegalArgumentException("Unknown sort: " + sort));
        }
        if (rangeEnd != null && rangeStart != null) {
            if (rangeEnd.isBefore(rangeStart)) {
                throw new IllegalArgumentException("The end of range must be after its start");
            }
        }

        SearchEventsPublicParams params = SearchEventsPublicParams.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .sort(eventSort)
                .build();

        log.info("Get events in public mode: params={}, from={}, size={}", params, from, size);
        return new ResponseEntity<>(eventService.getEventsByCriteria(params, from, size, request), HttpStatus.OK);
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<EventFullDto> getEvent(@PathVariable long id, HttpServletRequest request) {
        log.info("Get event: id={}", id);
        return new ResponseEntity<>(eventService.get(id, request), HttpStatus.OK);
    }

}
