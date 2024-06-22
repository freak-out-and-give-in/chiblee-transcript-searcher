package old.transcript;

import com.scrape.exception.CouldNotFindTranscriptException;
import com.scrape.exception.CouldNotFindTimestampException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class TranscriptUse {

    private final TranscriptGet transcriptGet;

    public TranscriptUse() {
        this.transcriptGet = new TranscriptGet();
    }

    private void deleteStorageFile(String fileName) {
        File file = new File(transcriptGet.getStoragePathWithFileName(fileName));
        file.delete();
    }

    private void deleteTranscript(String fileName) {
        File file = new File(transcriptGet.getTranscriptPathWithFileName(fileName));
        file.delete();
    }

    //this only needs to be done immediately after downloading all the txt files using yt-dlp
    //this is because there are duplicate downloads
    private String deleteAllFilesWithDotOrig() {
        int totalChecked = 0;
        int totalDeleted = 0;

        for (File fileEntry : transcriptGet.getFiles()) {
            String fileName = fileEntry.getName();
            String ending = fileName.substring(fileName.length() - 7);
            if (!ending.equals(".en.vtt")) {
                //deletes all files with .orig (seemingly duplicates)
                deleteTranscript(fileName);
                totalDeleted++;
            }

            totalChecked++;
        }

        return "Deleted: " + totalDeleted + " / " + totalChecked;
    }

    public int writeTranscriptsToCombinedFile() throws IOException {
        System.out.println(deleteAllFilesWithDotOrig());

        //if compiled transcript exists, delete it
        String compiledTranscript = transcriptGet.getCompiledTranscriptName();
        deleteStorageFile(compiledTranscript);

        List<List<String>> transcript = transcriptGet.getTranscriptFromEachFile();

        int totalCombined = 0;
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(transcriptGet.getStoragePathWithFileName(compiledTranscript)));

        for (List<String> file : transcript) {
            totalCombined++;
            for (String line : file) {
                bufferedWriter.write(line + "\n");
            }
        }

        return totalCombined;
    }

    private List<List<String>> findTimeStampOfPhrase(StringBuilder stringWithText, String title, String phrase, int lineNumber, int keyNumber, int textLength,
                                                     List<String> hashMapKeys, List<String> hashMapValues) {
        HashMap<String, List<String>> transcript = transcriptGet.getTranscriptWithoutLineBreaks();
        LinkedHashMap<String, String> timestampsAndText = new LinkedHashMap<>();

        //initialise timestampsAndText with values
        for (int i = 0; i < hashMapKeys.size(); i++) {
            timestampsAndText.put(hashMapKeys.get(i), hashMapValues.get(i));
        }

        if (stringWithText.isEmpty()) {
            stringWithText = new StringBuilder();
        }

        for (int i = lineNumber; i < transcript.get(title).size(); i++) {
            String line = transcript.get(title).get(i);
            //skip the first line - the title
            if (line.charAt(0) == ',') {
                continue;
            }

            String timestamp = line.substring(0, line.indexOf(","));
            String text = line.substring(line.indexOf(",") + 1) + " ";

            stringWithText.append(text);
            timestampsAndText.put(timestamp, text);

            if (stringWithText.toString().contains(phrase)) {
                int startIndexOfPhrase = stringWithText.indexOf(phrase);
                int endIndexOfPhrase = stringWithText.indexOf(phrase) + phrase.length();

                //adds up each string to see if the total size is same, so then we know where it begins and so the timestamp
                List<String> timestampKeys = new ArrayList<>(timestampsAndText.keySet());
                for (int k = keyNumber; k < timestampKeys.size(); k++) {
                    String timestampKey = timestampKeys.get(k);
                    textLength += timestampsAndText.get(timestampKey).length();

                    //k is really low for some reason, maybe for the 2nd/3rd phrase of the 100mg title we'll be able to find out why,
                    //because in those 2 the timestamp is empty. how the timestamp can return empty i don't know

                    if (textLength + text.length() + 1 >= startIndexOfPhrase) {
                        stringWithText = new StringBuilder(stringWithText.substring(0, startIndexOfPhrase) + stringWithText.substring(endIndexOfPhrase));

                        List<String> returningString = new ArrayList<>();
                        returningString.add(timestampKey);
                        returningString.add(stringWithText.toString());
                        returningString.add(String.valueOf(i));
                        returningString.add(String.valueOf(textLength));
                        returningString.add(String.valueOf(k));

                        List<String> keys = timestampsAndText.keySet().stream().toList();
                        List<String> values = timestampsAndText.values().stream().toList();

                        return List.of(returningString, keys, values);
                    }
                }
            }
        }

        throw new CouldNotFindTimestampException("Could not find the timestamp.");
    }

    public HashMap<String, List<String>> findTranscriptsWithThisPhrase(String phrase, int amountOfCharactersEachSide) {
        HashMap<String, String> transcript = transcriptGet.getTranscriptAsOneLineWithoutLineBreaksAndTimestamps();
        HashMap<String, List<String>> phrasesWithTitles = new HashMap<>();
        int maxNumberOfPhrasesFound = 100;

        for (String title : transcript.keySet()) {
            String file = transcript.get(title);
            if (file.contains(phrase)) {
                phrasesWithTitles.put(title, new ArrayList<>());
            }

            StringBuilder stringWithText = new StringBuilder();
            int lineNumber = 0;
            int textLength = 0;
            int keyNumber = 0;
            List<String> hashMapKeys = new ArrayList<>();
            List<String> hashMapValues = new ArrayList<>();
            while (file.contains(phrase)) {
                List<List<String>> findTimestamp = findTimeStampOfPhrase
                        (stringWithText, title, phrase, lineNumber, keyNumber, textLength, hashMapKeys, hashMapValues);

                String timestamp = findTimestamp.getFirst().getFirst();
                stringWithText = new StringBuilder(findTimestamp.getFirst().get(1));
                lineNumber = Integer.parseInt(findTimestamp.getFirst().get(2)) + 1;
                textLength = Integer.parseInt(findTimestamp.getFirst().get(3));
                keyNumber = Integer.parseInt(findTimestamp.getFirst().get(4));
                hashMapKeys = findTimestamp.get(1);
                hashMapValues = findTimestamp.get(2);

                int indexOfPhrase = file.indexOf(phrase);
                String phraseFound = cropPhraseAndSurroundingText(file, phrase, amountOfCharactersEachSide);
                phrasesWithTitles.get(title).add(timestamp + ": " + phraseFound);
                int phraseLength = phrase.length();

                //if before and after the phrase to be removed is a space, then remove the latter space. example of this:
                //phrase = "hello im doing great today", file = "fantastic yes hello im doing great today with my packaging"
                //without this, the file is -> "fantastic yes  with my packaging", with an extra space!
                //only an issue though if phrases are clumped together and a subsequent phrase captures the double-spacing
                //and is then shown to the user as a phrase found with the extra space
                if (indexOfPhrase > 0 && file.charAt(indexOfPhrase - 1) == ' ' && file.charAt(indexOfPhrase + phrase.length()) == ' ') {
                    phraseLength++;
                }

                file = file.substring(0, indexOfPhrase) + file.substring(indexOfPhrase + phraseLength);
            }

            if (phrasesWithTitles.size() > maxNumberOfPhrasesFound) {
                break;
            }
        }

        return phrasesWithTitles;
    }

    private String cropPhraseAndSurroundingText(String file, String phrase, int amountOfCharactersEachSide) {
        if (amountOfCharactersEachSide > 200) {
            amountOfCharactersEachSide = 200;
        }
        if (amountOfCharactersEachSide < 0) {
            amountOfCharactersEachSide = 0;
        }

        int upperAmountOfCharactersEachSide = amountOfCharactersEachSide;
        int lowerAmountOfCharactersEachSide = amountOfCharactersEachSide;
        int indexOfPhraseBeginning = file.indexOf(phrase);
        int indexOfPhraseEnd = indexOfPhraseBeginning + phrase.length();

        //if the phrase is early on in the transcript and the amount of characters
        //on the lower side goes out of bounds,
        //then make the bound only go as far as it can without going out of bounds
        if (indexOfPhraseBeginning - amountOfCharactersEachSide < 0) {
            lowerAmountOfCharactersEachSide = indexOfPhraseBeginning;
        }

        //if phrase upper bounds goes out of bounds at the end of the file,
        //then make that bound as high as it can go without going out of bounds (file length - last char of phrase)
        if (indexOfPhraseEnd + upperAmountOfCharactersEachSide > file.length()) {
            upperAmountOfCharactersEachSide = file.length() - indexOfPhraseEnd;
        }

        return file.substring(indexOfPhraseBeginning - lowerAmountOfCharactersEachSide, indexOfPhraseEnd +
                upperAmountOfCharactersEachSide);
    }

    //this is a method that is used right after the method to crop the phrase and surrounding text,
    //and is used in the method to find a phrase
    //not it use because it's likely the phrases people put in will be full words, we don't need to worry about the opposite.
    /*
    private String cropToClosestWordsOnBothSides(String file, String phrase) {
        int firstSpace = phrase.indexOf(" ") + 1;

        //this means that if the phrase is at the beginning, the first word isn't cut off
        if (file.startsWith(phrase)) {
            firstSpace = 0;
        }

        int lastSpace = phrase.length() - new StringBuilder(phrase).reverse().indexOf(" ") - 1;

        if (firstSpace < lastSpace) {
            phrase = phrase.substring(firstSpace, lastSpace);
        }

        return phrase;
    }
     */

    public String findThisTitlesNameAndId(HashMap<String, List<String>> transcript, String inputTitle) {
        for (String key : transcript.keySet()) {
            String titleWithoutId = key.substring(0, key.length() - 14);

            if (titleWithoutId.equals(inputTitle)) {
                return key;
            }
        }

        return "";
    }

    private boolean isThisTitleOnlyUsedOnce(HashMap<String, List<String>> transcript, String inputTitleNoId) {
        long count = transcript.keySet().stream()
                .map(title -> title.substring(0, title.length() - 14))
                .filter(title -> title.equals(inputTitleNoId))
                .count();

        return count < 2;
    }

    private boolean doesThisTitleExist(HashMap<String, List<String>> transcript, String inputTitleNoId) {
        List<String> listOfTitlesWithoutId = transcript.keySet()
                .stream()
                .map(title -> title.substring(0, title.length() - 14))
                .toList();

        return listOfTitlesWithoutId.contains(inputTitleNoId);
    }

    private String askUserForVideoId(String inputTitle, BufferedReader bufferedReader) throws IOException {
        System.out.println("There is more than one video that has this exact title, so we'll need some more information.");
        System.out.println("What is the ID of the youtube video?");
        System.out.println("TIP: For example, for this video: https://www.youtube.com/watch?v=uZcO3k0kNDk, the id would be 'uZcO3k0kNDk'");
        String id = bufferedReader.readLine();
        System.out.println();

        String idRegex = ".{11}";
        while (!id.matches(idRegex)) {
            System.out.println("The id should be 11 characters long. Please try again.");
            id = bufferedReader.readLine();
        }

        inputTitle += " [" + id + "]";

        return inputTitle;
    }


    //input parameter is the yt video title without an id
    public List<String> findThisTranscript(String inputTitle, BufferedReader bufferedReader) throws IOException {
        HashMap<String, List<String>> titlesAndFiles = transcriptGet.getTranscriptFromCombinedFile();

        if (!doesThisTitleExist(titlesAndFiles, inputTitle)) {
            throw new CouldNotFindTranscriptException("Could not find the transcript");
        }

        if (isThisTitleOnlyUsedOnce(titlesAndFiles, inputTitle)) {
            inputTitle = findThisTitlesNameAndId(titlesAndFiles, inputTitle);
        } else {
            inputTitle = askUserForVideoId(inputTitle, bufferedReader);
        }

        return titlesAndFiles.get(inputTitle);
    }
}
