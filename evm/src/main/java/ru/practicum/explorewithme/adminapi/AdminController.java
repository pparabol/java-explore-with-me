package ru.practicum.explorewithme.adminapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.adminapi.service.category.AdminCategoryService;
import ru.practicum.explorewithme.adminapi.service.compilation.AdminCompilationService;
import ru.practicum.explorewithme.adminapi.service.event.AdminEventService;
import ru.practicum.explorewithme.adminapi.service.user.AdminUserService;
import ru.practicum.explorewithme.common.dto.category.CategoryDto;
import ru.practicum.explorewithme.common.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.common.dto.compilation.NewCompilationDto;
import ru.practicum.explorewithme.common.dto.compilation.UpdateCompilationRequest;
import ru.practicum.explorewithme.common.dto.event.EventFullDto;
import ru.practicum.explorewithme.common.dto.event.EventState;
import ru.practicum.explorewithme.common.dto.event.UpdateEventRequest;
import ru.practicum.explorewithme.common.dto.user.UserDto;
import ru.practicum.explorewithme.adminapi.util.SearchEventsAdminParams;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminController {
    private final AdminCategoryService categoryService;
    private final AdminUserService userService;
    private final AdminCompilationService compilationService;
    private final AdminEventService eventService;

    @PostMapping("categories")
    public ResponseEntity<CategoryDto> saveNewCategory(@Valid @RequestBody CategoryDto categoryDto) {
        log.info("Creating category: name={}", categoryDto.getName());
        return new ResponseEntity<>(categoryService.save(categoryDto), HttpStatus.CREATED);
    }

    @DeleteMapping("categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable int catId) {
        log.info("Deleting category: catId={}", catId);
        categoryService.delete(catId);
    }

    @PatchMapping("categories/{catId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable int catId,
                                                      @Valid @RequestBody CategoryDto categoryDto) {
        categoryDto.setId(catId);
        log.info("Updating category: categoryDto={}", categoryDto);
        return new ResponseEntity<>(categoryService.update(categoryDto), HttpStatus.OK);
    }

    @PostMapping("users")
    public ResponseEntity<UserDto> saveNewUser(@Valid @RequestBody UserDto userDto) {
        log.info("Creating user: userDto={}", userDto);
        return new ResponseEntity<>(userService.save(userDto), HttpStatus.CREATED);
    }

    @DeleteMapping("users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable long userId) {
        log.info("Deleting user: userId={}", userId);
        userService.delete(userId);
    }

    @GetMapping("users")
    public ResponseEntity<List<UserDto>> getUsers(@RequestParam(required = false) List<Long> ids,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Get users: ids={}, from={}, size={}", ids, from, size);
        return new ResponseEntity<>(userService.getUsers(ids, from, size), HttpStatus.OK);
    }

    @PostMapping("compilations")
    public ResponseEntity<CompilationDto> saveNewCompilation(@Valid @RequestBody NewCompilationDto compilationDto) {
        log.info("Creating compilation: compilationDto={}", compilationDto);
        return new ResponseEntity<>(compilationService.save(compilationDto), HttpStatus.CREATED);
    }

    @DeleteMapping("compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable int compId) {
        log.info("Deleting compilation: compId={}", compId);
        compilationService.delete(compId);
    }

    @PatchMapping("compilations/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(
            @PathVariable int compId,
            @Valid @RequestBody UpdateCompilationRequest compilationDto) {
        log.info("Updating compilation: compId={}, compilationDto={}", compId, compilationDto);
        return new ResponseEntity<>(compilationService.update(compId, compilationDto), HttpStatus.OK);
    }

    @GetMapping("events")
    public ResponseEntity<List<EventFullDto>> getEvents(
            @RequestParam(required = false) Set<Long> users,
            @RequestParam(required = false) Set<String> states,
            @RequestParam(required = false) Set<Integer> categories,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        Set<EventState> eventStates = null;
        if (states != null) {
            eventStates = states.stream()
                    .map(s -> EventState.from(s).orElseThrow(() ->
                            new IllegalArgumentException("Unknown state: " + s)))
                    .collect(Collectors.toSet());
        }

        SearchEventsAdminParams params = SearchEventsAdminParams.builder()
                .users(users)
                .states(eventStates)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .build();

        log.info("Get events by admin: params={}, from={}, size={}", params, from, size);
        return new ResponseEntity<>(eventService.getByCriteria(params, from, size), HttpStatus.OK);
    }

    @PatchMapping("events/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@PathVariable long eventId,
                                                    @Valid @RequestBody UpdateEventRequest eventDto) {
        log.info("Updating event: eventId={}, eventDto={}", eventId, eventDto);
        return new ResponseEntity<>(eventService.update(eventId, eventDto), HttpStatus.OK);
    }
}
