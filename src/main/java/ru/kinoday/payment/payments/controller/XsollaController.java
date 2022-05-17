package ru.kinoday.payment.payments.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kinoday.payment.payments.entity.CodesXsolla;
import ru.kinoday.payment.payments.entity.NotificationType;
import ru.kinoday.payment.payments.service.PaymentService;
import ru.kinoday.payment.payments.service.XsollaService;

@RequestMapping("/handle/")
@RestController
@AllArgsConstructor
public class XsollaController {

    private XsollaService service;
    private Gson gson;
    private PaymentService paymentService;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> handle(
            @RequestBody(required = true) String data,
            @RequestHeader(name = "Authorization") String auth
    ) {
        if (!service.isSignatureGood(auth.substring(10), data)) {
            return ResponseEntity.badRequest().body(CodesXsolla.INVALID_SIGNATURE.getFullMessage());
        }

        JsonObject request = gson.fromJson(data, JsonObject.class);
        NotificationType type = NotificationType.valueOfText(request.get("notification_type").getAsString());

        if (type == null) {
            return ResponseEntity.badRequest().body(CodesXsolla.INVALID_PARAMETER.getFullMessage());
        }
        System.out.println(type);
        switch (type){
            case PAYMENT: {
                JsonObject customParameters = request.getAsJsonObject("custom_parameters");
                boolean result = paymentService.processNewPayment(customParameters);
                if (result)
                    return ResponseEntity.ok(CodesXsolla.SUCCESS.getFullMessage());
                else
                    return ResponseEntity.ok(CodesXsolla.INVALID_PARAMETER.getFullMessage());
            }
            case USER_SEARCH:
            case USER_VALIDATION: {
                long uid = 0;
                try {
                    uid = request.get("user").getAsJsonObject().get("id").getAsLong();
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body(CodesXsolla.INVALID_USER.getFullMessage());
                }
                if (uid == 0)
                    return ResponseEntity.badRequest().body(CodesXsolla.INVALID_USER.getFullMessage());

                return paymentService.userExist(uid);
            }
        }
        return ResponseEntity.badRequest().body(CodesXsolla.INVALID_PARAMETER.getFullMessage());
    }
}
