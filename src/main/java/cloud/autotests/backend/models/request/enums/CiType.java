package cloud.autotests.backend.models.request.enums;

import com.google.gson.annotations.SerializedName;

public enum CiType {

    @SerializedName("Jenkins")
    JENKINS("Jenkins"),
    @SerializedName("Github Actions")
    GITHUB_ACTIONS("Github Actions"),
    @SerializedName("Gitlab Runner")
    GITLAB_RUNNER("Gitlab Runner");

    private final String name;

    CiType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
