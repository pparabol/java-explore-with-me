package ru.practicum.explorewithme;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.explorewithme.config.JsonDateTimeConfig;
import ru.practicum.explorewithme.service.StatsService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(StatsController.class)
@Import(JsonDateTimeConfig.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StatsControllerTest {
    private final ObjectMapper mapper;

    @MockBean
    private final StatsService statsService;

    private final MockMvc mvc;

    private final EndpointHitDto hitDto = EndpointHitDto.builder()
            .app("evm-service")
            .uri("/endpoint/1")
            .ip("123.123.1")
            .timestamp(LocalDateTime.now())
            .build();

    private final ViewStatsDto viewStatsDto = ViewStatsDto.builder()
            .app("evm-service")
            .uri("/endpoint/1")
            .hits(1L)
            .build();

    @Test
    void saveHit() throws Exception {
        mvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(hitDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(statsService, times(1)).saveHit(any());
    }

    @Test
    void getStats() throws Exception {
        when(statsService.getStats(any(), any(), any(), anyBoolean()))
                .thenReturn(List.of(viewStatsDto));

        mvc.perform(get("/stats")
                        .param("start", "2001-01-01 13:13:13")
                        .param("end", "2023-03-03 15:15:15")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].app", is(viewStatsDto.getApp())))
                .andExpect(jsonPath("$[0].uri", is(viewStatsDto.getUri())))
                .andExpect(jsonPath("$[0].hits").value(viewStatsDto.getHits()));
    }

    @Test
    void getStatsByIncorrectDateParams() throws Exception {
        mvc.perform(get("/stats")
                        .param("end", "2001-01-01 13:13:13")
                        .param("start", "2023-03-03 15:15:15")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
