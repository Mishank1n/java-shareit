package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * TODO Sprint add-controllers.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class User {
    Long id;
    String name;
    @Email(message = "Адрес электронной почты должен быть реальным!")
    String email;
}