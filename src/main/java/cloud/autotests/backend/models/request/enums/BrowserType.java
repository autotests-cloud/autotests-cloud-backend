package cloud.autotests.backend.models.request.enums;

import com.google.gson.annotations.SerializedName;

public enum BrowserType {

    @SerializedName("Mozilla")
    MOZILLA("Mozilla"),
    @SerializedName("Chrome")
    CHROME("Chrome"),
    @SerializedName("Safari")
    SAFARI("Safari"),
    @SerializedName("Opera")
    OPERA("Opera");

    private final String name;

    BrowserType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
