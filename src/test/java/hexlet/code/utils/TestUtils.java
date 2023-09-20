package hexlet.code.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.component.JWTHelper;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;

import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestUtils {

    public static final String TEST_USERNAME = "kaermorhen@gmail.com";
    public static final String TEST_USERNAME_2 = "vengerberg@gmail.com";

    public static final String TEST_TASK_NAME = "earn a living";

    public static final String TEST_TASK_DESCRIPTION = "need to find orders";

    public static final String TEST_TASK_STATUS_NAME = "looking for orders";

    private final UserDto testDtoForRegistration = new UserDto(
            TEST_USERNAME,
            "Geralt",
            "from Rivia",
            "yennefer"
    );

    private final TaskStatusDto testDtoForTaskStatus = new TaskStatusDto(TEST_TASK_STATUS_NAME);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private JWTHelper jwtHelper;


    public void tearDown() {
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();
        taskRepository.deleteAll();
    }

    public UserDto getTestDtoForRegistration() {
        return testDtoForRegistration;
    }

    public User getUserByEmail(final String email) {
        return userRepository.findByEmail(email).get();
    }

    private TaskDto buildDefaultTaskDto() throws Exception {
        regDefaultUser();
        final User user = userRepository.findByEmail(TEST_USERNAME).get();

        createDefaultTaskStatus();
        final TaskStatus taskStatus = taskStatusRepository.findByName(TEST_TASK_STATUS_NAME).get();

        final TaskDto taskDto = new TaskDto();
        taskDto.setName(TEST_TASK_NAME);
        taskDto.setDescription(TEST_TASK_DESCRIPTION);
        taskDto.setTaskStatusId(taskStatus.getId());
        taskDto.setAuthorId(user.getId());
        //taskDto.setExecutorId(user.getId());

        return taskDto;
    }

    public ResultActions regDefaultUser() throws Exception {
        return regUser(testDtoForRegistration);
    }

    public ResultActions createDefaultTask() throws Exception {
        return createTask(buildDefaultTaskDto());
    }

    public ResultActions createDefaultTaskStatus() throws Exception {
        return createTaskStatus(testDtoForTaskStatus);
    }

    public ResultActions regUser(final UserDto userDto) throws Exception {
        final MockHttpServletRequestBuilder request = post(USER_CONTROLLER_PATH)
                .content(asJson(userDto))
                .contentType(APPLICATION_JSON);

        return perform(request);
    }


    public ResultActions createTask(final TaskDto taskDto) throws Exception {
        final MockHttpServletRequestBuilder request = post(TASK_CONTROLLER_PATH)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);

        return perform(request, TEST_USERNAME);
    }

    public ResultActions createTaskStatus(final TaskStatusDto taskStatusDto) throws Exception {
        final MockHttpServletRequestBuilder request = post(TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(taskStatusDto))
                .contentType(APPLICATION_JSON);

        return perform(request, TEST_USERNAME);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request, final String byUser) throws Exception {
        final String token = jwtHelper.expiring(Map.of("username", byUser));
        request.header(AUTHORIZATION, token);

        return perform(request);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    public static String asJson(final Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }

    public static <T> T fromJson(final String json, final TypeReference<T> to) throws JsonProcessingException {
        return MAPPER.readValue(json, to);
    }
}
