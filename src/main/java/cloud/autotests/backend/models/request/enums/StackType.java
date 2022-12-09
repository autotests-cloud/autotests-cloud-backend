package cloud.autotests.backend.models.request.enums;

import com.google.gson.annotations.SerializedName;

public enum StackType {

    @SerializedName("Java")
    JAVA("Java"),
    @SerializedName("Python")
    PYTHON("Python");

    private final String name;

    StackType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
