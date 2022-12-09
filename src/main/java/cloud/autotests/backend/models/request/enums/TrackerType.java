package cloud.autotests.backend.models.request.enums;

import com.google.gson.annotations.SerializedName;

public enum TrackerType {

    @SerializedName("jira")
    JIRA("jira");

    private final String name;

    TrackerType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}