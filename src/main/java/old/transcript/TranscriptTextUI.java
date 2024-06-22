package old.transcript;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class TranscriptTextUI {

    private final TranscriptGet transcriptGet;

    private final TranscriptUse transcriptUse;

    private final String chibleeVodChannelLink = "https://www.youtube.com/@ChibleeVODs";

    public TranscriptTextUI() {
        this.transcriptGet = new TranscriptGet();
        this.transcriptUse = new TranscriptUse();
    }

    private void printLinksForPhrases(HashMap<String, List<String>> phrases) {
        for (String title : phrases.keySet()) {
            for (String timestampAndText : phrases.get(title)) {
                int indexBetweenTimestampAndText = timestampAndText.length() - new StringBuilder(timestampAndText).reverse().indexOf(":") - 1;
                String timestamp = timestampAndText.substring(0, indexBetweenTimestampAndText);
                String text = timestampAndText.substring(indexBetweenTimestampAndText + 2);
                String linkOfVideo = getTheLinkOfThisVideo(title, timestamp);

                System.out.println(title + ":");
                System.out.println("\"" + text + "\"");
                System.out.println(linkOfVideo);
                System.out.println();
            }
        }
    }

    private String getTheLinkOfThisVideo(String title, String timestamp) {
        String id = title.substring(title.length() - 12, title.length() - 1);
        long timeInSeconds = calculateTimestampInSeconds(timestamp);

        return "https://www.youtube.com/watch?v=" + id + "&t=" + timeInSeconds;
    }

    private long calculateTimestampInSeconds(String timestamp) {
        timestamp = timestamp.substring(0, timestamp.length() - 4);

        String durationStr = timestamp.replaceAll("(\\d+):(\\d+):(\\d+)", "PT$1H$2M$3S");
        Duration duration = Duration.parse(durationStr);

        //could -1 the return here if the videos are too late
        return duration.getSeconds();
    }

    public void giveOptions() throws Exception {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("NOTE: This program uses " + chibleeVodChannelLink + " as a source.\n");
        boolean endFlag = false;

        while (!endFlag) {
            System.out.println("Your options are:");
            System.out.println("[combine] Combine all txt files (ADMIN)");
            System.out.println("[phrase] List all the times a phrase has been said (including in titles)");
            System.out.println("[titles] List all transcript titles");
            System.out.println("[ts] List a specific transcript");
            System.out.println("[vod] List the Chiblee VOD channel link");
            System.out.println("[end] Stop the program\n");

            String choice = input.readLine();
            switch (choice) {
                case "end": {
                    endFlag = true;
                    break;
                }
                case "combine": {
                    int totalCombined = transcriptUse.writeTranscriptsToCombinedFile();
                    System.out.println("Combined " + totalCombined + " transcripts");
                    break;
                }
                case "phrase": {
                    System.out.println("What phrase do you want to find?");
                    String phraseWanted = input.readLine();
                    System.out.println("How many characters should the text surrounding the phrase be? (e.g. 40)");
                    int charactersWanted = Integer.parseInt(input.readLine());

                    HashMap<String, List<String>> phrases = transcriptUse.findTranscriptsWithThisPhrase(phraseWanted, charactersWanted);
                    printLinksForPhrases(phrases);
                    break;
                }
                case "titles": {
                    Set<String> titles = transcriptGet.getTranscriptFromCombinedFile().keySet();
                    titles.stream()
                            .map(t -> t.substring(0, t.length() - 14))
                            .forEach(System.out::println);

                    break;
                }
                case "ts": {
                    System.out.println("What is the title of the vod on youtube?");
                    System.out.println("You can get this information from (" + chibleeVodChannelLink + ")");
                    String transcriptWanted = input.readLine();

                    List<String> transcript = transcriptUse.findThisTranscript(transcriptWanted, input);
                    transcript.forEach(System.out::println);
                    break;
                }
                case "vod": {
                    System.out.println(chibleeVodChannelLink);
                    break;
                }
                default: {
                    System.out.println("You chose: none");
                }
            }

            Thread.sleep(100);
            System.out.println();
        }
    }

    public static void main(String[] args) throws Exception {
        TranscriptTextUI tUI = new TranscriptTextUI();
        tUI.giveOptions();
    }
}

