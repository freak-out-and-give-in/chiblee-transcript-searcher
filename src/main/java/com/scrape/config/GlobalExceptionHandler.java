package com.scrape.config;

import com.scrape.exception.*;
import org.scrape.demo.Exceptions.RateLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({InvalidPhraseException.class})
    public ResponseEntity<Object> handleInvalidPhraseException(InvalidPhraseException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler({InvalidInvertedIndexException.class})
    public ResponseEntity<Object> handleInvalidInvertedIndexException(InvalidInvertedIndexException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler({InvalidTranscriptException.class})
    public ResponseEntity<Object> handleInvalidTranscriptException(InvalidTranscriptException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler({InvalidTitleException.class})
    public ResponseEntity<Object> handleInvalidTitleException(InvalidTitleException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler({InvalidIdException.class})
    public ResponseEntity<Object> handleInvalidIdException(InvalidIdException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler({InvalidWordCountException.class})
    public ResponseEntity<Object> handleInvalidWordCountException(InvalidWordCountException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity<Object> handleNoSuchElementException(NoSuchElementException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler({UnauthorizedAccessException.class})
    public ResponseEntity<Object> handleUnauthorizedAccessException(UnauthorizedAccessException exception) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(exception.getMessage());
    }

    @ExceptionHandler({RateLimitExceededException.class})
    public ResponseEntity<Object> handleRateLimitExceededException(RateLimitExceededException exception) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(exception.getMessage());
    }
}
