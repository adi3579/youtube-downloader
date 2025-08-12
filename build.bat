@echo off
echo ========================================
echo    Maven Build Script
echo ========================================
echo.

:: Check if mvn.cmd exists in the same directory
if exist "mvn.cmd" (
    echo Using local Maven installation...
    call mvn.cmd clean package
) else (
    echo Local Maven not found, trying system PATH...
    mvn clean package
)

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo    BUILD SUCCESSFUL!
    echo ========================================
    echo.
    echo The application has been built successfully.
    echo You can now run: java -jar target\youtube-downloader-1.0.0.jar
    echo.
) else (
    echo.
    echo ========================================
    echo    BUILD FAILED!
    echo ========================================
    echo.
    echo There was an error during the build process.
    echo Check the error messages above.
    echo.
)

pause
