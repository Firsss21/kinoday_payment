package ru.kinoday.payment.payments.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.kinoday.payment.payments.entity.PayRequest;
import ru.kinoday.payment.payments.entity.XsollaRequest;

import java.util.Arrays;

@Service
public class XsollaService {

    @Value("${xsolla.merchant.id}")
    private int MERCHANT_ID;
    @Value("${xsolla.project.id}")
    private int PROJECT_ID;
    @Value("${xsolla.webhook.key}")
    private String WEBHOOK_KEY;
    @Value("${xsolla.api.key}")
    private String API_KEY;
    @Value("${xsolla.api.url}")
    private String API_URL;
    @Value("${xsolla.url}")
    private String URL;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getPaymentLink(PayRequest request) {
        try {

            JsonObject body = buildJsonObject(request);
            System.out.println(body.toString());

            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(String.valueOf(MERCHANT_ID), API_KEY);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<String>(body.toString(), headers);
            ResponseEntity<XsollaRequest> response = restTemplate.postForEntity(this.API_URL.replace("{merchant_id}", String.valueOf(MERCHANT_ID)), entity, XsollaRequest.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().getToken() != null && !response.getBody().getToken().equals("")) {
                return URL.replace("{token}", response.getBody().getToken());
            }

        } catch (RestClientException e) {
            e.printStackTrace();
            return "/profile";
        }

        return "/profile";
    }

    public boolean isSignatureGood(String auth, String string) {
        String en = encodeXsolla(string);
        return en.equals(auth);
    }

    public String encodeXsolla(String data) {
        String str = data + WEBHOOK_KEY;
        return DigestUtils.sha1Hex(str);
    }

    private JsonObject buildJsonObject(PayRequest request) {

        JsonObject user = new JsonObject();
        JsonObject id = new JsonObject();
        id.addProperty("value", String.valueOf(request.getUid()));
        JsonObject name = new JsonObject();
        name.addProperty("value", request.getEmail());
        JsonObject email = new JsonObject();
        email.addProperty("value", request.getEmail());
        user.add("id", id);
        user.add("email", email);
        user.add("name", name);

        JsonObject settings = new JsonObject();
        settings.addProperty("project_id", this.PROJECT_ID);
        settings.addProperty("currency", "RUB");
        settings.addProperty("language", "ru");
        settings.addProperty("mode", "sandbox");
        settings.addProperty("payment_method", 26);

        JsonObject purchase = new JsonObject();
        JsonObject cur = new JsonObject();
        cur.addProperty("quantity", request.getPrice());
        purchase.add("virtual_currency", cur);

        JsonObject customParameters = new JsonObject();
        customParameters.addProperty("email", request.getEmail());
        customParameters.addProperty("uid", request.getUid());
        customParameters.addProperty("price", request.getPrice());
        customParameters.addProperty("ticketIds", Arrays.toString(request.getTicketIds()));

        JsonObject body = new JsonObject();
        body.add("user", user);
        body.add("purchase", purchase);
        body.add("settings", settings);
        body.add("custom_parameters", customParameters);
        return body;
    }
}
