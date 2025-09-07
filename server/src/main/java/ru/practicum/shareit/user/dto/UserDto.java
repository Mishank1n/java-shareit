package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    Long id;
    @NotBlank
    String name;
    @Email
    String email;

    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}
