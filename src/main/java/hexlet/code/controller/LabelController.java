package hexlet.code.controller;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@RestController
@SecurityRequirement(name = "javainuseapi")
@RequestMapping("${base-url}" + LABEL_CONTROLLER_PATH)
public class LabelController {

    public static final String LABEL_CONTROLLER_PATH = "/labels";
    public static final String ID = "/{id}";

    private final LabelService labelService;

    @Operation(summary = "Create a new label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Label created",
                    content = @Content(schema = @Schema(implementation = Label.class))),
            @ApiResponse(responseCode = "422", description = "Cannot create label with this data",
                    content = @Content(schema = @Schema(implementation = Label.class)))
    })
    @PostMapping
    @ResponseStatus(CREATED)
    public Label createNew(@RequestBody @Valid final LabelDto labelDto) {
        return labelService.createNewLabel(labelDto);
    }

    @Operation(summary = "Get all labels")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Labels found",
                    content = @Content(schema = @Schema(implementation = Label.class))),
            @ApiResponse(responseCode = "404", description = "Labels not found",
                    content = @Content(schema = @Schema(implementation = Label.class)))
    })
    @GetMapping
    public List<Label> getAll() {
        return labelService.getAllLabels();
    }

    @Operation(summary = "Get label by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label found",
                    content = @Content(schema = @Schema(implementation = Label.class))),
            @ApiResponse(responseCode = "404", description = "Label not found",
                    content = @Content(schema = @Schema(implementation = Label.class)))
    })
    @GetMapping(ID)
    public Label getLabelById(@PathVariable final Long id) {
        return labelService.getLabelById(id);
    }

    @Operation(summary = "Update label by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label updated",
                    content = @Content(schema = @Schema(implementation = Label.class))),
            @ApiResponse(responseCode = "422", description = "Cannot update label with this data",
                    content = @Content(schema = @Schema(implementation = Label.class))),
            @ApiResponse(responseCode = "404", description = "Label not found",
                    content = @Content(schema = @Schema(implementation = Label.class)))
    })
    @PutMapping(ID)
    public Label updateLabel(@PathVariable final Long id, @RequestBody @Valid final LabelDto labelDto) {
        return labelService.updateLabel(id, labelDto);
    }

    @Operation(summary = "Delete label by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label deleted",
                    content = @Content(schema = @Schema(implementation = Label.class))),
            @ApiResponse(responseCode = "404", description = "Label not found",
                    content = @Content(schema = @Schema(implementation = Label.class)))
    })
    @DeleteMapping(ID)
    public void deleteLabel(@PathVariable final Long id) {
        labelService.deleteLabel(id);
    }
}
