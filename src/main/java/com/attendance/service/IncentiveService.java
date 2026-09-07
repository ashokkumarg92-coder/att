package com.attendance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class IncentiveService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String EXTERNAL_INCENTIVES_URL = "http://117.192.9.201:7777/reports/rwservlet";


    public ResponseEntity<byte[]> getIncentives(String empId) {
        // Create headers for form-urlencoded
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Create form data
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("hidden_run_parameters", "server=rep_wls_reports_kerpaps&paramform=yes&desformat=pdf&userid=GEN/pinkcity@kpclkerp&report=/u01/app/oracle/config/applications/kpclapp/hrms/gr411.rdf&destype=cache&EMP_CODE=" + empId + "&PR_ID=-1&DIV_CODE=-1&HR=$");

        // Create HTTP entity with headers and body
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);


        ResponseEntity<byte[]> resp = restTemplate.exchange(EXTERNAL_INCENTIVES_URL, HttpMethod.POST, request, byte[].class);

        return resp;

    }
}
