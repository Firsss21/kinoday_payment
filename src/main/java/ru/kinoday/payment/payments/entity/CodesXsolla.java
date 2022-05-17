package ru.kinoday.payment.payments.entity;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CodesXsolla {
    INVALID_USER("Invalid user"),
    INVALID_PARAMETER("Invalid parameter"),
    INVALID_SIGNATURE("Invalid signature"),
    INCORRECT_AMOUNT("Incorrect amount"),
    INCORRECT_INVOICE("Incorrect invoice"),

    SUCCESS("Success"),
    SERVICE_UNAVAILABLE("Service currently unavailable"),

    ;

    private final String message;


    public String getFullMessage() {
        JsonObject errorIn = new JsonObject();
        errorIn.addProperty("code", this.name());
        errorIn.addProperty("message", this.getMessage());
        JsonObject error = new JsonObject();
        error.add("error", errorIn);
        return error.toString();
    }
}
