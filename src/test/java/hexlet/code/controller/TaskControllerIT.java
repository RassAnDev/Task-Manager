package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.Set;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.TaskController.ID;
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.utils.TestUtils.TEST_TASK_NAME;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.utils.TestUtils.TEST_USERNAME_2;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class TaskControllerIT {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestUtils utils;

    @BeforeEach
    public void before() throws Exception {
        utils.regDefaultUser();
        utils.createDefaultTaskStatus();
        utils.createDefaultLabel();
    }

    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    @Test
    public void createTask() throws Exception {
        assertEquals(0, taskRepository.count());
        utils.createDefaultTask().andExpect(status().isCreated());
        assertEquals(1, taskRepository.count());
        assertEquals(taskRepository.findAll().get(0).getName(), TEST_TASK_NAME);
    }

    @Test
    public void createTaskFail() throws Exception {
        final TaskDto taskDto = new TaskDto();

        final MockHttpServletRequestBuilder request = post(TASK_CONTROLLER_PATH)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);

        utils.perform(request).andExpect(status().isForbidden());
    }

    @Test
    public void twiceCreateTheSameTaskFail() throws Exception {
        utils.createDefaultTask().andExpect(status().isCreated());
        utils.createDefaultTask().andExpect(status().isUnprocessableEntity());

        assertEquals(1, taskRepository.count());
    }

    @Test
    public void getTaskById() throws Exception {
        utils.createDefaultTask();

        final Task expectedTask = taskRepository.findAll().get(0);
        final MockHttpServletResponse response = utils.perform(
                get(TASK_CONTROLLER_PATH + ID, expectedTask.getId()),
                TEST_USERNAME
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Task task = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(expectedTask.getId(), task.getId());
        assertEquals(expectedTask.getName(), task.getName());
        assertEquals(expectedTask.getDescription(), task.getDescription());
        assertEquals(expectedTask.getTaskStatus().getName(), task.getTaskStatus().getName());
        assertEquals(expectedTask.getAuthor().getEmail(), task.getAuthor().getEmail());
        assertEquals(expectedTask.getExecutor().getEmail(), task.getExecutor().getEmail());
    }

    @Test
    public void getTaskByIdFail() throws Exception {
        utils.createDefaultTask();

        final Task expectedTask = taskRepository.findAll().get(0);

        utils.perform(
                get(TASK_CONTROLLER_PATH + ID, expectedTask.getId() + 1),
                TEST_USERNAME
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllTasks() throws Exception {
        utils.createDefaultTask();

        final MockHttpServletResponse response = utils.perform(
                get(TASK_CONTROLLER_PATH),
                        TEST_USERNAME
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() { });
        final List<Task> expectedTasks =  taskRepository.findAll();

        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getName()).isEqualTo(expectedTasks.get(0).getName());
    }

    @Test
    public void getSortedAllTasks() throws Exception {
        utils.createDefaultTask();

        final Task expectedTask = taskRepository.findAll().get(0);
        final Long expectedLabelId = expectedTask.getLabels().stream().findFirst().get().getId();

        final String queryString = String.format("?taskStatus=%d&executorId=%d&authorId=%d&labelsId=%d",
                expectedTask.getTaskStatus().getId(),
                expectedTask.getExecutor().getId(),
                expectedTask.getAuthor().getId(),
                expectedLabelId);

        final MockHttpServletResponse response = utils.perform(
                get(TASK_CONTROLLER_PATH + queryString),
                        TEST_USERNAME
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() { });

        final Task currentTask = tasks.get(0);
        final Long currentLabelId = currentTask.getLabels().stream().findFirst().get().getId();

        assertThat(tasks).hasSize(1);
        assertEquals(expectedTask.getName(), currentTask.getName());
        assertEquals(expectedTask.getTaskStatus().getName(), currentTask.getTaskStatus().getName());
        assertEquals(expectedTask.getExecutor().getEmail(), currentTask.getExecutor().getEmail());
        assertEquals(expectedTask.getAuthor().getFirstName(), currentTask.getAuthor().getFirstName());
        assertEquals(expectedLabelId, currentLabelId);
    }

    @Test
    public void updateTask() throws Exception {
        utils.createDefaultTask();

        final Long taskId = taskRepository.findByName(TEST_TASK_NAME).get().getId();
        final Task currentTask = taskRepository.findAll().get(0);
        final TaskDto taskDtoForUpdate = buildTaskDtoForUpdate(currentTask);

        final MockHttpServletRequestBuilder updateRequest = put(
                TASK_CONTROLLER_PATH + ID, taskId
                )
                .content(asJson(taskDtoForUpdate))
                .contentType(APPLICATION_JSON);

        utils.perform(updateRequest, TEST_USERNAME).andExpect(status().isOk());

        final Task expectedTask = taskRepository.findAll().get(0);

        assertEquals(expectedTask.getId(), taskId);
        assertNotEquals(expectedTask.getName(), TEST_TASK_NAME);
        assertEquals(expectedTask.getName(), taskDtoForUpdate.getName());
    }

    @Test
    public void updateTaskFail() throws Exception {
        utils.createDefaultTask();

        final Long taskId = taskRepository.findByName(TEST_TASK_NAME).get().getId();
        final Task currentTask = taskRepository.findAll().get(0);
        final TaskDto taskDtoForUpdate = buildTaskDtoForUpdate(currentTask);
        taskDtoForUpdate.setName("");

        final MockHttpServletRequestBuilder updateRequest = put(
                TASK_CONTROLLER_PATH + ID, taskId
                )
                .content(asJson(taskDtoForUpdate))
                .contentType(APPLICATION_JSON);

        utils.perform(updateRequest, TEST_USERNAME).andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void deleteTaskByOwner() throws Exception {
        utils.createDefaultTask();

        final Long taskId = taskRepository.findByName(TEST_TASK_NAME).get().getId();

        utils.perform(delete(TASK_CONTROLLER_PATH + ID, taskId), TEST_USERNAME)
                .andExpect(status().isOk());

        assertEquals(0, taskRepository.count());
        assertFalse(taskRepository.existsById(taskId));
    }

    @Test
    public void deleteTaskByNotOwner() throws Exception {
        utils.createDefaultTask();

        final Long taskId = taskRepository.findByName(TEST_TASK_NAME).get().getId();

        utils.perform(delete(TASK_CONTROLLER_PATH + ID, taskId), TEST_USERNAME_2)
                .andExpect(status().isForbidden());
    }

    private static TaskDto buildTaskDtoForUpdate(Task task) {
        final String updatedTaskName = "relax";
        final String updatedTaskDescription = "need to find a place to sleep";
        final Label label = task.getLabels().stream().findFirst().get();

        final TaskDto taskDtoForUpdate = new TaskDto();
        taskDtoForUpdate.setName(updatedTaskName);
        taskDtoForUpdate.setDescription(updatedTaskDescription);
        taskDtoForUpdate.setTaskStatusId(task.getTaskStatus().getId());
        taskDtoForUpdate.setAuthorId(task.getAuthor().getId());
        taskDtoForUpdate.setExecutorId(task.getExecutor().getId());
        taskDtoForUpdate.setLabelIds(Set.of(label.getId()));

        return taskDtoForUpdate;
    }
}
