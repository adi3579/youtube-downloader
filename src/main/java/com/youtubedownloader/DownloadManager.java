package com.youtubedownloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class DownloadManager {
    
    private static final Logger logger = LoggerFactory.getLogger(DownloadManager.class);
    private final AtomicBoolean isDownloading = new AtomicBoolean(false);
    
    public void download(String url, String outputDir, DownloadFormat format, DownloadCallback callback) {
        if (isDownloading.get()) {
            callback.onError("Download already in progress");
            return;
        }
        
        isDownloading.set(true);
        
        try {
            if (!isYtDlpInstalled()) {
                callback.onError("yt-dlp is not installed. Please install it first.");
                return;
            }
            
            if (isFfmpegInstalled()) {
                callback.onLog("FFmpeg found - will use for proper video/audio merging");
            } else {
                callback.onLog("FFmpeg not found - using yt-dlp's built-in merging");
            }
            
            File outputDirectory = new File(outputDir);
            if (!outputDirectory.exists()) {
                if (!outputDirectory.mkdirs()) {
                    callback.onError("Failed to create output directory: " + outputDir);
                    return;
                }
            }
            
            List<String> command = buildYtDlpCommand(url, outputDir, format);
            
            callback.onLog("Selected format: " + format.name());
            callback.onLog("Starting download with command: " + String.join(" ", command));
            
            if (format != DownloadFormat.AUDIO_MP3 && format != DownloadFormat.AUDIO_M4A) {
                command.add("--prefer-free-formats");
            }
            
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            
            Process process = processBuilder.start();
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                int lastProgress = 0;
                
                while ((line = reader.readLine()) != null) {
                    callback.onLog(line);
                    
                    if (line.contains("Downloading format") || line.contains("has already been downloaded") || 
                        line.contains("Format") || line.contains("Selected")) {
                        callback.onLog("*** FORMAT INFO: " + line + " ***");
                    }
                    
                    if (line.contains("p") && (line.contains("720") || line.contains("1080") || line.contains("2160") || line.contains("360") || line.contains("480"))) {
                        callback.onLog("*** RESOLUTION INFO: " + line + " ***");
                    }
                    
                    if (line.contains("Merging") || line.contains("Converting") || line.contains("Post-processing") || line.contains("FFmpeg")) {
                        callback.onLog("*** MERGE INFO: " + line + " ***");
                    }
                    
                    int progress = parseProgress(line);
                    if (progress > lastProgress) {
                        callback.onProgress(progress);
                        lastProgress = progress;
                    }
                }
            }
            
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                String downloadedFile = findDownloadedFile(outputDir, url);
                if (downloadedFile != null) {
                    callback.onComplete(downloadedFile);
                } else {
                    callback.onError("Download completed but file not found");
                }
            } else {
                callback.onError("Download failed with exit code: " + exitCode);
            }
            
        } catch (Exception e) {
            logger.error("Download error", e);
            callback.onError("Download error: " + e.getMessage());
        } finally {
            isDownloading.set(false);
        }
    }
    
    private boolean isYtDlpInstalled() {
        try {
            String localYtDlp = System.getProperty("user.dir") + File.separator + "yt-dlp.exe";
            File localYtDlpFile = new File(localYtDlp);
            
            if (localYtDlpFile.exists()) {
                ProcessBuilder processBuilder = new ProcessBuilder(localYtDlp, "--version");
                Process process = processBuilder.start();
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    return true;
                }
            }
            
            ProcessBuilder processBuilder = new ProcessBuilder("py", "-m", "yt_dlp", "--version");
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return true;
            }
            
            processBuilder = new ProcessBuilder("yt-dlp", "--version");
            process = processBuilder.start();
            exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean isFfmpegInstalled() {
        try {
            String localFfmpeg = System.getProperty("user.dir") + File.separator + "ffmpeg.exe";
            File localFfmpegFile = new File(localFfmpeg);
            
            if (localFfmpegFile.exists()) {
                ProcessBuilder processBuilder = new ProcessBuilder(localFfmpeg, "-version");
                Process process = processBuilder.start();
                int exitCode = process.waitFor();
                return exitCode == 0;
            }
            
            ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg", "-version");
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    private List<String> buildYtDlpCommand(String url, String outputDir, DownloadFormat format) {
        List<String> command = new ArrayList<>();
        
        String localYtDlp = System.getProperty("user.dir") + File.separator + "yt-dlp.exe";
        File localYtDlpFile = new File(localYtDlp);
        
        if (localYtDlpFile.exists()) {
            command.add(localYtDlp);
        } else {
            command.add("py");
            command.add("-m");
            command.add("yt_dlp");
        }
        
        switch (format) {
            case AUDIO_MP3:
                command.add("-x"); 
                command.add("--audio-format");
                command.add("mp3");
                command.add("--audio-quality");
                command.add("0");
                break;
                
            case AUDIO_M4A:
                command.add("-x"); 
                command.add("--audio-format");
                command.add("m4a");
                command.add("--audio-quality");
                command.add("0"); 
                break;
                
            case VIDEO_4K:
                command.add("-f");
                command.add("315+140/308+140/299+140/298+140/137+140/136+140/135+140/134+140/18");
                break;
                
            case VIDEO_1080P:
                command.add("-f");
                command.add("137+140/136+140/135+140/134+140/18");
                break;
                
            case VIDEO_720P:
                command.add("-f");
                command.add("136+140/135+140/134+140/18");
                break;
                
            case VIDEO_480P:
                command.add("-f");
                command.add("135+140/134+140/18");
                break;
                
            case VIDEO_360P:
                command.add("-f");
                command.add("134+140/18");
                break;
                
            case BEST_QUALITY:
            default:
                command.add("-f");
                command.add("137+140/136+140/135+140/134+140/18"); 
                break;
        }
        
        command.add("-o");
        command.add(outputDir + File.separator + "%(title)s.%(ext)s");
        
        command.add("--no-playlist"); // Don't download playlists
        command.add("--no-warnings"); // Reduce output noise
        command.add("--progress"); // Show progress
        
        command.add("--force-overwrites");
        command.add("--no-part");
        
        command.add("--merge-output-format");
        command.add("mp4"); 

        if (isFfmpegInstalled()) {
            command.add("--embed-metadata"); // Embed metadata
            command.add("--add-metadata"); // Add metadata
        }
        
        command.add("--concurrent-fragments");
        command.add("4"); 
        command.add("--buffer-size");
        command.add("1024"); 
        
        command.add(url);
        
        return command;
    }
    
    private int parseProgress(String line) {
        try {
            if (line.contains("%")) {
                int percentIndex = line.indexOf("%");
                int startIndex = percentIndex - 1;
                
=                while (startIndex >= 0 && Character.isDigit(line.charAt(startIndex))) {
                    startIndex--;
                }
                startIndex++;
                
                if (startIndex < percentIndex) {
                    String percentStr = line.substring(startIndex, percentIndex);
                    return Integer.parseInt(percentStr);
                }
            }
        } catch (Exception e) {
        }
        return -1;
    }
    
    private String findDownloadedFile(String outputDir, String url) {
        try {
            File directory = new File(outputDir);
            File[] files = directory.listFiles();
            
            if (files != null) {
                File latestFile = null;
                long latestTime = 0;
                
                for (File file : files) {
                    if (file.isFile() && file.lastModified() > latestTime) {
                        latestFile = file;
                        latestTime = file.lastModified();
                    }
                }
                
                if (latestFile != null) {
                    return latestFile.getAbsolutePath();
                }
            }
        } catch (Exception e) {
            logger.error("Error finding downloaded file", e);
        }
        
        return null;
    }
    
    public boolean isDownloading() {
        return isDownloading.get();
    }
    
    public void cancelDownload() {
        isDownloading.set(false);
    }
}
