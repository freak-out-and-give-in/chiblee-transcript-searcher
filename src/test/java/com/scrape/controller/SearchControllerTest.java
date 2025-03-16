package com.scrape.controller;

import com.scrape.model.InvertedIndex;
import com.scrape.model.Transcript;
import com.scrape.repository.InvertedIndexRepository;
import com.scrape.repository.TranscriptRepository;
import com.scrape.service.InvertedIndexParsingService;
import com.scrape.service.InvertedIndexService;
import com.scrape.service.TranscriptParsingService;
import com.scrape.service.TranscriptService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class SearchControllerTest {

    @Container
    static final MySQLContainer<?> databaseContainer = new MySQLContainer<>("mysql:latest");

    @DynamicPropertySource
    static void mySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", databaseContainer::getJdbcUrl);
        registry.add("spring.datasource.username", databaseContainer::getUsername);
        registry.add("spring.datasource.password", databaseContainer::getPassword);
    }

    private MockMvc mockMvc;

    @Autowired
    private InvertedIndexParsingService invertedIndexParsingService;

    @Autowired
    private InvertedIndexService invertedIndexService;

    @Autowired
    private InvertedIndexRepository invertedIndexRepository;

    @Autowired
    private TranscriptParsingService transcriptParsingService;

    @Autowired
    private TranscriptService transcriptService;

    @Autowired
    private TranscriptRepository transcriptRepository;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        invertedIndexRepository.save(new InvertedIndex("im", "{xKmzwClut54=[01:18:12.400], hHNXDjYc3Ig=[00:37:43.440], " +
                "1X6fMcCug_E=[00:47:42.540, 02:48:15.180], _MKCxj0t7J4=[00:11:34.079, 00:11:39.240, 00:14:33.480, 00:16:28.139, 00:16:44.339]}"));
        invertedIndexRepository.save(new InvertedIndex("the", "{xKmzwClut54=[01:18:12.400], hHNXDjYc3Ig=[00:37:43.440], " +
                "1X6fMcCug_E=[00:47:42.540, 02:48:15.180], _MKCxj0t7J4=[00:11:34.079, 00:11:39.240, 00:14:33.480, 00:16:28.139, 00:16:44.339]}"));
        invertedIndexRepository.save(new InvertedIndex("joker", "{xKmzwClut54=[01:18:12.400], hHNXDjYc3Ig=[00:37:43.440], " +
                "1X6fMcCug_E=[00:47:42.540, 02:48:15.180], _MKCxj0t7J4=[00:11:34.079, 00:11:39.240, 00:14:33.480, 00:16:28.139, 00:16:44.339]}"));
        invertedIndexRepository.save(new InvertedIndex("baby", "{xKmzwClut54=[01:18:12.400], hHNXDjYc3Ig=[00:37:43.440], " +
                "1X6fMcCug_E=[00:47:42.540, 02:48:15.180], _MKCxj0t7J4=[00:11:34.079, 00:11:39.240, 00:14:33.480, 00:16:28.139, 00:16:44.339]}"));

        transcriptRepository.save(new Transcript("xKmzwClut54", "Super Luigi 64 Races (SM64 Part 19)",
                "00:00:00.399we dont know just where our bones will#hg" +
                        "00:00:03.100rest to dust i guess#hg" +
                        "01:18:12.400im baby joker the#hg"));
        transcriptRepository.save(new Transcript("hHNXDjYc3Ig", "title of this video!",
                "00:00:12.500and i dont even crae to shake these#hg" +
                        "00:37:43.440joker hey words baby i the im not done yet#hg"));
        transcriptRepository.save(new Transcript("1X6fMcCug_E", "title of this video2!",
                "00:00:04.000billy corgan pumpkins head#hg" +
                        "00:25:00.000cant you see theres no one around#hg" +
                        "00:47:42.540jump across the joker baby vacant and the bored i am the#hg" +
                        "02:48:15.180just do never knew the joker rules baby i am hang down the#hg" +
                        "03:11:01.923freaks and fools are you better than you thinking to see#hg"));
        transcriptRepository.save(new Transcript("_MKCxj0t7J4", "1979",
                "00:11:34.079baby i am the joker#hg" +
                        "00:11:39.240baby i am dog gold the joker#hg" +
                        "00:14:33.480baby i am the joker the great#hg" +
                        "00:16:28.139baby bass i am the joker#hg" +
                        "00:16:44.339time baby i am the joker#hg"));
    }

    @AfterEach
    void tearDown() {
        mockMvc = null;

        invertedIndexRepository.deleteAll();
        invertedIndexRepository.flush();

        transcriptRepository.deleteAll();
        transcriptRepository.flush();
    }

    @Test
    void findPhrase() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/findPhrase")
                        .param("phrase", "im the joker baby")
                        .param("wordCount", "15"))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();

        Assertions.assertEquals("{\"idAndTimestamps\":" +
                "{\"xKmzwClut54\":[4692]," +
                "\"hHNXDjYc3Ig\":[2263]," +
                "\"1X6fMcCug_E\":[2862,10095]," +
                "\"_MKCxj0t7J4\":[694,699,873,988,1004]}," +
                "\"context\":[\"im baby joker the\",\"joker hey words baby i the im not done yet\",\"jump across " +
                "the joker baby vacant and the bored i am the\",\"just do never knew the joker rules baby i am hang " +
                "down the\",\"baby i am the joker baby i am dog gold the joker\",\"baby i am the joker baby i am " +
                "dog gold the joker\",\"baby i am the joker the great\",\"baby bass i am the joker time baby i am " +
                "the joker\",\"baby bass i am the joker time baby i am the joker\"]}", response);
    }

    @Test
    void findTranscriptByTitle() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/findTranscriptByTitle")
                        .param("title", "title of this video2!"))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();

        Assertions.assertEquals("{\"id" +
                "\":\"1X6fMcCug_E\"," +
                "\"timestampsAndText\":{" +
                "\"4\":\"billy corgan pumpkins head\"," +
                "\"1500\":\"cant you see theres no one around\"," +
                "\"2862\":\"jump across the joker baby vacant and the bored i am the\"," +
                "\"10095\":\"just do never knew the joker rules baby i am hang down the\"," +
                "\"11461\":\"freaks and fools are you better than you thinking to see\"}}", response);
    }

    @Test
    void findTranscriptByVideoId() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/findTranscriptByVideoId")
                        .param("videoId", "hHNXDjYc3Ig"))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();

        Assertions.assertEquals("{\"id\":\"hHNXDjYc3Ig\"," +
                "\"timestampsAndText\":{" +
                "\"12\":\"and i dont even crae to shake these\"," +
                "\"2263\":\"joker hey words baby i the im not done yet\"}}", response);
    }
}