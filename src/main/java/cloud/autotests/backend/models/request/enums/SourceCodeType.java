package cloud.autotests.backend.models.request.enums;

import com.google.gson.annotations.SerializedName;

public enum SourceCodeType {

    @SerializedName("Gitlab")
    GITLAB("Gitlab"),
    @SerializedName("Github")
    GITHUB("Github"),
    @SerializedName("download.zip")
    DOWNLOAD_ZIP("download.zip");

    private final String name;

    SourceCodeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
