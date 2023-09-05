package ru.practicum.explorewithme.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Location {

    @NotNull(message = "must not be null")
    private Double lat;

    @NotNull(message = "must not be null")
    private Double lon;
}
