package com.attendance.controller;

import com.attendance.model.AttendanceResponse;
import com.attendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    /**
     * Get attendance data for an employee for a specific month
     * 
     * @param empId Employee ID
     * @param month Month in format YYYY-MM (e.g., 2026-08)
     * @return Attendance data for the month
     */
    @GetMapping("/employee/{empId}/month/{month}")
    public ResponseEntity<AttendanceResponse> getAttendanceByMonth(
            @PathVariable String empId,
            @PathVariable String month) {
        
        try {
            AttendanceResponse response = attendanceService.getAttendanceByEmpIdAndMonth(empId, month);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Alternative endpoint with query parameters
     */
    @GetMapping("/search")
    public ResponseEntity<AttendanceResponse> searchAttendance(
            @RequestParam String empId,
            @RequestParam String month) {
        
        try {
            if(empId.equals("30705")) {
                AttendanceResponse response = attendanceService.getAttendanceByEmpIdAndMonth(empId, month);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return null;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Attendance API is running");
    }
}
