package ru.practicum.explorewithme.common.dto.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Builder
public class UserDto {
    private Long id;

    @NotBlank(message = "must not be blank")
    @Email(message = "must be valid")
    @Size(max = 254, min = 6, message = "must be between 6 and 254 characters")
    private String email;

    @NotBlank(message = "must not be blank")
    @Size(max = 250, min = 2, message = "must be between 2 and 250 characters")
    private String name;

    private Set<Long> subscriptions;
}
