package com.scrape.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class FileParsingService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final String transcriptsPath = "C:\\Users\\James\\OneDrive\\Documents\\folder\\chiblee videos\\transcripts-dlp";

    public String getTranscriptPathWithFileName(String fileName) {
        return transcriptsPath + "\\" + fileName;
    }

    @Autowired
    public FileParsingService() {
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

    //reads through every file and writes to a temporary list that is returned (doesn't write to anywhere)
    //this is only to combine transcripts, it shouldn't be used in any other circumstance as it's extremely inefficient
    public List<List<String>> getTranscriptFromEachFile() {
        log.debug("Getting every transcript that's been converted to our readable format");
        File folder = new File(transcriptsPath);
        List<List<String>> transcript = new ArrayList<>();

        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (!fileEntry.isDirectory()) {
                transcript.add(getSpecificTranscriptFromFile(fileEntry.getName()));
            }
        }

        return transcript;
    }

    //contains title and text - among everything else of course like [Music]
    //also has the normal line breaks - like the line breaks the compiled transcript has
    private List<String> getSpecificTranscriptFromFile(String fileNameWithIdAndExtensions) {
        log.debug("Parsing the transcript with the filename {} so it is a csv and ordered with title, timestamps and text", fileNameWithIdAndExtensions);
        List<String> timestampsAndLines = new ArrayList<>();

        //for finding the file, as the file name contains the weird vertical bar
        //fileNameWithIdAndExtensions = fileNameWithIdAndExtensions.replace("|", "｜");
        String title = fileNameWithIdAndExtensions.substring(0, fileNameWithIdAndExtensions.length() - 7);

        //some symbols are converted weirdly in the file title name, so we're converting them back for the text title name inside the file.
        title = convertIllegalFileNameCharactersBackToNormal(title);
        title = convertLegalFileNameCharactersBackToNormal(title);
        //getting rid of potential phantom spaces in title
        title = convertDoubleSpacesToSingleSpace(title);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(getTranscriptPathWithFileName(fileNameWithIdAndExtensions)))) {
            timestampsAndLines.add("," + title);

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

                //if it's appeared in the last 2 lines
                if (text.equals(lastText[0]) && text.equals(lastText[1])) {
                    timestampsAndLines.remove(timestampsAndLines.getLast());
                    lastText[0] = null;
                    lastText[1] = null;
                } else {
                    timestampsAndLines.add(timestamp + "," + text);
                    lastText[0] = lastText[1];
                    lastText[1] = text;
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return timestampsAndLines;
    }

    public List<String> findTextBetweenArrowsAndTimestamp(String text) {
        log.debug("Returning only the text between arrows");
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

    //there are illegal symbols that cant be used in file names, such changes are from yt-dlp.
    //we're switching them back so file titles and yt titles match
    private String convertIllegalFileNameCharactersBackToNormal(String fileNameWithIdWithoutExtensions) {
        log.debug("Converting the illegal file name characters back to their much more common variant with {}", fileNameWithIdWithoutExtensions);
        fileNameWithIdWithoutExtensions = fileNameWithIdWithoutExtensions
                .replace("｜", "|")
                .replace("＂", "\"")
                .replace("？", "?")
                .replace("＊", "*")
                .replace("⧸", "/")
                .replace("：", ":");

        return fileNameWithIdWithoutExtensions;
    }

    //some legal characters from titles got changed, not to illegal ones but to other legal characters.
    //this happened with time, e.g. 11:49 -> 11_49
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

    //for titles
    private String convertDoubleSpacesToSingleSpace(String title) {
        log.debug("Converting double spaces to single spaces with the title {}", title);
        return title.replace("  ", " ");
    }

    //for text
    private String convertSinglePhantomSpacingToExplicit(String text) {
        log.debug("Converting single phantom spaces to explicit text, with {}", text);
        if (text.charAt(0) == ' ') {
            String explicitWithSpace = "[&nbsp;__&nbsp;] ";
            text = text.substring(1);
            text = explicitWithSpace + text;
        }

        return text;
    }
}
