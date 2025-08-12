@echo off
echo ========================================
echo    YouTube Downloader Setup
echo ========================================
echo.
echo This script will download all required dependencies:
echo - yt-dlp (standalone executable)
echo - FFmpeg (for video/audio merging)
echo.

:: Check if both are already installed
if exist "yt-dlp.exe" (
    echo yt-dlp.exe found ✓
) else (
    echo yt-dlp.exe not found ✗
)

if exist "ffmpeg.exe" (
    echo ffmpeg.exe found ✓
) else (
    echo ffmpeg.exe not found ✗
)

echo.

:: Download yt-dlp if not present
if not exist "yt-dlp.exe" (
    echo Downloading yt-dlp standalone executable...
    powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp.exe' -OutFile 'yt-dlp.exe'}"
    
    if exist "yt-dlp.exe" (
        echo yt-dlp downloaded successfully! ✓
    ) else (
        echo Failed to download yt-dlp! ✗
    )
    echo.
)

:: Download FFmpeg if not present
if not exist "ffmpeg.exe" (
    echo Downloading FFmpeg...
    
    :: Create temp directory
    if not exist "temp" mkdir temp
    cd temp
    
    powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://github.com/BtbN/FFmpeg-Builds/releases/download/latest/ffmpeg-master-latest-win64-gpl.zip' -OutFile 'ffmpeg.zip'}"
    
    if exist "ffmpeg.zip" (
        echo Extracting FFmpeg...
        powershell -Command "Expand-Archive -Path 'ffmpeg.zip' -DestinationPath '.' -Force"
        
        :: Find and copy ffmpeg.exe
        for /r %%i in (ffmpeg.exe) do (
            copy "%%i" "..\ffmpeg.exe" >nul
            goto :ffmpeg_found
        )
        
        :ffmpeg_found
        cd ..
        rmdir /s /q temp
        
        if exist "ffmpeg.exe" (
            echo FFmpeg downloaded successfully! ✓
        ) else (
            echo Failed to extract FFmpeg! ✗
        )
    ) else (
        cd ..
        echo Failed to download FFmpeg! ✗
    )
    echo.
)

:: Test both executables
echo Testing dependencies...
echo.

if exist "yt-dlp.exe" (
    echo Testing yt-dlp...
    yt-dlp.exe --version >nul 2>&1
    if %errorlevel% equ 0 (
        echo yt-dlp is working correctly! ✓
    ) else (
        echo yt-dlp may not be working properly! ✗
    )
)

if exist "ffmpeg.exe" (
    echo Testing FFmpeg...
    ffmpeg.exe -version >nul 2>&1
    if %errorlevel% equ 0 (
        echo FFmpeg is working correctly! ✓
    ) else (
        echo FFmpeg may not be working properly! ✗
    )
)

echo.
echo ========================================
echo    Setup Complete!
echo ========================================
echo.
echo Dependencies installed:
if exist "yt-dlp.exe" echo ✓ yt-dlp.exe (standalone - no Python needed)
if exist "ffmpeg.exe" echo ✓ ffmpeg.exe (for perfect video/audio merging)
echo.
echo You can now run the YouTube Downloader!
echo Use: .\run.bat
echo.
pause
