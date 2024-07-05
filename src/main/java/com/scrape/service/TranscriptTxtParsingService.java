package com.scrape.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class TranscriptTxtParsingService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final String transcriptsPath = "C:\\Users\\James\\OneDrive\\Documents\\folder\\chiblee videos\\transcripts-dlp";

    public String getTranscriptPathWithFileName(String fileName) {
        return transcriptsPath + "\\" + fileName;
    }

    @Autowired
    public TranscriptTxtParsingService() {
    }

    public List<String> getStopWords() {
        List<String> listOfStopWords = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader("src/main/java/com/scrape/util/stopwords.txt"))) {
            String line = br.readLine();

            while (line != null) {
                listOfStopWords.add(line);
                line = br.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return listOfStopWords;
    }

    public List<File> getIndividualTranscriptFiles() {
        log.debug("Getting every individual transcript file");

        List<File> fileList = new ArrayList<>();
        File folder = new File(transcriptsPath);

        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (!fileEntry.isDirectory()) {
                fileList.add(fileEntry);
            }
        }

        return fileList;
    }

    // Reads through every file and writes to a temporary list that is returned (doesn't write to anywhere)
    // This is only to combine transcripts, it shouldn't be used in any other circumstance as it's extremely inefficient
    public HashMap<String, LinkedHashMap<String, String>> getTranscriptFromEachFile() {
        log.debug("Getting every parsed transcript");
        HashMap<String, LinkedHashMap<String, String>> transcripts = new HashMap<>();

        for (File file : getIndividualTranscriptFiles()) {
            HashMap<String, LinkedHashMap<String, String>> transcript = getSpecificTranscriptFromFile(file.getName());
            String titleAndIdKey = transcript.keySet().stream().findFirst().get();

            transcripts.put(titleAndIdKey, transcript.get(titleAndIdKey));
        }

        return transcripts;
    }

    // Contains title and text - among everything else of course like [Music]
    // Also has the normal line breaks - like the line breaks the compiled transcript has
    private HashMap<String, LinkedHashMap<String, String>> getSpecificTranscriptFromFile(String fileNameWithTitleIdAndExtensions) {
        log.debug("Parsing the transcript with the filename {}, so it is a csv and ordered with title, timestamps and text", fileNameWithTitleIdAndExtensions);

        // For finding the file, as the file name contains the weird vertical bar
        // FileNameWithIdAndExtensions = fileNameWithIdAndExtensions.replace("|", "｜");
        String titleAndId = fileNameWithTitleIdAndExtensions.substring(0, fileNameWithTitleIdAndExtensions.length() - 7);

        // Some symbols are converted weirdly in the file title name, so we're converting them back for the text title name inside the file.
        titleAndId = convertIllegalFileNameCharactersBackToNormal(titleAndId);
        titleAndId = convertLegalFileNameCharactersBackToNormal(titleAndId);
        // Gets rid of any potential phantom spaces in title
        titleAndId = convertDoubleSpacesToSingleSpace(titleAndId);

        LinkedHashMap<String, String> timestampsAndText = new LinkedHashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(getTranscriptPathWithFileName(fileNameWithTitleIdAndExtensions)))) {
            String line;
            String arrow = " --> ";
            String timestamp = "";
            String[] lastText = new String[2];
            while ((line = bufferedReader.readLine()) != null) {
                if (line.equals(" ") || line.equals("WEBVTT") || line.equals("Kind: captions") || line.equals("Language: en") || line.isEmpty()) {
                    continue;
                }

                List<String> lineAndTimestamp = findTextBetweenArrowsAndTimestamp(line);
                line = lineAndTimestamp.get(0);
                if (lineAndTimestamp.size() >= 2) {
                    timestamp = lineAndTimestamp.get(1);
                }

                String text = "";
                line = line.replace(" align:start position:0%", "");
                if (line.contains(arrow)) {
                    timestamp = line.substring(0, line.indexOf(arrow));
                } else {
                    text = line;
                    text = convertSinglePhantomSpacingToExplicit(text);
                }

                if (text.isEmpty()) {
                    continue;
                }

                // If it's appeared in the last 2 lines
                if (text.equals(lastText[0]) && text.equals(lastText[1])) {
                    // Removes the last added pair
                    List<String> list = new ArrayList<>(timestampsAndText.keySet());
                    timestampsAndText.remove(list.getLast());

                    lastText[0] = null;
                    lastText[1] = null;
                } else {
                    timestampsAndText.put(timestamp, text);
                    lastText[0] = lastText[1];
                    lastText[1] = text;
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        HashMap<String, LinkedHashMap<String, String>> mapOfTitleWithLines = new HashMap<>();
        mapOfTitleWithLines.put(titleAndId, timestampsAndText);

        return mapOfTitleWithLines;
    }

    public List<String> findTextBetweenArrowsAndTimestamp(String text) {
        log.trace("Returning only the text between arrows of {}", text);

        if (text == null) {
            return Collections.singletonList(text);
        }

        if (!text.contains("<") || !text.contains(">")) {
            return Collections.singletonList(text);
        }

        boolean timestampFlag = true;
        String timestamp = "";
        while (text.contains("<") || text.contains(">")) {
            int indexLeft = text.indexOf("<");
            int indexRight = text.indexOf(">");

            if (timestampFlag) {
                timestamp = text.substring(indexLeft + 1, indexRight);
                timestampFlag = false;
            }

            text = text.substring(0, indexLeft) + text.substring(indexRight + 1);
        }

        return List.of(text, timestamp);
    }

    // There are illegal symbols that cant be used in file names, such changes are from yt-dlp.
    // We're switching them back so file titles and yt titles match
    private String convertIllegalFileNameCharactersBackToNormal(String fileNameWithIdWithoutExtensions) {
        log.trace("Converting the illegal file name characters back to their much more common variant with {}", fileNameWithIdWithoutExtensions);

        fileNameWithIdWithoutExtensions = fileNameWithIdWithoutExtensions
                .replace("｜", "|")
                .replace("＂", "\"")
                .replace("？", "?")
                .replace("＊", "*")
                .replace("⧸", "/")
                .replace("：", ":");

        return fileNameWithIdWithoutExtensions;
    }

    // Some legal characters from titles got changed, not to illegal ones but to other legal characters.
    // This happened with time, e.g. 11:49 -> 11_49
    private String convertLegalFileNameCharactersBackToNormal(String fileName) {
        log.debug("Converting legal file name characters back to their actual character with {}", fileName);
        //file name has id but no extensions

        //this is so any underscore in the id with numbers either side isn't also converted, we'll put it back in later
        String id = fileName.substring(fileName.length() - 13);
        fileName = fileName.substring(0, fileName.length() - 14);

        if (fileName.contains("_")) {
            int indexOfUnderscore = fileName.indexOf("_");
            String charBeforeUnderScore = String.valueOf(fileName.charAt(indexOfUnderscore - 1));
            String charAfterUnderScore = String.valueOf(fileName.charAt(indexOfUnderscore + 1));

            while (charBeforeUnderScore.matches("[0-9]") & charAfterUnderScore.matches("[0-9]")) {
                fileName = fileName.substring(0, indexOfUnderscore)
                        + ":" + fileName.substring(indexOfUnderscore + 1);

                indexOfUnderscore = fileName.indexOf("_");
                if (indexOfUnderscore >= 0) {
                    charBeforeUnderScore = String.valueOf(fileName.charAt(indexOfUnderscore - 1));
                    charAfterUnderScore = String.valueOf(fileName.charAt(indexOfUnderscore + 1));
                } else {
                    charBeforeUnderScore = "a";
                    charAfterUnderScore = "a";
                }
            }
        }

        fileName = fileName + " " + id;
        return fileName;
    }

    // For titles
    private String convertDoubleSpacesToSingleSpace(String title) {
        log.trace("Converting double spaces to single spaces with the title {}", title);

        return title.replace("  ", " ");
    }

    // For text
    private String convertSinglePhantomSpacingToExplicit(String text) {
        log.trace("Converting single phantom spaces to explicit text, with {}", text);

        if (text.charAt(0) == ' ') {
            String explicitWithSpace = "[&nbsp;__&nbsp;] ";
            text = text.substring(1);
            text = explicitWithSpace + text;
        }

        return text;
    }
}
