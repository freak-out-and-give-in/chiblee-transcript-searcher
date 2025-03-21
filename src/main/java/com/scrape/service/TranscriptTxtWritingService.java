package com.scrape.service;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class TranscriptTxtWritingService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private TranscriptTxtParsingService transcriptTxtParsingService;

    private Base base;

    @Autowired
    public TranscriptTxtWritingService(TranscriptTxtParsingService transcriptTxtParsingService) {
        this.transcriptTxtParsingService = transcriptTxtParsingService;
        base = new Base();
    }

    public void downloadTranscripts() {
        log.info("Downloading transcripts");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        downloadTxtTranscripts();

        log.info("Finished downloading the transcripts, it took: {}", stopWatch);
    }

    public void deleteAllTranscripts() {
        log.info("Deleting all transcripts...");

        int totalDeleted = 0;

        for (File fileEntry : transcriptTxtParsingService.getIndividualTranscriptFiles()) {
            String fileName = fileEntry.getName();
            deleteTranscript(fileName);

            totalDeleted++;
        }

        log.info("Deleted {} transcripts", totalDeleted);
    }

    public void clearArchiveFiles() {
        log.info("Clearing the archive...");

        PrintWriter writer;
        try {
            writer = new PrintWriter(base.getArchiveFile());
            writer.print("");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        writer.close();

        log.info("Successfully cleared the archive");
    }

    // Writes the txt transcripts to a folder
    private void downloadTxtTranscripts() {
        log.info("Downloading the txt transcripts");

        // The -P with a directory tells the application where to store the txt transcript downloads
        runCommandPrompt("yt-dlp --force-write-archive --download-archive " + base.getArchiveFileName() + " " +
                "-P \"" + base.getTranscriptsPath() + "\" \"https://www.youtube.com/@ChibleeVODs/videos\" " +
                "--write-auto-sub --sub-lang \"en\" --skip-download --sleep-requests 1.25 --sleep-interval 3");
    }

    private void runCommandPrompt(String command) {
        log.info("Running the command in command prompt: {}", command);

        // This path should be wherever yt-dlp.exe is stored
        String ytDlpPath = base.getConfigPath();

        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", "cd \"" + ytDlpPath + "\" && " + command);
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

            if (line == null) {
                break;
            }

            log.info("Command prompt: {}", line);
        }

        log.info("The command has finished");
    }

    private void deleteTranscript(String fileName) {
        log.debug("Deleting the transcript {}", fileName);

        File file = new File(base.getTranscriptPathWithFileName(fileName));
        file.delete();
    }
}
