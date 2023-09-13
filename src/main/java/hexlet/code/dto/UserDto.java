package hexlet.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 1, message = "Имя должно быть не менее 1 символа")
    private String firstName;

    @NotBlank
    @Size(min = 1, message = "Фамилия должна быть не менее 1 символа")
    private String lastName;

    @NotBlank
    @Size(min = 3, max = 255, message = "Пароль должен быть от 3 до 255 символов")
    private String password;

}
