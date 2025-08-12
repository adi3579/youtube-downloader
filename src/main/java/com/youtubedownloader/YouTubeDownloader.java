package com.youtubedownloader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * main app (GUI)
 */
public class YouTubeDownloader extends JFrame {
    
    private JTextField urlField;
    private JTextField outputDirField;
    private JComboBox<String> formatComboBox;
    private JTextArea logArea;
    private JButton downloadButton;
    private JButton browseButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    
    private final DownloadManager downloadManager;
    private final ExecutorService executorService;
    
    public YouTubeDownloader() {
        downloadManager = new DownloadManager();
        executorService = Executors.newFixedThreadPool(2);
        
        initializeUI();
        setupEventHandlers();
    }
    
    private void initializeUI() {
        setTitle("YouTube Downloader");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JPanel inputPanel = createInputPanel();
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        
        JPanel logPanel = createLogPanel();
        mainPanel.add(logPanel, BorderLayout.CENTER);
        
        JPanel statusPanel = createStatusPanel();
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("YouTube URL:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        urlField = new JTextField(30);
        panel.add(urlField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        panel.add(new JLabel("Output Directory:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        outputDirField = new JTextField(System.getProperty("user.home") + File.separator + "Downloads");
        panel.add(outputDirField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0.0;
        browseButton = new JButton("Browse");
        panel.add(browseButton, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0;
        panel.add(new JLabel("Format:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        String[] formats = {"Best Quality (Video + Audio)", "Audio Only (MP3)", "Audio Only (M4A)", "4K (2160p)", "1080p", "720p", "480p", "360p"};
        formatComboBox = new JComboBox<>(formats);
        panel.add(formatComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3; gbc.weightx = 1.0;
        downloadButton = new JButton("Download");
        downloadButton.setBackground(new Color(46, 204, 113));
        downloadButton.setForeground(Color.WHITE);
        downloadButton.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(downloadButton, gbc);
        
        return panel;
    }
    
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Download Log"));
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setBackground(new Color(248, 249, 250));
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(550, 250));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        statusLabel = new JLabel("Ready to download");
        statusLabel.setForeground(new Color(52, 73, 94));
        panel.add(statusLabel, BorderLayout.NORTH);
        
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("0%");
        panel.add(progressBar, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setupEventHandlers() {
        browseButton.addActionListener(e -> browseOutputDirectory());
        
        downloadButton.addActionListener(e -> startDownload());
        
        urlField.addActionListener(e -> startDownload());
    }
    
    private void browseOutputDirectory() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Select Output Directory");
        
        String currentDir = outputDirField.getText();
        if (!currentDir.isEmpty()) {
            fileChooser.setCurrentDirectory(new File(currentDir));
        }
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            outputDirField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    private void startDownload() {
        String url = urlField.getText().trim();
        String outputDir = outputDirField.getText().trim();
        
        if (url.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a YouTube URL", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (outputDir.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an output directory", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!isValidYouTubeUrl(url)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid YouTube URL", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        downloadButton.setEnabled(false);
        downloadButton.setText("Downloading...");
        
        String selectedFormat = (String) formatComboBox.getSelectedItem();
        DownloadFormat format = parseFormat(selectedFormat);
        
        logArea.setText("");
        
        executorService.submit(() -> {
            try {
                downloadManager.download(url, outputDir, format, new DownloadCallback() {
                    @Override
                    public void onProgress(int percentage) {
                        SwingUtilities.invokeLater(() -> {
                            progressBar.setValue(percentage);
                            progressBar.setString(percentage + "%");
                        });
                    }
                    
                    @Override
                    public void onLog(String message) {
                        SwingUtilities.invokeLater(() -> {
                            logArea.append(message + "\n");
                            logArea.setCaretPosition(logArea.getDocument().getLength());
                        });
                    }
                    
                    @Override
                    public void onComplete(String filePath) {
                        SwingUtilities.invokeLater(() -> {
                            statusLabel.setText("Download completed: " + new File(filePath).getName());
                            statusLabel.setForeground(new Color(46, 204, 113));
                            downloadButton.setEnabled(true);
                            downloadButton.setText("Download");
                            progressBar.setValue(100);
                            progressBar.setString("100%");
                            
                            JOptionPane.showMessageDialog(YouTubeDownloader.this, 
                                "Download completed successfully!\nFile: " + new File(filePath).getName(), 
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        });
                    }
                    
                    @Override
                    public void onError(String error) {
                        SwingUtilities.invokeLater(() -> {
                            statusLabel.setText("Download failed: " + error);
                            statusLabel.setForeground(new Color(231, 76, 60));
                            downloadButton.setEnabled(true);
                            downloadButton.setText("Download");
                            progressBar.setValue(0);
                            progressBar.setString("0%");
                            
                            JOptionPane.showMessageDialog(YouTubeDownloader.this, 
                                "Download failed: " + error, 
                                "Error", JOptionPane.ERROR_MESSAGE);
                        });
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Download failed: " + e.getMessage());
                    statusLabel.setForeground(new Color(231, 76, 60));
                    downloadButton.setEnabled(true);
                    downloadButton.setText("Download");
                });
            }
        });
    }
    
    private boolean isValidYouTubeUrl(String url) {
        return url.contains("youtube.com") || url.contains("youtu.be");
    }
    
    private DownloadFormat parseFormat(String formatString) {
        if (formatString.contains("Audio Only (MP3)")) {
            return DownloadFormat.AUDIO_MP3;
        } else if (formatString.contains("Audio Only (M4A)")) {
            return DownloadFormat.AUDIO_M4A;
        } else if (formatString.contains("4K") || formatString.contains("2160p")) {
            return DownloadFormat.VIDEO_4K;
        } else if (formatString.contains("1080p")) {
            return DownloadFormat.VIDEO_1080P;
        } else if (formatString.contains("720p")) {
            return DownloadFormat.VIDEO_720P;
        } else if (formatString.contains("480p")) {
            return DownloadFormat.VIDEO_480P;
        } else if (formatString.contains("360p")) {
            return DownloadFormat.VIDEO_360P;
        } else {
            return DownloadFormat.BEST_QUALITY;
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            YouTubeDownloader app = new YouTubeDownloader();
            app.setVisible(true);
        });
    }
}
