package cloud.autotests.backend.models.request.enums;

import com.google.gson.annotations.SerializedName;

public enum BrowserHubType {

    @SerializedName("Selenoid")
    SELENOID("Selenoid"),
    @SerializedName("Selenium hub")
    SELENIUM_HUB("Selenium hub"),
    @SerializedName("Local")
    LOCAL("Local");

    private final String name;

    BrowserHubType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
