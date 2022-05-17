package ru.kinoday.payment.payments.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;

import java.sql.Timestamp;
import java.util.Set;

@Value
public class PaymentDTO {
   Long uid;
   @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Europe/Berlin")
   Timestamp created;
   Integer price;
   Set<Long> ticketIds;
   String email;
}
