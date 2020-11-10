package models;

import com.vdurmont.emoji.EmojiManager;
import dataset.DatasetHelper;
import models.Helper.EmojiAnalyzer;
import models.Helper.YoutubeAnalyzer;
import models.POJO.Channel.ChannelResultItems;
import models.POJO.Comments.CommentResults;
import models.POJO.SearchResults.SearchResults;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.stream.Stream;

public class EmojiAnalyzerTest {

    SearchResults searchResults;
    ChannelResultItems channelResultItems;
    YoutubeAnalyzer youtubeAnalyzer;
    CommentResults commentResults;

    @Before
    public void init() {
        searchResults = DatasetHelper
                .jsonFileToObject(new File("test/dataset/searchresults/Golang.json"), SearchResults.class);
        youtubeAnalyzer = new YoutubeAnalyzer();
        channelResultItems = DatasetHelper
                .jsonFileToObject(new File("test/dataset/channelinformation/Channel_Golang_UC-R1UuxHVDyNoJN0Tn4nkiQ.json"), ChannelResultItems.class);
        commentResults = DatasetHelper.jsonFileToObject(new File("test/dataset/comments/happy_video.json"), CommentResults.class);
    }

    @Test
    public void processCommentsStreamTest0() {
        Stream<String> commentStream = commentResults.items.parallelStream()
                .map(commentResultItem -> commentResultItem.getSnippet().getTopLevelComment().getSnippet().getTextOriginal().trim());
        String happyEmojiUnicode = EmojiManager.getForAlias("heart_eyes").getUnicode();
        String expectedFilteredCommentString = String.join("", Collections.nCopies(5, happyEmojiUnicode));
        Assert.assertEquals(expectedFilteredCommentString, EmojiAnalyzer.processCommentStream(commentStream));
    }

    @Test   // Stream of string without emojis or an empty string return empty string.
    public void processCommentsStreamTest1() {
        Assert.assertEquals("", EmojiAnalyzer.processCommentStream(Stream.of("comments")));
        Assert.assertEquals("", EmojiAnalyzer.processCommentStream(Stream.of("")));
    }

    @Test   //encodes "grin" emoji as "happy"
    public void encodeEmojiSentimentTest0() {
        String grinEmoji = EmojiManager.getForAlias("grin").getUnicode();
        Assert.assertEquals("happy", EmojiAnalyzer.encodeEmojiSentiment(grinEmoji));
    }

    @Test   //encodes "pensive" emoji as "sad"
    public void encodeEmojiSentimentTest1() {
        String pensiveEmoji = EmojiManager.getForAlias("pensive").getUnicode();
        Assert.assertEquals("sad", EmojiAnalyzer.encodeEmojiSentiment(pensiveEmoji));
    }

    @Test   //encodes "family_woman_woman_boy_boy" emoji as "neutral"
    public void encodeEmojiSentimentTest2() {
        String familyEmoji = EmojiManager.getForAlias("family_woman_woman_boy_boy").getUnicode();
        Assert.assertEquals("neutral", EmojiAnalyzer.encodeEmojiSentiment(familyEmoji));
    }

    @Test
    public void generateReportTest0() {
        String grinEmoji = EmojiManager.getForAlias("grin").getUnicode();
        Assert.assertEquals(grinEmoji, EmojiAnalyzer.generateReport(youtubeAnalyzer.getCommentsString(commentResults)));
    }

    @Test
    public void generateReportTest1() {
        String pensiveEmoji = EmojiManager.getForAlias("pensive").getUnicode();
        commentResults = DatasetHelper.jsonFileToObject(new File("test/dataset/comments/sad_video.json"), CommentResults.class);
        assert commentResults != null;
        Assert.assertEquals(pensiveEmoji, EmojiAnalyzer.generateReport(youtubeAnalyzer.getCommentsString(commentResults)));
    }

    @Test
    public void generateReportTest2() {
        String neutralEmoji = EmojiManager.getForAlias("neutral_face").getUnicode();
        commentResults = DatasetHelper.jsonFileToObject(new File("test/dataset/comments/neutral_video.json"), CommentResults.class);
        assert commentResults != null;
        Assert.assertEquals(neutralEmoji, EmojiAnalyzer.generateReport(youtubeAnalyzer.getCommentsString(commentResults)));
    }

    @Test
    public void generateReportTest3() {
        String neutralEmoji = EmojiManager.getForAlias("neutral_face").getUnicode();
        commentResults = DatasetHelper.jsonFileToObject(new File("test/dataset/comments/zero_comments.json"), CommentResults.class);
        assert commentResults != null;
        Assert.assertEquals(neutralEmoji, EmojiAnalyzer.generateReport(youtubeAnalyzer.getCommentsString(commentResults)));
    }

    @After
    public void destroy() {
        searchResults = null;
        youtubeAnalyzer = null;
        channelResultItems = null;
        commentResults = null;
    }
}
