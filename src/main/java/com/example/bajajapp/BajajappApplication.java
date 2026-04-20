package com.example.bajajapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class BajajappApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(BajajappApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("App Started...");

        RestTemplate restTemplate = new RestTemplate();

        // STEP 1: Generate Webhook
        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "Parth Hiwanj");
        requestBody.put("regNo", "ADT23SOCB0704");
        requestBody.put("email", "parthhiwanj06@email.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        System.out.println("Webhook Response: " + response.getBody());

        String webhookUrl = (String) response.getBody().get("webhook");
        String token = (String) response.getBody().get("accessToken");

        // STEP 2: Send SQL Solution
        sendSolution(restTemplate, webhookUrl, token);
    }

    public void sendSolution(RestTemplate restTemplate, String webhookUrl, String token) {

        String finalQuery = "SELECT e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME, COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT FROM EMPLOYEE e1 JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID LEFT JOIN EMPLOYEE e2 ON e1.DEPARTMENT = e2.DEPARTMENT AND e2.DOB > e1.DOB GROUP BY e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME ORDER BY e1.EMP_ID DESC;";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);

        Map<String, String> body = new HashMap<>();
        body.put("finalQuery", finalQuery);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, request, String.class);

        System.out.println("Final Submission Response: " + response.getBody());
    }
}