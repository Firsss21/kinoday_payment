package ru.kinoday.payment.payments.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PayRequest {
    Long uid;
    String email;
    int price;
    Long[] ticketIds;
}
