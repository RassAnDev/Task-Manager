package hexlet.code.service;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;

import java.util.List;

public interface LabelService {

    Label getLabelById(Long id);

    List<Label> getAllLabels();

    Label createNewLabel(LabelDto labelDto);

    Label updateLabel(Long id, LabelDto labelDto);

    void deleteLabel(Long id);
}
