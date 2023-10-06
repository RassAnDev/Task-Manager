package hexlet.code.service;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;

    public Label getLabelById(final Long id) {
        return labelRepository.findById(id).orElseThrow();
    }

    public List<Label> getAllLabels() {
        return labelRepository.findAll();
    }

    public Label createNewLabel(final LabelDto labelDto) {
        final Label newLabel = fromDto(labelDto);

        return labelRepository.save(newLabel);
    }

    public Label updateLabel(final Long id, final LabelDto labelDto) {
        final Label labelForUpdate = labelRepository.findById(id).orElseThrow();
        labelForUpdate.setName(labelDto.getName());

        return labelRepository.save(labelForUpdate);
    }

    public void deleteLabel(final Long id) {
        final Label labelForDelete = labelRepository.findById(id).orElseThrow();

        labelRepository.delete(labelForDelete);
    }

    private Label fromDto(final LabelDto labelDto) {
        return Label.builder()
                .name(labelDto.getName())
                .build();
    }
}
