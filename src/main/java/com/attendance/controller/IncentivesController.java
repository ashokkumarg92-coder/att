package com.attendance.controller;

import com.attendance.model.AttendanceResponse;
import com.attendance.service.IncentiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/incentives")
@CrossOrigin(origins = "*", maxAge = 3600)
public class IncentivesController {


    @Autowired
    private IncentiveService incentiveService;


    @GetMapping("/getIncentives")
    public ResponseEntity<byte[]> searchAttendance(
            @RequestParam String empId) {

        try {
            return incentiveService.getIncentives(empId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
