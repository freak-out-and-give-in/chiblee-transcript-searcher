package com.scrape.service;

import com.scrape.dto.TranscriptDto;
import com.scrape.exception.InvalidIdException;
import com.scrape.exception.InvalidTitleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

@Service
public class TranscriptDtoService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private TranscriptService transcriptService;

    @Autowired
    public TranscriptDtoService(TranscriptService transcriptService) {
        this.transcriptService = transcriptService;
    }

    public TranscriptDto getTranscriptIdTimestampsAndTextByTitle(String title) {
        validateInputForTitle(title);

        int transcriptsSize = transcriptService.getByTitle(title).size();
        if (transcriptsSize == 1) {
            String videoId = transcriptService.getVideoIdByTitle(title);
            LinkedHashMap<Integer, String> timestampsAndText = transcriptService.makeMapOfTimestampsAndText(videoId);

            return new TranscriptDto(videoId, timestampsAndText);
        }

        // transcriptsSize >= 2, aka not unique title,
        // so we need to ask the user for the video's id
        throw new InvalidIdException("There are multiple videos with this title. Please also enter the video's id");
    }

    public TranscriptDto getTranscriptIdTimestampsAndTextByVideoId(String videoId) {
        if (videoId.length() != 11) {
            throw new InvalidIdException("The id should be exactly 11 characters long");
        }

        return new TranscriptDto(videoId, transcriptService.makeMapOfTimestampsAndText(videoId));
    }

    private void validateInputForTitle(String title) {
        if (title.isEmpty()) {
            throw new InvalidTitleException("The title should not be empty");
        }

        if (title.length() > 120) {
            throw new InvalidTitleException("The title should be less than 121 characters long");
        }
    }
}
