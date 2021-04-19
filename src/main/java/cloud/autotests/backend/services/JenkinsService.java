package cloud.autotests.backend.services;

import cloud.autotests.backend.builders.TestBuilder;
import cloud.autotests.backend.config.GithubConfig;
import cloud.autotests.backend.config.JenkinsConfig;
import cloud.autotests.backend.models.Order;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Base64;

public class JenkinsService {
    private static final Logger LOG = LoggerFactory.getLogger(JenkinsService.class);

    @Autowired
    public JenkinsService(JenkinsConfig jenkinsConfig) {

    }

}
