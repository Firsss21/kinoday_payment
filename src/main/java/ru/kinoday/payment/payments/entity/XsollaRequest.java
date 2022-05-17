package ru.kinoday.payment.payments.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class XsollaRequest {
    private String token;
}
