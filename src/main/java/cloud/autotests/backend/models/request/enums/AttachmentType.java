package cloud.autotests.backend.models.request.enums;

import com.google.gson.annotations.SerializedName;

public enum AttachmentType {

    @SerializedName("screenshots")
    SCREENSHOTS("screenshots"),
    @SerializedName("video")
    VIDEO("video"),
    @SerializedName("console_logs")
    CONSOLE_LOGS("console_logs"),
    @SerializedName("network_logs")
    NETWORK_LOGS("network_logs"),
    @SerializedName("page_source")
    PAGE_SOURCE("page_source");

    private final String name;

    AttachmentType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
