package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;

import java.util.List;

public interface TaskStatusService {

    TaskStatus getTaskStatusById(Long id);

    List<TaskStatus> getAllTaskStatuses();

    TaskStatus createNewTaskStatus(TaskStatusDto taskStatusDto);

    TaskStatus updateTaskStatus(Long id, TaskStatusDto taskStatusDto);

    void deleteTaskStatus(Long id);
}
