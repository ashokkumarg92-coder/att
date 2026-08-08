# Attendance API

A Spring Boot REST API for retrieving employee attendance data from an external attendance system.

## Project Structure

```
att/
├── pom.xml
├── README.md
├── QUICK-START.txt
├── run.bat
└── src/
    └── main/
        ├── java/com/attendance/
        │   ├── AttendanceApiApplication.java
        │   ├── config/
        │   │   └── RestTemplateConfig.java
        │   ├── controller/
        │   │   └── AttendanceController.java
        │   ├── model/
        │   │   ├── AttendanceResponse.java
        │   │   └── ExternalApiResponse.java
        │   └── service/
        │       ├── AttendanceService.java
        │       └── ExternalAttendanceService.java
        └── resources/
            └── application.properties
```

## Overview

This API acts as a wrapper around an external attendance system. Instead of maintaining its own database, it:
- Receives requests with employee ID and month
- Calls the external attendance API (`http://117.192.9.195/Attendance_Api/punchReport.php`)
- Transforms the response into a standardized format
- Returns comprehensive attendance statistics

## Build and Run

### Prerequisites
- Java 17 or higher
- Maven 3.8.0 or higher
- Network access to external API

### Building the Project

```bash
cd C:\Users\DELL\IdeaProjects\att
mvn clean install
```

### Running the Application

```bash
mvn spring-boot:run
```

The API will start on `http://localhost:8080`

## API Endpoints

### 1. Get Attendance by Employee ID and Month

**URL:** `/api/attendance/employee/{empId}/month/{month}`

**Method:** GET

**Parameters:**
- `empId` (Path): Employee ID (e.g., EMP001)
- `month` (Path): Month in multiple formats supported:
  - **Month names** (case-insensitive):
    - Full: `january`, `february`, `march`, `april`, `may`, `june`, `july`, `august`, `september`, `october`, `november`, `december`
    - Short: `jan`, `feb`, `mar`, `apr`, `may`, `jun`, `jul`, `aug`, `sep`, `oct`, `nov`, `dec`
  - **Month numbers**: `1` to `12`
  - **YYYY-MM format**: `2026-08`

**Example Requests:**
```bash
# Using full month names
curl http://localhost:8080/api/attendance/employee/EMP001/month/january
curl http://localhost:8080/api/attendance/employee/EMP001/month/february
curl http://localhost:8080/api/attendance/employee/EMP001/month/june

# Using short month names
curl http://localhost:8080/api/attendance/employee/EMP001/month/jan
curl http://localhost:8080/api/attendance/employee/EMP001/month/feb

# Using month numbers
curl http://localhost:8080/api/attendance/employee/EMP001/month/1
curl http://localhost:8080/api/attendance/employee/EMP001/month/6

# Using YYYY-MM format
curl http://localhost:8080/api/attendance/employee/EMP001/month/2026-08
```

**Response:**
```json
{
  "empId": "EMP001",
  "month": "01",
  "year": "2026",
  "totalWorkingDays": 20,
  "presentDays": 18,
  "absentDays": 2,
  "leaveDays": 0,
  "halfDays": 0,
  "totalWorkingHours": 144.0,
  "attendanceDetails": [
    {
      "date": "2026-01-01",
      "status": "PRESENT",
      "workingHours": 8.0,
      "remarks": "09:00 AM to 05:30 PM"
    }
  ]
}

### 2. Get Attendance by Query Parameters

**URL:** `/api/attendance/search`

**Method:** GET

**Query Parameters:**
- `empId`: Employee ID
- `month`: Month (supports all formats: month names, numbers, or YYYY-MM)

**Example Requests:**
```bash
curl "http://localhost:8080/api/attendance/search?empId=EMP001&month=january"
curl "http://localhost:8080/api/attendance/search?empId=EMP001&month=jan"
curl "http://localhost:8080/api/attendance/search?empId=EMP001&month=1"
curl "http://localhost:8080/api/attendance/search?empId=EMP001&month=2026-08"
```

### 3. Health Check

**URL:** `/api/attendance/health`

**Method:** GET

**Example Request:**
```bash
curl http://localhost:8080/api/attendance/health
```

**Response:**
```
Attendance API is running
```

## How It Works

1. **Request:** Client sends employee ID and month
2. **Date Calculation:** System calculates start and end dates for the month
3. **External API Call:** Calls `http://117.192.9.195/Attendance_Api/punchReport.php` with:
   - **Method:** POST
   - **Content-Type:** application/x-www-form-urlencoded
   - **Parameters (Form Body):**
     - `EMP_CODE`: Employee ID
     - `FROM_DATE`: First day of month (YYYY-MM-DD)
     - `TO_DATE`: Last day of month (YYYY-MM-DD)
4. **Response Transformation:** Converts external API response to standardized format
5. **Statistics Calculation:** 
   - Total working days
   - Present days (days with working hours > 0)
   - Absent days (total - present)
   - Total working hours
6. **Return:** Returns formatted JSON with attendance summary and details

## External API Parameters

The application calls: `http://117.192.9.195/Attendance_Api/punchReport.php`

**Request Method:** POST

**Content-Type:** application/x-www-form-urlencoded

**Form Parameters:**
- `EMP_CODE`: Employee code/ID
- `FROM_DATE`: Date in YYYY-MM-DD format
- `TO_DATE`: Date in YYYY-MM-DD format

**Example Request (cURL):**
```bash
curl -X POST http://117.192.9.195/Attendance_Api/punchReport.php \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "EMP_CODE=EMP001&FROM_DATE=2026-08-01&TO_DATE=2026-08-31"
```

## API Response Format

```json
{
  "empId": "string",
  "month": "string (MM format)",
  "year": "string",
  "totalWorkingDays": "number",
  "presentDays": "number",
  "absentDays": "number",
  "leaveDays": "number",
  "halfDays": "number",
  "totalWorkingHours": "number",
  "attendanceDetails": [
    {
      "date": "string (YYYY-MM-DD format)",
      "status": "string",
      "workingHours": "number",
      "remarks": "string"
    }
  ]
}
```

## Technology Stack

- **Framework:** Spring Boot 3.1.5
- **Language:** Java 17
- **Build Tool:** Maven
- **HTTP Client:** RestTemplate
- **JSON Parsing:** Jackson
- **Others:** Lombok for reducing boilerplate code

## Configuration

Update `src/main/resources/application.properties` to change settings:

```properties
spring.application.name=attendance-api
server.port=8080

# External API Configuration
external.api.url=http://117.192.9.195/Attendance_Api/punchReport.php

# Logging
logging.level.root=INFO
logging.level.com.attendance=DEBUG
```

## Error Handling

- If external API is unreachable, returns empty attendance details
- If employee has no attendance records for the month, returns with totalWorkingDays = 0
- All errors are logged but don't stop the application

## Testing

To test the API endpoints, use the provided `test-api.bat` script or curl commands directly.

