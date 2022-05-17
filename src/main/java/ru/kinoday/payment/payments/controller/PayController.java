package ru.kinoday.payment.payments.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kinoday.payment.payments.entity.PayRequest;
import ru.kinoday.payment.payments.service.PaymentService;

@AllArgsConstructor
@RestController
@RequestMapping("/pay/")
public class PayController {

    private PaymentService paymentService;

    @GetMapping()
    public String getPaymentLink(@ModelAttribute PayRequest request) {
        return paymentService.getPaymentLink(request);
    }


}
