package com.youtubedownloader;

// currently quality is only 480p, 720p, 1080p, 4k
public enum DownloadFormat {
    BEST_QUALITY,    // Best video + audio quality
    AUDIO_MP3,       // Audio only in MP3 format
    AUDIO_M4A,       // Audio only in M4A format
    VIDEO_4K,        // Video at 4K (2160p) resolution
    VIDEO_1080P,     // Video at 1080p resolution
    VIDEO_720P,      // Video at 720p resolution
    VIDEO_480P,      // Video at 480p resolution
    VIDEO_360P       // Video at 360p resolution
}
