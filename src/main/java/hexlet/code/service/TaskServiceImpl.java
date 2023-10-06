package hexlet.code.service;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    private final UserService userService;

    private final TaskStatusService taskStatusService;

    private final LabelService labelService;

    public Task getTaskById(final Long id) {
        return taskRepository.findById(id).orElseThrow();
    }

    public List<Task> getAllTasks(Predicate predicate) {
        return (List<Task>) taskRepository.findAll(predicate);
    }

    public Task createNewTask(final TaskDto taskDto) {
        final Task newTask = fromDto(taskDto);

        return taskRepository.save(newTask);
    }

    public Task updateTask(final Long id, final TaskDto taskDto) {
        final Task taskForUpdate = taskRepository.findById(id).orElseThrow();

        merge(taskForUpdate, taskDto);

        return taskRepository.save(taskForUpdate);
    }

    public void deleteTask(final Long id) {
        final Task taskForDelete = taskRepository.findById(id).orElseThrow();

        taskRepository.delete(taskForDelete);
    }

    private void merge(final Task task, final TaskDto taskDto) {
        final Task newTask = fromDto(taskDto);
        task.setName(newTask.getName());
        task.setDescription(newTask.getDescription());
        task.setTaskStatus(newTask.getTaskStatus());
        task.setAuthor(newTask.getAuthor());
        task.setExecutor(newTask.getExecutor());
    }

    private Task fromDto(final TaskDto taskDto) {

        final User author = userService.getCurrentUser();

        final User executor = Optional.ofNullable(taskDto.getExecutorId())
                .map(userService::getUserById)
                .orElse(null);

        final TaskStatus taskStatus = Optional.ofNullable(taskDto.getTaskStatusId())
                .map(taskStatusService::getTaskStatusById)
                .orElse(null);

        final Set<Label> labels = Optional.ofNullable(taskDto.getLabelIds())
                .orElse(Set.of())
                .stream()
                .filter(Objects::nonNull)
                .map(labelService::getLabelById)
                .collect(Collectors.toSet());

        return Task.builder()
                .name(taskDto.getName())
                .description(taskDto.getDescription())
                .author(author)
                .executor(executor)
                .taskStatus(taskStatus)
                .labels(labels)
                .build();
    }
}
