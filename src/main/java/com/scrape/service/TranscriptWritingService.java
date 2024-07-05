package com.scrape.service;

import com.scrape.model.Transcript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class TranscriptWritingService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private TranscriptService transcriptService;

    private TranscriptTxtParsingService transcriptTxtParsingService;

    @Autowired
    public TranscriptWritingService(TranscriptService transcriptService, TranscriptTxtParsingService transcriptTxtParsingService) {
        this.transcriptService = transcriptService;
        this.transcriptTxtParsingService = transcriptTxtParsingService;
    }

    public void writeTranscriptsToDatabase() {
        log.info("Writing transcripts to the database...");

        transcriptService.deleteAll();
        HashMap<String, LinkedHashMap<String, String>> transcripts = transcriptTxtParsingService.getTranscriptFromEachFile();

        int transcriptCount = 0;
        for (String titleAndId : transcripts.keySet()) {
            String title = titleAndId.substring(0, titleAndId.length() - 14);
            String id = titleAndId.substring(titleAndId.length() - 12, titleAndId.length() - 1);

            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> transcript : transcripts.get(titleAndId).entrySet()) {
                String timestamp = transcript.getKey();
                String text = transcript.getValue();

                sb.append(timestamp)
                        // Random characters to be used to split the timestamp and text.
                        // Must not be something that is ever said.
                        .append(text)
                        .append("#hg");
            }

            String timestampsAndText = sb.toString().trim();
            timestampsAndText = replaceMusicAndExplicitFromText(timestampsAndText);

            Transcript transcript = new Transcript(id, title, timestampsAndText);
            transcriptService.save(transcript);

            transcriptCount++;
        }

        log.info("Successfully wrote {} transcripts to the database", transcriptCount);
    }

    // Same method used when writing to the inverted index.
    // We are using it so the phrases can match (they wouldn't match with [Music] instead of MusicAnnotation)
    private String replaceMusicAndExplicitFromText(String text) {
        text = text.replace("[Music]", "MusicAnnotation");
        text = text.replace("[&nbsp;__&nbsp;]", "ExplicitAnnotation");

        return text;
    }
}
