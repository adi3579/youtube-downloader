package com.youtubedownloader;

/**
 * Callback interface for download progress and completion events
 */
public interface DownloadCallback {
    
    /**
     * Called when download progress updates
     * @param percentage Progress percentage (0-100)
     */
    void onProgress(int percentage);
    
    /**
     * Called when a log message is available
     * @param message The log message
     */
    void onLog(String message);
    
    /**
     * Called when download completes successfully
     * @param filePath Path to the downloaded file
     */
    void onComplete(String filePath);
    
    /**
     * Called when download fails
     * @param error Error message
     */
    void onError(String error);
}
