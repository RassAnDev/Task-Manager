package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    private final UserService userService;

    private final TaskStatusService taskStatusService;

    public Task getTaskById(final Long id) {
        return taskRepository.findById(id).orElseThrow();
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task createNewTask(final TaskDto taskDto) {
        final Task newTask = fromDto(taskDto);

        return taskRepository.save(newTask);
    }

    public Task updateTask(final Long id, final TaskDto taskDto) {
        final Task taskForUpdate = taskRepository.findById(id).get();

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

        final Task task = new Task();

        final User author = userService.getCurrentUser();
        final User executor = userService.getUserById(taskDto.getExecutorId());
        final TaskStatus taskStatus = taskStatusService.getTaskStatusById(taskDto.getTaskStatusId());

        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setAuthor(author);
        task.setTaskStatus(taskStatus);

        if (executor != null) {
            task.setExecutor(executor);
        }

        return task;
    }
}
