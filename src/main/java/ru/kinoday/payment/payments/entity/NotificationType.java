package ru.kinoday.payment.payments.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NotificationType {
    USER_VALIDATION("user_validation"),
    USER_SEARCH("user_search"),
    PAYMENT("payment"),
    ;

    private final String text;

    public static NotificationType valueOfText(String text) {
        for (NotificationType value : NotificationType.values()) {
            if (value.text.equals(text))
                return value;
        }
        return null;
    }
}
