package com.scrape.service;

/*
class TranscriptUseTest {

    private TranscriptGet transcriptGet;

    private TranscriptUse transcriptUse;

    @BeforeEach
    void setUp() {
        transcriptGet = new TranscriptGet();
        transcriptUse = new TranscriptUse();
    }

    @AfterEach
    void tearDown() {
    }

    @DisplayName("Make sure no files have .orig (if this fails, then call the delete orig files method)")
    @Test
    void deleteAllFilesWithDotOrig() {
        for (File fileEntry : transcriptGet.getFiles()) {
            String fileName = fileEntry.getName();
            String ending = fileName.substring(fileName.length() - 7);
            assertEquals(".en.vtt", ending);
        }
    }

    @DisplayName("Find a phrase that ")
    @Nested
    class FindTranscriptPhrases {

        void isThisPhraseAsCommonAsExpected(String phrase, HashMap<String, Integer> titles) {
            HashMap<String, List<String>> transcriptTitlesWithPhrasesFound = transcriptUse.findTranscriptsWithThisPhrase(phrase, 30);

            for(String title : titles.keySet()) {
                int howCommonPhraseActuallyIs = transcriptTitlesWithPhrasesFound.get(title).size();
                if (titles.get(title) != howCommonPhraseActuallyIs) {
                    fail();
                }
            }
        }

        void isThisPhraseInThisManyTitles(String phrase, int amountExpected) {
            HashMap<String, List<String>> transcriptTitlesWithPhrasesFound = transcriptUse.findTranscriptsWithThisPhrase(phrase, 30);
            if (transcriptTitlesWithPhrasesFound.size() != amountExpected) {
                fail();
            }
        }

        void doesThePhraseAppearAtTheseTimestamps(String phrase, List<String> timestamps) {
            HashMap<String, List<String>> transcriptTitlesWithPhrasesFound = transcriptUse.findTranscriptsWithThisPhrase(phrase, 30);
            List<String> originalTimestamps = new ArrayList<>(timestamps);

            for(List<String> lines : transcriptTitlesWithPhrasesFound.values()) {
                for(String line : lines) {
                    String timestamp = line.substring(0, line.length() - new StringBuilder(line).reverse().indexOf(":") - 1);
                    timestamps.remove(timestamp);
                }
            }

            if (!timestamps.isEmpty()) {
                System.out.println("These were the timestamps to be checked: " + originalTimestamps);
                System.out.println("Of these timestamps, these were not found: " + timestamps);
                fail();
            }
        }

        @DisplayName("is in 3 transcripts with 3 occurrences in 1 one of them")
        @Test
        void findTranscriptWithAPhraseThatAppearsMultipleTimes() {
            HashMap<String, Integer> titles = new HashMap<>();

            String phrase = "what is penis music";
            titles.put("100mg of Pog injected right into the Brain (Cook, Serve, Delicious 3) [STTH84ZUQ30]", 3);
            titles.put("Game So Easy I Beat It Twice | A Difficult Game About Climbing [bf3D21kg9dc]", 1);
            titles.put("Minecraft Fartcore with Jennifer Part 1 [lPGr-7tOb2g]", 1);

            isThisPhraseInThisManyTitles(phrase, 3);
            isThisPhraseAsCommonAsExpected(phrase, titles);
        }

        @DisplayName("is in 2 transcripts")
        @Test
        void findTranscriptsWithAPhraseThatAppearsMultipleTimes() {
            HashMap<String, Integer> titles = new HashMap<>();

            String phrase = "getting my ass handed to me";
            titles.put("FISHING GAME OF THE YEAR 2018 & SM64 (Part9) [iIW1CirnLnQ]", 1);
            titles.put("Oh lord (Spore Part 1) [V1OGZJNgQf8]", 1);

            isThisPhraseInThisManyTitles(phrase, 2);
            isThisPhraseAsCommonAsExpected(phrase, titles);
        }

        @DisplayName("is in 1 transcript")
        @Test
        void findTranscriptsWithAPhraseThatAppearsOnce() {
            HashMap<String, Integer> titles = new HashMap<>();

            String phrase = "think uh we don't really have fridge space though dude we don't have fridge space";
            titles.put("He's So Job Pilled | Supermarket Simulator [zppfqfYNNhk]", 1);

            isThisPhraseInThisManyTitles(phrase, 1);
            isThisPhraseAsCommonAsExpected(phrase, titles);
        }

        @DisplayName("also has the correct timestamps")
        @Test
        void findPhraseWithTimestamp() {
            List<String> timestamps = new ArrayList<>();

            String phrase = "what is penis music";
            timestamps.add("00:45:24.559");
            timestamps.add("01:27:47.639");
            timestamps.add("01:27:49.980");
            timestamps.add("01:27:49.980");
            timestamps.add("00:53:16.500");

            doesThePhraseAppearAtTheseTimestamps(phrase, timestamps);
        }
    }

    @DisplayName("Check that ")
    @Nested
    class FindTranscriptTitlesWithFiles {

        private HashMap<String, List<String>> getTranscriptFromCombinedFile;

        @BeforeEach
        void setUp() {
            getTranscriptFromCombinedFile = transcriptGet.getTranscriptFromCombinedFile();
        }

        @AfterEach
        void tearDown() {
            getTranscriptFromCombinedFile = null;
        }

        @DisplayName("the title's conversion ")
        @Nested
        class checkIfTitleConversionIsCorrect {

            @DisplayName(" doesn't change id's underscores to colons")
            @Test
            void checkIfTitlesTimeHasBeenFalselyConverted() {
                String correctTitle = "chiblee streams; E [Super Monkey Ball Banana Mania Part 3] [uO4EFk2_8K4]";

                if (!getTranscriptFromCombinedFile.containsKey(correctTitle)) {
                    fail();
                }
            }

            @DisplayName(" doesn't change colons to underscores in reference to time")
            @Test
            void checkIfTitlesTimeIsCorrect() {
                String correctTitle = "NEW PB FULL RUN (2:04:18) | Mario Party Superstars [CjJRkESXpE0]";
                if (!getTranscriptFromCombinedFile.containsKey(correctTitle)) {
                    fail();
                }
            }

            @DisplayName(" doesn't change colons to underscores in a non-time reference")
            @Test
            void checkIfUnderscoreHasBeenCorrectlyLeftAlone() {
                String correctTitle = "TAke a look, y'all: IMG_4346.jpeg | Stardew Valley [SVP-YhpwvPg]";
                if (!getTranscriptFromCombinedFile.containsKey(correctTitle)) {
                    fail();
                }
            }

            @DisplayName(" does change illegal characters to legal ones")
            @Test
            void checkIfTitleHasBeenConvertedFromIllegalToLegal() {
                String correctTitle = "Playing SPORE For BUG SUNDAY ? | Spore [YE18GbvbU7g]";
                if (!getTranscriptFromCombinedFile.containsKey(correctTitle)) {
                    fail();
                }
            }
        }

        @DisplayName("text doesn't have arrows")
        @Test
        void checkIfTranscriptDoesntHaveArrows() {
            for (List<String> listOfText : getTranscriptFromCombinedFile.values()) {
                if (listOfText.contains(">") || listOfText.contains("<")) {
                    fail();
                }
            }
        }

        @DisplayName("titles don't contain double spaces")
        @Test
        void checkIfTitlestDontContainDoubleSpaces() {
            String doubleSpacing = "  ";

            for (String key : getTranscriptFromCombinedFile.keySet()) {
                if (key.contains(doubleSpacing)) {
                    fail();
                }
            }
        }

        @DisplayName("texts don't contain double spaces")
        @Test
        void checkIfTextDoesntContainDoubleSpacesOrSpacesAtBeginning() {
            String doubleSpacing = "  ";

            for (List<String> listOfText : getTranscriptFromCombinedFile.values()) {
                for(String line : listOfText) {
                    int indexOfSplitComma = line.indexOf(",") + 1;
                    String text = line.substring(indexOfSplitComma);

                    if (!text.isEmpty()) {
                        if (text.contains(doubleSpacing)) {
                            System.out.println("The text line with double spacing: " + text);
                            fail();
                        }

                        if (text.charAt(0) == ' ') {
                            System.out.println("The text line with a single space at the start: " + text);
                            fail();
                        }
                    }
                }
            }
        }
    }

    @Nested
    class FindThisTranscript {

        private BufferedReader bufferedReader;

        @BeforeEach
        void setUp() {
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        }

        @AfterEach
        void tearDown() {
            bufferedReader = null;
        }

        @DisplayName("If the transcript contains as many characters as it should")
        @Test
        void findThisTranscriptsNumberOfCharacters() throws IOException {
            assertEquals(2944, transcriptUse.findThisTranscript("I PB In This Video | Mario Party Superstars", bufferedReader).size());
        }

        @DisplayName("Should throw an exception when entering an invalid transcript name")
        @Test
        void findATranscriptThatDoesntExistAndThrowException() {
            assertThrows(CouldNotFindTranscriptException.class,
                    () -> transcriptUse.findThisTranscript("98210978117406676640363133589312172518657276861265", bufferedReader));
        }

        @DisplayName("Should be able to find the id just by the title")
        @Test
        void findTranscriptIdWithJustName() throws IOException {
            assertTrue(transcriptUse.findThisTranscript("MINECRAFT BUT MY CHAT CONTROLS EVERYTHING", bufferedReader).size() > 1000);
        }

        @DisplayName("Should still work with duplicate titles")
        @Test
        void findTranscriptWithUserInputToo() throws IOException {
            String id = "lj3y1Fx30Qo";
            System.setIn(new ByteArrayInputStream(id.getBytes()));
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            assertTrue(transcriptUse.findThisTranscript("Fortnite with Friends", bufferedReader).size() > 1000);
        }
    }

    //necessary so when you match a yt title to our file title they actually match
    @DisplayName("Check no transcript contains any illegal characters")
    @Test
    void checkIfAnyTitlesHaveIllegalCharacters() {
        List<String> listOfTitlesWithIllegalCharacters = new ArrayList<>();

        for (String title : transcriptGet.getTranscriptFromCombinedFile().keySet()) {

            String regex = "[a-zA-Z0-9 ()|*?.@;+_&$!:'#,=○△□é\uD83D\uDE0D\"\\-/\\[\\]]+";
            if (!title.matches(regex)) {
                listOfTitlesWithIllegalCharacters.add(title);
            }
        }

        if (!listOfTitlesWithIllegalCharacters.isEmpty()) {
            System.out.println("These titles contain illegal characters:\n");
            listOfTitlesWithIllegalCharacters.forEach(System.out::println);

            fail();
        }
    }
}

 */