package hexlet.code.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    @NotBlank
    @Size(min = 1, message = "Название задачи должно быть не менее 1 символа")
    private String name;

    private String description;

    @NotNull
    private Long taskStatusId;

    @NotNull
    private Long authorId;

    private Long executorId;
}
