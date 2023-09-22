package hexlet.code.controller;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@RestController
@SecurityRequirement(name = "javainuseapi")
@RequestMapping("${base-url}" + TASK_CONTROLLER_PATH)
public class TaskController {

    public static final String TASK_CONTROLLER_PATH = "/tasks";
    public static final String ID = "/{id}";

    private static final String ONLY_AUTHOR_BY_ID = """
            @taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.getName()
            """;

    private final TaskService taskService;

    @Operation(summary = "Create a new task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created",
                    content = @Content(schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "422", description = "Cannot create task with this data",
                    content = @Content(schema = @Schema(implementation = Task.class)))
    })
    @PostMapping
    @ResponseStatus(CREATED)
    public Task createNew(@RequestBody @Valid final TaskDto taskDto) {
        return taskService.createNewTask(taskDto);
    }

    @Operation(summary = "Get all tasks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks found",
                    content = @Content(schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "404", description = "Tasks not found",
                    content = @Content(schema = @Schema(implementation = Task.class)))
    })
    @GetMapping
    public List<Task> getAll(Predicate predicate) {
        return taskService.getAllTasks(predicate);
    }

    @Operation(summary = "Get task by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task found",
                    content = @Content(schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = Task.class)))
    })
    @GetMapping(ID)
    public Task getTaskById(@PathVariable final Long id) {
        return taskService.getTaskById(id);
    }

    @Operation(summary = "Update task by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated",
                    content = @Content(schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "422", description = "Cannot update task with this data",
                    content = @Content(schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = Task.class)))
    })
    @PutMapping(ID)
    public Task updateTask(@PathVariable final Long id, @RequestBody @Valid final TaskDto taskDto) {
        return taskService.updateTask(id, taskDto);
    }

    @Operation(summary = "Delete task by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task deleted",
                    content = @Content(schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = Task.class)))
    })
    @DeleteMapping(ID)
    @PreAuthorize(ONLY_AUTHOR_BY_ID)
    public void deleteTask(@PathVariable final Long id) {
        taskService.deleteTask(id);
    }
}
