# YouTube Downloader

A Java-based YouTube video downloader with a modern GUI that supports downloading videos in various qualities including 4K, 1080p, 720p, 480p, and 360p, as well as audio-only formats.

## Features

- **Multiple Quality Options**: Download videos in 4K (2160p), 1080p, 720p, 480p, 360p
- **Audio Formats**: Extract audio in MP3 or M4A format
- **GUI**: Clean and intuitive user interface
- **Progress Tracking**: Real-time download progress with detailed logging
- **Standalone Dependencies**: No need to install Python or system-wide tools
- **Perfect Merging**: Automatic video/audio merging with metadata


## Checkout releases for easier Download

## Quick Start

### 1. Download Dependencies
Run the setup script to automatically download all required dependencies:

```bash
.\setup.bat
```

This will download:
- `yt-dlp.exe` (standalone - no Python needed)
- `ffmpeg.exe` (for perfect video/audio merging)

### 2. Build and Run
```bash
.\run.bat
```

This will:
- Build the application
- Launch the YouTube Downloader GUI

## Manual Setup

If you prefer to download dependencies manually:

### Download yt-dlp (Standalone)
```bash
.\download_ytdlp.bat
```

### Download FFmpeg
```bash
.\download_ffmpeg.bat
```

### Build Only
```bash
.\build.bat
```

## How It Works

The application uses:
- **yt-dlp**: For downloading YouTube videos (standalone executable)
- **FFmpeg**: For merging video and audio streams with metadata
- **Java Swing**: For the user interface
- **Maven**: For building the project

## File Structure

```
youtube-downloader/
├── src/                          # Source code
├── target/                       # Built application
├── yt-dlp.exe                   # Standalone yt-dlp (no Python needed)
├── ffmpeg.exe                   # FFmpeg for video/audio merging
├── mvn.cmd                      # Local Maven (optional)
├── setup.bat                    # Download all dependencies
├── run.bat                      # Build and run application
├── build.bat                    # Build only
├── download_ytdlp.bat           # Download yt-dlp only
└── download_ffmpeg.bat          # Download FFmpeg only
```

## Requirements

- **Java 11 or higher**
- **Windows 10/11** (tested on Windows)
- **Internet connection** (for downloading videos and dependencies)

## Usage

1. **Launch the application**: Run `.\run.bat`
2. **Enter YouTube URL**: Paste any YouTube video URL
3. **Select Quality**: Choose from 4K, 1080p, 720p, 480p, 360p, or audio-only
4. **Choose Output Directory**: Select where to save the downloaded file
5. **Click Download**: Watch the progress and detailed logs

## Quality Options

- **4K (2160p)**: Highest quality available (up to 4K)
- **1080p**: Full HD quality
- **720p**: HD quality
- **480p**: Standard definition
- **360p**: Low definition
- **Audio Only (MP3)**: Extract audio as MP3
- **Audio Only (M4A)**: Extract audio as M4A
- **Best Quality**: Automatically select the best available quality

## Troubleshooting

### "yt-dlp is not installed"
- Run `.\setup.bat` to download dependencies
- Or run `.\download_ytdlp.bat` to download yt-dlp only

### "FFmpeg not found"
- Run `.\setup.bat` to download dependencies
- Or run `.\download_ffmpeg.bat` to download FFmpeg only

### "Java not found"
- Install Java 11 or higher from: https://adoptium.net/

### Download fails or wrong quality
- Check the log output for detailed error messages
- Ensure the YouTube URL is valid and accessible
- Try a different quality option

## Technical Details

### Format Selection
The application uses specific yt-dlp format IDs for reliable quality selection:
- **4K**: `315+140/308+140/299+140/298+140/137+140/136+140/135+140/134+140/18`
- **1080p**: `137+140/136+140/135+140/134+140/18`
- **720p**: `136+140/135+140/134+140/18`
- **480p**: `135+140/134+140/18`
- **360p**: `134+140/18`

### Merging Process
1. Downloads video and audio streams separately
2. Uses FFmpeg to merge them into a single MP4 file
3. Adds metadata (title, description, etc.)
4. Creates a final file with both video and audio

## Development

### Building from Source
```bash
# Clone the repository
git clone https://github.com/up7b8/youtube-downloader.git
cd youtube-downloader

# Download dependencies
.\setup.bat

# Build the project
.\build.bat

# Run the application
java -jar target\youtube-downloader-1.0.0.jar
```

### Project Structure
```
src/main/java/com/youtubedownloader/
├── YouTubeDownloader.java       # Main GUI class
├── DownloadManager.java         # Core download logic
├── DownloadFormat.java          # Quality format definitions
└── DownloadCallback.java        # Progress callback interface
```

## License

This project is for educational purposes. Please respect YouTube's terms of service and copyright laws when downloading content.

## Contributing

Feel free to submit issues and enhancement requests!
