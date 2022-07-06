package cloud.autotests.backend.controllers;

import kong.unirest.Unirest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.lang.String.format;

@RestController
@RequestMapping("jenkins")
public class FakeJenkinsLogsController {

    @GetMapping("/{id}")
    public String getLogsById(@PathVariable String id) {
        String jenkinsLog = Unirest
                .get(format("https://jenkins.autotests.cloud/job/%s/1/logText/progressiveText?start=0", id))
                .asString().getBody();
        return jenkinsLog;
    }
}
