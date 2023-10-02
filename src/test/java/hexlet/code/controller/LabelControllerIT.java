package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
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
import static hexlet.code.controller.LabelController.ID;
import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static hexlet.code.utils.TestUtils.TEST_LABEL_NAME;
import static hexlet.code.utils.TestUtils.TEST_LABEL_NAME_2;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
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
public class LabelControllerIT {

    @Autowired
    private LabelRepository labelRepository;

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
    public void createLabel() throws Exception {
        assertEquals(0, labelRepository.count());

        final MockHttpServletResponse response = utils.createDefaultLabel()
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final Label savedLabel = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(1, labelRepository.count());
        assertThat(labelRepository.getReferenceById(savedLabel.getId())).isNotNull();
    }

    @Test
    public void createLabelFail() throws Exception {
        final LabelDto labelDto = new LabelDto();

        final MockHttpServletRequestBuilder request = post(LABEL_CONTROLLER_PATH)
                .content(asJson(labelDto))
                .contentType(APPLICATION_JSON);

        utils.perform(request).andExpect(status().isForbidden());
    }

    @Test
    public void twiceCreateTheSameLabelFail() throws Exception {
        utils.createDefaultLabel().andExpect(status().isCreated());
        utils.createDefaultLabel().andExpect(status().isUnprocessableEntity());

        assertEquals(1, labelRepository.count());
    }

    @Test
    public void getLabelById() throws Exception {
        utils.createDefaultLabel();

        final Label expectedLabel = labelRepository.findAll().get(0);
        final MockHttpServletResponse response = utils.perform(
                get(LABEL_CONTROLLER_PATH + ID, expectedLabel.getId()),
                TEST_USERNAME
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Label label = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(expectedLabel.getId(), label.getId());
        assertEquals(expectedLabel.getName(), label.getName());
    }

    @Test
    public void getLabelByIdFail() throws Exception {
        utils.createDefaultLabel();

        final Label expectedLabel = labelRepository.findAll().get(0);

        utils.perform(
                get(LABEL_CONTROLLER_PATH + ID, expectedLabel.getId() + 1),
                TEST_USERNAME
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllLabels() throws Exception {
        utils.createDefaultLabel();

        final MockHttpServletResponse response = utils.perform(
                get(LABEL_CONTROLLER_PATH),
                        TEST_USERNAME
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Label> labels = fromJson(response.getContentAsString(), new TypeReference<>() { });
        final List<Label> expectedLabels = labelRepository.findAll();

        assertThat(labels).hasSize(1);
        assertThat(labels).containsAll(expectedLabels);
    }

    @Test
    public void updateLabel() throws Exception {
        utils.createDefaultLabel();

        final Long labelId = labelRepository.findByName(TEST_LABEL_NAME).get().getId();
        final LabelDto labelDtoForUpdate = new LabelDto(TEST_LABEL_NAME_2);

        final MockHttpServletRequestBuilder updateRequest = put(
                LABEL_CONTROLLER_PATH + ID, labelId
                )
                .content(asJson(labelDtoForUpdate))
                .contentType(APPLICATION_JSON);

        utils.perform(updateRequest, TEST_USERNAME).andExpect(status().isOk());

        final Label expectedLabel = labelRepository.findAll().get(0);

        assertEquals(expectedLabel.getId(), labelId);
        assertNotEquals(expectedLabel.getName(), TEST_LABEL_NAME);
        assertEquals(expectedLabel.getName(), TEST_LABEL_NAME_2);
    }

    @Test
    public void updateLabelFail() throws Exception {
        utils.createDefaultLabel();

        final Long labelId = labelRepository.findByName(TEST_LABEL_NAME).get().getId();
        final LabelDto labelDtoForUpdate = new LabelDto("");

        final MockHttpServletRequestBuilder updateRequest = put(
                LABEL_CONTROLLER_PATH + ID, labelId
                )
                .content(asJson(labelDtoForUpdate))
                .contentType(APPLICATION_JSON);

        utils.perform(updateRequest, TEST_USERNAME).andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void deleteLabel() throws Exception {
        utils.createDefaultLabel();

        final Long labelId = labelRepository.findByName(TEST_LABEL_NAME).get().getId();

        utils.perform(delete(LABEL_CONTROLLER_PATH + ID, labelId), TEST_USERNAME)
                .andExpect(status().isOk());

        assertFalse(labelRepository.existsById(labelId));
    }

    @Test
    public void deleteLabelFail() throws Exception {
        utils.createDefaultLabel();

        final Long labelId = labelRepository.findByName(TEST_LABEL_NAME).get().getId();

        utils.perform(delete(LABEL_CONTROLLER_PATH + ID, labelId))
                .andExpect(status().isForbidden());
    }
}
