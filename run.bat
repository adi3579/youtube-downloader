@echo off
echo ========================================
echo    YouTube Downloader - Build & Run
echo ========================================
echo.

:: Kill any running Java processes first
echo Stopping any running instances...
taskkill /f /im java.exe >nul 2>&1

:: Build the project
echo Building the application...
if exist "mvn.cmd" (
    call mvn.cmd clean package
) else (
    mvn clean package
)

if %errorlevel% neq 0 (
    echo.
    echo BUILD FAILED! Cannot run the application.
    echo.
    pause
    exit /b 1
)

echo.
echo ========================================
echo    Starting YouTube Downloader...
echo ========================================
echo.

:: Run the application
java -jar target\youtube-downloader-1.0.0.jar

echo.
echo Application closed.
pause
