package com.scrape.service;

import com.scrape.model.TimestampsAndText;
import com.scrape.model.Transcript;
import com.scrape.repository.TimestampsAndTextRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TimestampsAndTextService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private TimestampsAndTextRepository timestampsAndTextRepository;

    @Autowired
    public TimestampsAndTextService(TimestampsAndTextRepository timestampsAndTextRepository) {
        this.timestampsAndTextRepository = timestampsAndTextRepository;
    }

    public TimestampsAndText getTimestampsAndText(String id) {
        log.debug("Getting the timestampsAndText with the id {}", id);
        return timestampsAndTextRepository.findById(id).get();
    }

    public void saveTimestampsAndText(TimestampsAndText timestampsAndText) {
        log.debug("Saving timestampsAndText with the id {}", timestampsAndText.getId());
        timestampsAndTextRepository.save(timestampsAndText);
    }
}
