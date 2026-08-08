package com.attendance.service;

import com.attendance.model.ExternalApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

@Service
public class ExternalAttendanceService {

    private static final String EXTERNAL_API_URL = "http://117.192.9.195/Attendance_Api/punchReport.php";

    @Autowired
    private RestTemplate restTemplate;

    public ExternalApiResponse getAttendanceData(String empCode, String fromDate, String toDate) {
        try {
            // Create headers for form-urlencoded
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Create form data
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("EMP_CODE", empCode);
            body.add("FROM_DATE", fromDate);
            body.add("TO_DATE", toDate);

            // Create HTTP entity with headers and body
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            System.out.println("Calling external API (POST form-urlencoded)");
            System.out.println("URL: " + EXTERNAL_API_URL);
            System.out.println("Params: EMP_CODE=" + empCode + ", FROM_DATE=" + fromDate + ", TO_DATE=" + toDate);

            String response = restTemplate.postForObject(EXTERNAL_API_URL, request, String.class);
            System.out.println("External API response: " + response);
            return response != null ? new com.fasterxml.jackson.databind.ObjectMapper().readValue(response, ExternalApiResponse.class) : new ExternalApiResponse();
        } catch (Exception e) {
            System.err.println("Error calling external API: " + e.getMessage());
            e.printStackTrace();
            return new ExternalApiResponse();
        }
    }
}

