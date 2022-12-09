package cloud.autotests.backend.controllers;

import cloud.autotests.backend.models.request.GenerateRequest;
import cloud.autotests.backend.services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/autotest")
@RequiredArgsConstructor
@Valid
public class GenerateProjectController {
    private final ProjectService projectService;

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody @Valid GenerateRequest request) {
        return projectService.generate(request);
    }
}
