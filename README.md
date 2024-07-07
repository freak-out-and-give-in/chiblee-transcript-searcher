The live-website of this application is here: WIP.

This is is a transcript searcher that searches the YouTube channel Of Chiblee - https://www.youtube.com/@ChibleeVODs.
It can find a phrase that Chiblee has said, or show an interactable list of words he said in a particular video.


# How it works
1. All of the transcripts are downloaded using yt-dlp, and stored as txt files.
2. The txt files are then parsed and separated into separate lines, with it's timestamp and it's text.
3. This parsing is used for creating 2 tables in a database: an Inverted index and one called 'Transcript'.
4. The Inverted index stores each word with a list of: ids of every video it's appeared in, along with the timestamp of when it was said in those videos.
   Certain techniques are used here like tokenisation, lemmatisation, and removing stop-words.
6. The Transcript table simply stores all the text and timestamps in all videos as a single string.
7. Then these 2 tables data are looped over and key information is returned.

The phrase finder uses the Inverted index to very quickly find the occurences of a phrase, and the Transcript is used for showing the context of the phrase (which appers below the video).
The 'individual' finder, which gives an interactable transcript for a certain video, works by giving the video's unique title or id to the Transcript table - which returns all the timestamps and text.

![screenshot-of-search](https://github.com/freak-out-and-give-in/chiblee-transcript-searcher/assets/137592545/5a62a834-b5cc-4ee2-b2a9-99015ab0d8d8)
