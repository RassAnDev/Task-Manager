package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;

    public TaskStatus getTaskStatusById(final Long id) {
        return taskStatusRepository.findById(id).orElseThrow();
    }

    public List<TaskStatus> getAllTaskStatuses() {
        return taskStatusRepository.findAll();
    };

    public TaskStatus createNewTaskStatus(final TaskStatusDto taskStatusDto) {
        final TaskStatus newTaskStatus = fromDto(taskStatusDto);

        return taskStatusRepository.save(newTaskStatus);
    }

    public TaskStatus updateTaskStatus(final Long id, final TaskStatusDto taskStatusDto) {
        final TaskStatus taskStatusForUpdate = taskStatusRepository.findById(id).get();
        taskStatusForUpdate.setName(taskStatusDto.getName());

        return taskStatusRepository.save(taskStatusForUpdate);
    }

    public void deleteTaskStatus(final Long id) {
        final TaskStatus taskStatusForDelete = taskStatusRepository.findById(id).orElseThrow();

        taskStatusRepository.delete(taskStatusForDelete);
    }

    private TaskStatus fromDto(final TaskStatusDto taskStatusDto) {
        return TaskStatus.builder()
                .name(taskStatusDto.getName())
                .build();
    }
}
