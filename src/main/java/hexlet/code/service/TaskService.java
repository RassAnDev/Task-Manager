package hexlet.code.service;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;

import java.util.List;

public interface TaskService {

    Task getTaskById(Long id);

    List<Task> getAllTasks(Predicate predicate);

    Task createNewTask(TaskDto taskDto);

    Task updateTask(Long id, TaskDto taskDto);

    void deleteTask(Long id);
}
