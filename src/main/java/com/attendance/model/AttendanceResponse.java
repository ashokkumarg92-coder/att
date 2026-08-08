package com.attendance.model;

import java.util.List;

public class AttendanceResponse {

    private String empId;
    private String month;
    private String year;
    private Integer totalWorkingDays;
    private Integer presentDays;
    private Integer morningShiftDays;
    private Integer eveningShiftDays;
    private Integer nightShiftDays;
    private Integer absentDays;
    private Integer leaveDays;
    private Integer halfDays;
    private Double totalWorkingHours;
    private List<AttendanceDetail> attendanceDetails;

    public AttendanceResponse() {
    }

    public AttendanceResponse(String empId, String month, String year, Integer totalWorkingDays,
                              Integer presentDays, Integer absentDays, Integer leaveDays,
                              Integer halfDays, Double totalWorkingHours, List<AttendanceDetail> attendanceDetails) {
        this.empId = empId;
        this.month = month;
        this.year = year;
        this.totalWorkingDays = totalWorkingDays;
        this.presentDays = presentDays;
        this.absentDays = absentDays;
        this.leaveDays = leaveDays;
        this.halfDays = halfDays;
        this.totalWorkingHours = totalWorkingHours;
        this.attendanceDetails = attendanceDetails;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Integer getTotalWorkingDays() {
        return totalWorkingDays;
    }

    public void setTotalWorkingDays(Integer totalWorkingDays) {
        this.totalWorkingDays = totalWorkingDays;
    }

    public Integer getPresentDays() {
        return presentDays;
    }

    public void setPresentDays(Integer presentDays) {
        this.presentDays = presentDays;
    }

    public Integer getMorningShiftDays() {
        return morningShiftDays;
    }

    public void setMorningShiftDays(Integer morningShiftDays) {
        this.morningShiftDays = morningShiftDays;
    }

    public Integer getEveningShiftDays() {
        return eveningShiftDays;
    }

    public void setEveningShiftDays(Integer eveningShiftDays) {
        this.eveningShiftDays = eveningShiftDays;
    }

    public Integer getNightShiftDays() {
        return nightShiftDays;
    }

    public void setNightShiftDays(Integer nightShiftDays) {
        this.nightShiftDays = nightShiftDays;
    }

    public Integer getAbsentDays() {
        return absentDays;
    }

    public void setAbsentDays(Integer absentDays) {
        this.absentDays = absentDays;
    }

    public Integer getLeaveDays() {
        return leaveDays;
    }

    public void setLeaveDays(Integer leaveDays) {
        this.leaveDays = leaveDays;
    }

    public Integer getHalfDays() {
        return halfDays;
    }

    public void setHalfDays(Integer halfDays) {
        this.halfDays = halfDays;
    }

    public Double getTotalWorkingHours() {
        return totalWorkingHours;
    }

    public void setTotalWorkingHours(Double totalWorkingHours) {
        this.totalWorkingHours = totalWorkingHours;
    }

    public List<AttendanceDetail> getAttendanceDetails() {
        return attendanceDetails;
    }

    public void setAttendanceDetails(List<AttendanceDetail> attendanceDetails) {
        this.attendanceDetails = attendanceDetails;
    }

    public static class AttendanceDetail {
        private String date;
        private String shiftType;
        private Double workingHours;
        private String timeRange;

        public AttendanceDetail() {
        }

        public AttendanceDetail(String date, String shiftType, Double workingHours, String timeRange) {
            this.date = date;
            this.shiftType = shiftType;
            this.workingHours = workingHours;
            this.timeRange = timeRange;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getShiftType() {
            return shiftType;
        }

        public void setShiftType(String shiftType) {
            this.shiftType = shiftType;
        }

        public Double getWorkingHours() {
            return workingHours;
        }

        public void setWorkingHours(Double workingHours) {
            this.workingHours = workingHours;
        }

        public String getTimeRange() {
            return timeRange;
        }

        public void setTimeRange(String timeRange) {
            this.timeRange = timeRange;
        }
    }
}
