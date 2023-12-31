package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
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

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.TaskStatusController.ID;
import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static hexlet.code.utils.TestUtils.TEST_TASK_STATUS_NAME;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class TaskStatusControllerIT {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TestUtils utils;

    @BeforeEach
    public void before() throws Exception {
        utils.regDefaultUser();
    }

    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    @Test
    public void createTaskStatus() throws Exception {
        assertEquals(0, taskStatusRepository.count());

        MockHttpServletResponse response = utils.createDefaultTaskStatus()
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final TaskStatus savedTaskStatus = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(1, taskStatusRepository.count());
        assertThat(taskStatusRepository.getReferenceById(savedTaskStatus.getId())).isNotNull();
    }

    @Test
    public void createTaskStatusFail() throws Exception {
        final TaskStatusDto taskStatusDto = new TaskStatusDto("took an order");

        final MockHttpServletRequestBuilder request = post(TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(taskStatusDto))
                .contentType(APPLICATION_JSON);

        utils.perform(request).andExpect(status().isForbidden());
    }

    @Test
    public void twiceCreateTheSameTaskStatusFail() throws Exception {
        utils.createDefaultTaskStatus().andExpect(status().isCreated());
        utils.createDefaultTaskStatus().andExpect(status().isUnprocessableEntity());

        assertEquals(1, taskStatusRepository.count());
    }

    @Test
    public void getTaskStatusById() throws Exception {
        utils.createDefaultTaskStatus();

        final TaskStatus expectedTaskStatus = taskStatusRepository.findByName(TEST_TASK_STATUS_NAME).orElseThrow();
        final MockHttpServletResponse response = utils.perform(
                get(TASK_STATUS_CONTROLLER_PATH + ID, expectedTaskStatus.getId()),
                TEST_USERNAME
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final TaskStatus taskStatus = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(expectedTaskStatus.getId(), taskStatus.getId());
        assertEquals(expectedTaskStatus.getName(), taskStatus.getName());
    }

    @Test
    public void getTaskStatusByIdFail() throws Exception {
        utils.createDefaultTaskStatus();

        final TaskStatus expectedTaskStatus = taskStatusRepository.findByName(TEST_TASK_STATUS_NAME).orElseThrow();

        utils.perform(
                get(TASK_STATUS_CONTROLLER_PATH + ID, expectedTaskStatus.getId() + 1),
                TEST_USERNAME
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllTaskStatuses() throws Exception {
        utils.createDefaultTaskStatus();

        final MockHttpServletResponse response = utils.perform(
                get(TASK_STATUS_CONTROLLER_PATH),
                        TEST_USERNAME
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<TaskStatus> taskStatuses = fromJson(response.getContentAsString(), new TypeReference<>() { });
        final List<TaskStatus> expectedTaskStatuses = taskStatusRepository.findAll();

        assertThat(taskStatuses).hasSize(1);
        assertThat(taskStatuses).containsAll(expectedTaskStatuses);
    }

    @Test
    public void updateTaskStatus() throws Exception {
        utils.createDefaultTaskStatus();

        final Long taskStatusId = taskStatusRepository.findByName(TEST_TASK_STATUS_NAME).orElseThrow().getId();

        final TaskStatusDto taskStatusDtoForUpdate = new TaskStatusDto("took an order");

        final MockHttpServletRequestBuilder updateRequest = put(
                TASK_STATUS_CONTROLLER_PATH + ID, taskStatusId
                )
                .content(asJson(taskStatusDtoForUpdate))
                .contentType(APPLICATION_JSON);

        utils.perform(updateRequest, TEST_USERNAME).andExpect(status().isOk());

        final TaskStatus expectedTaskStatus = taskStatusRepository.findById(taskStatusId).orElseThrow();

        assertEquals(expectedTaskStatus.getId(), taskStatusId);
        assertNotEquals(expectedTaskStatus.getName(), TEST_TASK_STATUS_NAME);
        assertEquals(expectedTaskStatus.getName(), taskStatusDtoForUpdate.getName());
    }

    @Test
    public void updateTaskStatusFail() throws Exception {
        utils.createDefaultTaskStatus();

        final Long taskStatusId = taskStatusRepository.findByName(TEST_TASK_STATUS_NAME).orElseThrow().getId();

        final TaskStatusDto taskStatusDtoForUpdate = new TaskStatusDto("");

        final MockHttpServletRequestBuilder updateRequest = put(
                TASK_STATUS_CONTROLLER_PATH + ID, taskStatusId
                )
                .content(asJson(taskStatusDtoForUpdate))
                .contentType(APPLICATION_JSON);

        utils.perform(updateRequest, TEST_USERNAME).andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void deleteTaskStatus() throws Exception {
        utils.createDefaultTaskStatus();

        final Long taskStatusId = taskStatusRepository.findByName(TEST_TASK_STATUS_NAME).orElseThrow().getId();

        utils.perform(delete(TASK_STATUS_CONTROLLER_PATH + ID, taskStatusId), TEST_USERNAME)
                .andExpect(status().isOk());

        assertFalse(taskStatusRepository.existsById(taskStatusId));
    }

    @Test
    public void deleteTaskStatusFail() throws Exception {
        utils.createDefaultTaskStatus();

        final Long taskStatusId = taskStatusRepository.findByName(TEST_TASK_STATUS_NAME).orElseThrow().getId();

        utils.perform(delete(TASK_STATUS_CONTROLLER_PATH + ID, taskStatusId))
                .andExpect(status().isForbidden());
    }
}
