package cloud.autotests.backend.models.request.enums;

import com.google.gson.annotations.SerializedName;

public enum NotificationType {

    @SerializedName("Email")
    EMAIL("Email"),
    @SerializedName("Telegram")
    TELEGRAM("Telegram"),
    @SerializedName("Slack")
    SLACK("Slack"),
    @SerializedName("Skype")
    SKYPE("Skype"),
    @SerializedName("Mattermost")
    MATTERMOST("Mattermost"),
    @SerializedName("Icq")
    ICQ("Icq");

    private final String name;

    NotificationType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
