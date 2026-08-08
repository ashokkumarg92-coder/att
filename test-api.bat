@echo off
REM Script to test the Attendance API endpoints

echo ========================================
echo Attendance API - Test Script
echo ========================================
echo.

setlocal enabledelayedexpansion

set "BASE_URL=http://localhost:8080/api/attendance"
set "DELAY=2"

echo Testing API health endpoint...
echo.
curl -X GET "%BASE_URL%/health" -H "Content-Type: application/json"
echo.
echo.

timeout /t %DELAY% /nobreak
echo Testing Attendance for EMP001 in August 2026...
echo Request: GET %BASE_URL%/employee/EMP001/month/2026-08
echo.
curl -X GET "%BASE_URL%/employee/EMP001/month/2026-08" -H "Content-Type: application/json"
echo.
echo.

timeout /t %DELAY% /nobreak
echo Testing Attendance for EMP002 in August 2026...
echo Request: GET %BASE_URL%/employee/EMP002/month/2026-08
echo.
curl -X GET "%BASE_URL%/employee/EMP002/month/2026-08" -H "Content-Type: application/json"
echo.
echo.

echo Testing with query parameters...
echo Request: GET %BASE_URL%/search?empId=EMP001^&month=2026-08
echo.
curl -X GET "%BASE_URL%/search?empId=EMP001&month=2026-08" -H "Content-Type: application/json"
echo.
echo.

echo ========================================
echo Test completed!
echo ========================================
