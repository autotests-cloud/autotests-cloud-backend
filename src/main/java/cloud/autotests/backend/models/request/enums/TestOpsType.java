package cloud.autotests.backend.models.request.enums;

import com.google.gson.annotations.SerializedName;

public enum TestOpsType {
    @SerializedName("allure_testops")
    ALLURE("allure");

    private final String name;

    TestOpsType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}