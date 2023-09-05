package ru.practicum.explorewithme.common.dto.category;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
public class CategoryDto {
    private Integer id;

    @Size(max = 50, min = 1, message = "must be between 1 and 50 characters")
    @NotBlank(message = "must not be blank")
    private String name;
}
