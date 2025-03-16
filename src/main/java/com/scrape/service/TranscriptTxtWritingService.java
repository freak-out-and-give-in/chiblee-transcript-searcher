package com.scrape.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class TranscriptTxtWritingService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private TranscriptTxtParsingService transcriptTxtParsingService;

    @Autowired
    public TranscriptTxtWritingService(TranscriptTxtParsingService transcriptTxtParsingService) {
        this.transcriptTxtParsingService = transcriptTxtParsingService;
    }

    public void downloadTranscripts() {
        log.info("Downloading transcripts");

        // Writing the transcripts should ALWAYS occur before updating the archive.
        // A downside is that if this fails during execution for whatever reason,
        // then the archive will not be accurate, and we'll have to start-a-new.

        writeTranscripts();
        deleteAllFilesWithDotOrig();
        populateOrUpdateArchive();
    }

    // Writes the transcripts to file
    private void writeTranscripts() {
        log.info("Writing transcript files");

        runCommandPrompt("yt-dlp \"https://www.youtube.com/@ChibleeVODs/videos\" --write-auto-sub --sub-lang \"en.*\" --skip-download --download-archive archive.txt");
    }

    // Populates or updates the archive.
    // The archive stores which transcripts have been downloaded,
    // so we don't have to download all transcripts every time
    private void populateOrUpdateArchive() {
        log.info("Populating or updating the transcript archive");

        runCommandPrompt("yt-dlp --force-write-archive --simulate --flat-playlist --download-archive archive.txt \"https://www.youtube.com/@ChibleeVODs/videos\"");
    }

    // This only needs to be done immediately after downloading all the txt files using yt-dlp
    // This is because there are duplicate downloads
    private void deleteAllFilesWithDotOrig() {
        log.info("Deleting all files ending in .orig ...");

        int totalChecked = 0;
        int totalDeleted = 0;

        for (File fileEntry : transcriptTxtParsingService.getIndividualTranscriptFiles()) {
            String fileName = fileEntry.getName();
            String ending = fileName.substring(fileName.length() - 7);
            if (!ending.equals(".en.vtt")) {
                // Deletes all files with .orig (seemingly duplicates)
                deleteTranscriptFile(fileName);
                totalDeleted++;
            }

            totalChecked++;
        }

        log.info("Checked {} files and deleted {} of them that ended in .orig", totalChecked, totalDeleted);
    }

    private void runCommandPrompt(String command) {
        log.info("Running the command in command prompt: {} ...", command);

        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", "cd \"C:\\Users\\James\\OneDrive\\Documents\\folder\\chiblee videos\\transcripts-tldr\" && " + command);
        builder.redirectErrorStream(true);
        Process process;

        try {
            process = builder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        while (true) {
            try {
                line = r.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (line == null) { break; }
            log.info("Cmd prompt: {}", line);
        }

        log.info("The command has finished.");
    }

    private void deleteTranscriptFile(String fileName) {
        log.trace("Deleting the transcript {}", fileName);

        File file = new File(transcriptTxtParsingService.getTranscriptPathWithFileName(fileName));
        file.delete();
    }
}
