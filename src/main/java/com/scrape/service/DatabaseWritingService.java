package com.scrape.service;

import com.scrape.model.TimestampsAndText;
import com.scrape.model.Transcript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DatabaseWritingService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private TranscriptService transcriptService;

    private TimestampsAndTextService timestampsAndTextService;

    private FileParsingService fileParsingService;

    @Autowired
    public DatabaseWritingService(TranscriptService transcriptService, TimestampsAndTextService timestampsAndTextService, FileParsingService fileParsingService) {
        this.transcriptService = transcriptService;
        this.timestampsAndTextService = timestampsAndTextService;
        this.fileParsingService = fileParsingService;
    }

    public void writeTranscriptsToDatabase() {
        log.info("Starting to clear the database and write new transcripts.");
        // First, clear the database, so we don't have duplicate data
        transcriptService.deleteAllTranscripts();

        List<List<String>> entireTranscript = fileParsingService.getTranscriptFromEachFile();

        int totalCombined = 0;
        for (List<String> file : entireTranscript) {
            Transcript transcript = new Transcript();

            for (int i = 0; i < file.size(); i++) {
                String line = file.get(i);

                // If it's the first line, which contains: '[title] [id]'
                if (i == 0) {
                    // Removing , from start
                    line = line.substring(1);
                    int lineLength = line.length();

                    String title = line.substring(0, lineLength - 14);
                    String id = line.substring(lineLength - 12, lineLength - 1);

                    transcript.setTitle(title);
                    transcript.setId(id);
                } else {
                    String[] timestampsAndTextArray = line.split(",");

                    TimestampsAndText timestampsAndText = new TimestampsAndText();
                    timestampsAndText.setTimestamp(LocalTime.parse(timestampsAndTextArray[0], DateTimeFormatter.ofPattern("HH:mm:ss.SSS")));
                    timestampsAndText.setText(timestampsAndTextArray[1]);
                    timestampsAndText.setTranscript(transcript);

                    timestampsAndTextService.saveTimestampsAndText(timestampsAndText);
                }
            }

            transcriptService.saveTranscript(transcript);
            totalCombined++;
        }

        log.info("Wrote {} transcripts to the database.", totalCombined);
    }
}
