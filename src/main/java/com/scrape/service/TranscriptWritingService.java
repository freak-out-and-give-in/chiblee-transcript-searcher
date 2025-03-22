package com.scrape.service;

import com.scrape.model.Transcript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    public void addTranscriptsToDatabase() {
        log.info("Writing transcripts to the database...");

        // titleAndId, <timestamps,text>
        HashMap<String, LinkedHashMap<String, String>> transcripts = transcriptTxtParsingService.getTranscriptFromEachFile();

        int transcriptCount = 0;
        for (String titleAndId : transcripts.keySet()) {
            String title = titleAndId.substring(0, titleAndId.length() - 14);
            String videoId = titleAndId.substring(titleAndId.length() - 12, titleAndId.length() - 1);

            // If a transcript with the same videoId already exists in the database
            if (transcriptService.doesThisVideoIdExist(videoId)) {
                Transcript transcript = transcriptService.getByVideoId(videoId);

                // If the videoId exists and is the same but the title is different, then delete the current transcript,
                // because then they have probably updated the video since
                if (!transcript.getTitle().equals(title)) {
                    transcriptService.deleteTranscript(transcript);
                } else { // Else, if the videoId and title are the same, then do not add it as it is already stored
                    continue;
                }
            }

            LinkedHashMap<String, String> transcriptTimestampsAndText = transcripts.get(titleAndId);
            String timestampsAndText = buildTimestampsAndTextString(transcriptTimestampsAndText);
            timestampsAndText = replaceMusicAndExplicitFromText(timestampsAndText);

            Transcript transcript = new Transcript(videoId, title, timestampsAndText);
            transcriptService.addOrUpdate(transcript);

            transcriptCount++;
        }

        log.info("Successfully added {} transcripts to the database", transcriptCount);
    }

    private String buildTimestampsAndTextString(LinkedHashMap<String, String> transcriptTimestampsAndText) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> transcript : transcriptTimestampsAndText.entrySet()) {
            String timestamp = transcript.getKey();
            String text = transcript.getValue();

            sb.append(timestamp)
                    // Random characters to be used to split the timestamp and text.
                    // Must not be something that is ever said.
                    .append(text)
                    .append("#hg");
        }

        return sb.toString().trim();
    }

    // Same method used when writing to the inverted index.
    // We are using it so the phrases can match (they wouldn't match with [Music] instead of MusicAnnotation)
    private String replaceMusicAndExplicitFromText(String text) {
        text = text.replace("[Music]", "MusicAnnotation");
        text = text.replace("[&nbsp;__&nbsp;]", "ExplicitAnnotation");

        return text;
    }

}
