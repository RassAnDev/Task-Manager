package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;

    public TaskStatus getTaskStatusById(Long id) {
        return taskStatusRepository.findById(id).orElseThrow();
    }

    public List<TaskStatus> getAllTaskStatuses() {
        return taskStatusRepository.findAll();
    };

    public TaskStatus createNewTaskStatus(TaskStatusDto taskStatusDto) {
        final TaskStatus newTaskStatus = fromDto(taskStatusDto);

        return taskStatusRepository.save(newTaskStatus);
    }

    public TaskStatus updateTaskStatus(Long id, TaskStatusDto taskStatusDto) {
        TaskStatus taskStatusForUpdate = taskStatusRepository.findById(id).orElseThrow();
        taskStatusForUpdate.setName(fromDto(taskStatusDto).getName());

        return taskStatusRepository.save(taskStatusForUpdate);
    }

    public void deleteTaskStatus(Long id) {
        TaskStatus taskStatusForDelete = taskStatusRepository.findById(id).orElseThrow();

        taskStatusRepository.delete(taskStatusForDelete);
    }

    private TaskStatus fromDto(final TaskStatusDto taskStatusDto) {
        return TaskStatus.builder()
                .name(taskStatusDto.getName())
                .build();
    }
}
