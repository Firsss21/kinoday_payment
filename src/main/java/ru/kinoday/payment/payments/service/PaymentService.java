package ru.kinoday.payment.payments.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.kinoday.payment.payments.entity.*;
import ru.kinoday.payment.payments.repository.PaymentRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PaymentService {

    public PaymentService(PaymentRepository paymentRepo, XsollaService xsollaService, Gson gson) {
        this.paymentRepo = paymentRepo;
        this.xsollaService = xsollaService;
        this.gson = gson;
    }

    private PaymentRepository paymentRepo;
    private XsollaService xsollaService;
    private Gson gson;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${service.cinema.uri}")
    private String hostAddress;

    private final String path = "/tickets/";

    public String getPaymentLink(PayRequest request){
        String link = xsollaService.getPaymentLink(request);
        System.out.println(link);
        return link;
    }

    public ResponseEntity<String> userExist(long uid) {
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(hostAddress + path + "user/" + uid)
                .encode()
                .toUriString();

        try {
            ResponseEntity<Boolean> en = restTemplate.getForEntity(
                    urlTemplate,
                    Boolean.class
            );

            if (en.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(204).body(CodesXsolla.SUCCESS.getFullMessage());
            } else if (en.getStatusCode().is5xxServerError()){
                return ResponseEntity.internalServerError().body(CodesXsolla.SERVICE_UNAVAILABLE.getFullMessage());
            } else {
                return ResponseEntity.badRequest().body(CodesXsolla.INVALID_USER.getFullMessage());
            }

        } catch (RestClientException e){
            return ResponseEntity.internalServerError().body(CodesXsolla.SERVICE_UNAVAILABLE.getFullMessage());
        }
    }

    public boolean processNewPayment(JsonObject customParameters) {
        try {
            int price = customParameters.get("price").getAsInt();
            String email = customParameters.get("email").getAsString();
            long uid = customParameters.get("uid").getAsLong();
            Set<Long> ticketIds = gson.fromJson(customParameters.get("ticketIds").getAsString(), new TypeToken<Set<Long>>(){}.getType());
            Payment payment = new Payment(price, ticketIds, PaymentStatus.PROCESSING, uid, email);
            Payment save = paymentRepo.save(payment);
            paymentUpdate();
            System.out.println(payment);
        } catch (Exception e){
            System.out.println(customParameters);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Scheduled(fixedDelay = 30 * 1000)
    private void paymentUpdate() {
        List<Payment> needToGive = paymentRepo.findAllByPaymentStatus(PaymentStatus.PROCESSING);

        for (Payment payment : needToGive) {
            try {
                PaymentDTO paymentDTO = payment.toDto();

                String urlTemplate = UriComponentsBuilder.fromHttpUrl(hostAddress + path + "payment/")
                        .encode()
                        .toUriString();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<String> entity = new HttpEntity<>(gson.toJson(paymentDTO), headers);
                ResponseEntity<Long> response = restTemplate.postForEntity(urlTemplate, entity, Long.class);
                if (response.getStatusCode().is2xxSuccessful()){
                    payment.setPaymentStatus(PaymentStatus.COMPLETED);
                    paymentRepo.save(payment);
                }
            } catch (RestClientException e) {
                e.printStackTrace();
                continue;
            }
        }
    }
}
