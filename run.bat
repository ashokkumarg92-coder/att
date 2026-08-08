@echo off
REM Script to run the Attendance API

echo Building the project...
call mvn clean install -DskipTests

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Build successful! Starting the application...
    echo.
    echo The API will be available at: http://localhost:8080
    echo.
    echo Test endpoints:
    echo - http://localhost:8080/api/attendance/employee/EMP001/month/2026-08
    echo - http://localhost:8080/api/attendance/search?empId=EMP002^&month=2026-08
    echo - http://localhost:8080/api/attendance/health
    echo.
    call mvn spring-boot:run
) else (
    echo Build failed!
    exit /b 1
)
