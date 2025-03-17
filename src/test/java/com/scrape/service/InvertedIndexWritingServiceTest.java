package com.scrape.service;

import com.scrape.model.InvertedIndex;
import com.scrape.model.Transcript;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvertedIndexWritingServiceTest {

    @InjectMocks
    private InvertedIndexWritingService invertedIndexWritingService;

    @Mock
    private InvertedIndexService invertedIndexService;

    @Mock
    private TranscriptTxtParsingService transcriptTxtParsingService;

    @BeforeEach
    void setUp() {
        HashMap<String, LinkedHashMap<String, String>> transcripts = getStringLinkedHashMapHashMap();

        when(transcriptTxtParsingService.getTranscriptFromEachFile()).thenReturn(transcripts);
        when(transcriptTxtParsingService.getStopWords()).thenReturn(List.of("the", "a", "to", "and"));
    }

    @Test
    void writeInvertedIndexesToDatabase() {
        invertedIndexWritingService.writeInvertedIndexesToDatabase();

        ArgumentCaptor<InvertedIndex> captor = ArgumentCaptor.forClass(InvertedIndex.class);
        verify(invertedIndexService, times(6)).save(captor.capture());
    }

    @Test
    void buildInvertedIndex() {
        // TitleAndId, Timestamp, List(Text)
        HashMap<String, HashMap<String, List<String>>> invertedIndex = invertedIndexWritingService.buildInvertedIndex();

        HashMap<String, HashMap<String, List<String>>> invertedIndexTest = new HashMap<>();
        invertedIndexTest.put("love", new HashMap<>(Map.of("tle [21340d]", List.of("134", "654"))));

        System.out.println(invertedIndexTest);

        assertThat("{love={tle [21340d=[00:13:30, 02:55:34]}, wound={tle [21340d=[02:55:34]}, machina={tle [21340d=[04:14:52]}, stand={tle [21340d=[00:13:30]}," +
                " inside={tle [21340d=[00:13:30]}, you={tle [21340d=[00:13:30]}}").isEqualTo(invertedIndex + "");

        verify(invertedIndexService, times(1)).deleteAll();
    }

    private static @NotNull HashMap<String, LinkedHashMap<String, String>> getStringLinkedHashMapHashMap() {
        HashMap<String, LinkedHashMap<String, String>> transcripts = new HashMap<>();

        LinkedHashMap<String, String> timestampsAndText = new LinkedHashMap<>();
        timestampsAndText.put("00:12:34", "punching through your skin");
        timestampsAndText.put("01:55:31", "dont know what to do");
        transcripts.put("new title [21340d]", timestampsAndText);

        LinkedHashMap<String, String> timestampsAndText2 = new LinkedHashMap<>();
        timestampsAndText2.put("00:13:30", "stand inside your love");
        timestampsAndText2.put("02:55:34", "wound love");
        timestampsAndText2.put("04:14:52", "machina");
        transcripts.put("new title [21340d]", timestampsAndText2);

        return transcripts;
    }
}