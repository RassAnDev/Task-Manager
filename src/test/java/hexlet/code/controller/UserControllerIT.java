package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.LoginDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
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
import static hexlet.code.config.security.SecurityConfig.LOGIN;
import static hexlet.code.controller.UserController.ID;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class UserControllerIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestUtils utils;

    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    @Test
    public void registration() throws Exception {
        assertEquals(0, userRepository.count());

        MockHttpServletResponse response = utils.regDefaultUser()
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final User savedUser = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(1, userRepository.count());
        assertThat(userRepository.getReferenceById(savedUser.getId())).isNotNull();
    }

    @Test
    public void login() throws Exception {
        utils.regDefaultUser();

        final LoginDto loginDto = new LoginDto(
                utils.getTestDtoForRegistration().getEmail(),
                utils.getTestDtoForRegistration().getPassword()
        );

        final MockHttpServletRequestBuilder loginRequest = post(LOGIN).content(asJson(loginDto))
                .contentType(APPLICATION_JSON);

        utils.perform(loginRequest).andExpect(status().isOk());
    }

    @Test
    public void loginFail() throws Exception {
        final LoginDto loginDto = new LoginDto(
                utils.getTestDtoForRegistration().getEmail(),
                utils.getTestDtoForRegistration().getPassword()
        );

        final MockHttpServletRequestBuilder loginRequest = post(LOGIN).content(asJson(loginDto))
                .contentType(APPLICATION_JSON);

        utils.perform(loginRequest).andExpect(status().isUnauthorized());
    }

    @Test
    public void twiceRegTheSameUserFail() throws Exception {
        utils.regDefaultUser().andExpect(status().isCreated());
        utils.regDefaultUser().andExpect(status().isUnprocessableEntity());

        assertEquals(1, userRepository.count());
    }

    @Test
    public void getUserById() throws Exception {
        utils.regDefaultUser();
        final User expectedUser = userRepository.findByEmail(TEST_USERNAME).orElseThrow();
        final MockHttpServletResponse response = utils.perform(
                get(USER_CONTROLLER_PATH + ID, expectedUser.getId()),
                expectedUser.getEmail()
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final User user = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(expectedUser.getId(), user.getId());
        assertEquals(expectedUser.getEmail(), user.getEmail());
        assertEquals(expectedUser.getFirstName(), user.getFirstName());
        assertEquals(expectedUser.getLastName(), user.getLastName());
    }

    @Test
    public void getUserByIdFail() throws Exception {
        utils.regDefaultUser();
        final User expectedUser = userRepository.findByEmail(TEST_USERNAME).orElseThrow();

        utils.perform(
               get(USER_CONTROLLER_PATH + ID, expectedUser.getId() + 1),
                       expectedUser.getEmail()
               )
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllUsers() throws Exception {
        utils.regDefaultUser();
        final MockHttpServletResponse response = utils.perform(get(USER_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<User> users = fromJson(response.getContentAsString(), new TypeReference<>() { });
        final List<User> expectedUsers = userRepository.findAll();

        assertThat(users).hasSize(1);
        assertThat(users).containsAll(expectedUsers);
    }

    @Test
    public void updateUser() throws Exception {
        utils.regDefaultUser();

        final Long userId = userRepository.findByEmail(TEST_USERNAME).orElseThrow().getId();

        final UserDto userDto = new UserDto(
                TEST_USERNAME_2,
                "Yennefer",
                "from Vengerberg",
                "geralt"
        );

        final MockHttpServletRequestBuilder updateRequest = put(USER_CONTROLLER_PATH + ID, userId)
                .content(asJson(userDto))
                .contentType(APPLICATION_JSON);

        utils.perform(updateRequest, TEST_USERNAME).andExpect(status().isOk());

        final User expectedUser = userRepository.findById(userId).orElseThrow();

        assertEquals(expectedUser.getId(), userId);
        assertNotEquals(expectedUser.getEmail(), TEST_USERNAME);
        assertEquals(expectedUser.getEmail(), TEST_USERNAME_2);
    }

    @Test
    public void updateUserFail() throws Exception {
        utils.regDefaultUser();

        final Long userId = userRepository.findByEmail(TEST_USERNAME).orElseThrow().getId();

        final UserDto userDto = new UserDto(
                TEST_USERNAME_2,
                "",
                "",
                ""
        );

        final MockHttpServletRequestBuilder updatedRequest = put(USER_CONTROLLER_PATH + ID, userId)
                .content(asJson(userDto))
                .contentType(APPLICATION_JSON);

        utils.perform(updatedRequest, TEST_USERNAME).andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void deleteUser() throws Exception {
        utils.regDefaultUser();

        final Long userId = userRepository.findByEmail(TEST_USERNAME).orElseThrow().getId();

        utils.perform(delete(USER_CONTROLLER_PATH + ID, userId), TEST_USERNAME)
                .andExpect(status().isOk());

        assertFalse(userRepository.existsById(userId));
    }

    @Test
    public void deleteUserFail() throws Exception {
        utils.regDefaultUser();

        final Long userId = userRepository.findByEmail(TEST_USERNAME).orElseThrow().getId();

        utils.perform(delete(USER_CONTROLLER_PATH + ID, userId), TEST_USERNAME_2)
                .andExpect(status().isForbidden());
    }
}
