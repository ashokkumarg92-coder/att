@echo off
REM Script to invalidate IntelliJ IDEA cache and rebuild project

echo ================================================
echo Attendance API - IDE Cache Invalidation & Build
echo ================================================
echo.

echo Clearing IntelliJ IDEA cache...
rmdir /s /q "%USERPROFILE%\.IntelliJIdea2024\system\caches" 2>nul
rmdir /s /q "%USERPROFILE%\.IntelliJIdea2023\system\caches" 2>nul
rmdir /s /q "%USERPROFILE%\.idea\system\caches" 2>nul

echo Clearing project build output...
rmdir /s /q "out" 2>nul
rmdir /s /q "target" 2>nul

echo.
echo Rebuilding with Maven...
call mvn clean package -DskipTests -q

if %ERRORLEVEL% EQU 0 (
    echo.
    echo BUILD SUCCESSFUL!
    echo.
    echo You can now:
    echo 1. Close and reopen the project in IntelliJ IDEA
    echo 2. Or File ^> Invalidate Caches / Restart
    echo 3. Then run: mvn spring-boot:run
) else (
    echo.
    echo BUILD FAILED! Check errors above.
)

pause
