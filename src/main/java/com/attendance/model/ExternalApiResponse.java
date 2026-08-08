package com.attendance.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ExternalApiResponse {

    @JsonProperty("EXIST")
    private Boolean exist;

    @JsonProperty("DATA")
    private ApiData data;

    public ExternalApiResponse() {
    }

    public ExternalApiResponse(Boolean exist, ApiData data) {
        this.exist = exist;
        this.data = data;
    }

    public Boolean getExist() {
        return exist;
    }

    public void setExist(Boolean exist) {
        this.exist = exist;
    }

    public ApiData getData() {
        return data;
    }

    public void setData(ApiData data) {
        this.data = data;
    }

    public static class ApiData {
        @JsonProperty("DT")
        private List<String> dt;

        public ApiData() {
        }

        public ApiData(List<String> dt) {
            this.dt = dt;
        }

        public List<String> getDt() {
            return dt;
        }

        public void setDt(List<String> dt) {
            this.dt = dt;
        }
    }

    public static class AttendanceRecord {
        @JsonProperty("EMP_CODE")
        private String empCode;

        @JsonProperty("PUNCH_DATE")
        private String punchDate;

        @JsonProperty("IN_TIME")
        private String inTime;

        @JsonProperty("OUT_TIME")
        private String outTime;

        @JsonProperty("WORK_HRS")
        private String workHrs;

        @JsonProperty("STATUS")
        private String status;

        public AttendanceRecord() {
        }

        public AttendanceRecord(String empCode, String punchDate, String inTime, String outTime, String workHrs, String status) {
            this.empCode = empCode;
            this.punchDate = punchDate;
            this.inTime = inTime;
            this.outTime = outTime;
            this.workHrs = workHrs;
            this.status = status;
        }

        public String getEmpCode() {
            return empCode;
        }

        public void setEmpCode(String empCode) {
            this.empCode = empCode;
        }

        public String getPunchDate() {
            return punchDate;
        }

        public void setPunchDate(String punchDate) {
            this.punchDate = punchDate;
        }

        public String getInTime() {
            return inTime;
        }

        public void setInTime(String inTime) {
            this.inTime = inTime;
        }

        public String getOutTime() {
            return outTime;
        }

        public void setOutTime(String outTime) {
            this.outTime = outTime;
        }

        public String getWorkHrs() {
            return workHrs;
        }

        public void setWorkHrs(String workHrs) {
            this.workHrs = workHrs;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}

