package ru.practicum.explorewithme.publicapi.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.EndpointHitDto;
import ru.practicum.explorewithme.StatsClient;
import ru.practicum.explorewithme.common.dto.event.EventFullDto;
import ru.practicum.explorewithme.common.dto.event.EventShortDto;
import ru.practicum.explorewithme.common.dto.event.EventState;
import ru.practicum.explorewithme.common.exception.NotFoundException;
import ru.practicum.explorewithme.common.mapper.EventMapper;
import ru.practicum.explorewithme.common.model.Event;
import ru.practicum.explorewithme.common.repository.EventRepository;
import ru.practicum.explorewithme.publicapi.util.SearchEventsPublicParams;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicEventServiceImpl implements PublicEventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final StatsClient statsClient;

    @Value("${evm-app.name}")
    private String appName;

    @Override
    public List<EventShortDto> getEventsByCriteria(SearchEventsPublicParams params,
                                                   int from,
                                                   int size,
                                                   HttpServletRequest request) {
        Sort sort;
        switch (params.getSort()) {
            case VIEWS:
                sort = Sort.by(Sort.Direction.DESC, "views");
                break;
            case EVENT_DATE:
                sort = Sort.by(Sort.Direction.DESC, "eventDate");
                break;
            default:
                sort = Sort.by(Sort.Direction.ASC, "id");
        }
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);

        List<Event> events;

        if (params.getRangeStart() == null && params.getRangeEnd() == null) {
            events = eventRepository.findFutureEventsByCriteria(params.getText(), params.getCategories(),
                    params.getPaid(), params.isOnlyAvailable(), pageRequest);
        } else {
            events = eventRepository.findEventsByCriteria(params.getText(),
                    params.getCategories(), params.getPaid(), params.getRangeStart(),
                    params.getRangeEnd(), params.isOnlyAvailable(), pageRequest);
        }

        saveHit(request);
        return events.stream()
                .map(eventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto get(long eventId, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() ->
                        new NotFoundException((String.format("Event with id=%d was not found", eventId))));

        ResponseEntity<Object> view =
                statsClient.getEventView(request.getRequestURI(), request.getRemoteAddr());

        if (view.getBody() == null) {
            event.setViews(event.getViews() + 1);
        }

        saveHit(request);
        return eventMapper.toFullDto(eventRepository.save(event));
    }

    private void saveHit(HttpServletRequest request) {
        EndpointHitDto hitDto = EndpointHitDto.builder()
                .app(appName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
        statsClient.saveHit(hitDto);
    }
}
