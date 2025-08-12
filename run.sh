#!/bin/bash

echo "YouTube Downloader"
echo "=================="

if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    echo "Please install Java 11 or higher from: https://adoptium.net/"
    exit 1
fi

java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$java_version" -lt 11 ]; then
    echo "Error: Java 11 or higher is required. Current version: $java_version"
    exit 1
fi

if ! command -v yt-dlp &> /dev/null; then
    echo "Warning: yt-dlp is not installed or not in PATH"
    echo "Please install yt-dlp using: pip install yt-dlp"
    echo "Or on macOS: brew install yt-dlp"
    echo "Or on Ubuntu/Debian: sudo apt install yt-dlp"
    echo ""
    read -p "Press Enter to continue anyway..."
fi

if [ ! -f "target/youtube-downloader-1.0.0.jar" ]; then
    echo "Building the application..."
    if ! command -v mvn &> /dev/null; then
        echo "Error: Maven is not installed. Please install Maven first."
        exit 1
    fi
    
    mvn clean package
    if [ $? -ne 0 ]; then
        echo "Error: Failed to build the application"
        exit 1
    fi
fi

echo "Starting YouTube Downloader..."
java -jar target/youtube-downloader-1.0.0.jar
