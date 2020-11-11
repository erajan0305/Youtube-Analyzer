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

/**
 * EmojiAnalyzer Test Class
 */

public class EmojiAnalyzerTest {

    SearchResults searchResults;
    ChannelResultItems channelResultItems;
    YoutubeAnalyzer youtubeAnalyzer;
    CommentResults commentResults;

    /**
     * Initializes the {@link SearchResults} object with data from <code>dataset</code>
     * Initializes the {@link CommentResults} object with data from <code>dataset</code>
     * Initializes the {@link CommentResults} object with data from <code>dataset</code>
     */
    @Before
    public void init() {
        searchResults = DatasetHelper
                .jsonFileToObject(new File("test/dataset/searchresults/Golang.json"), SearchResults.class);
        youtubeAnalyzer = new YoutubeAnalyzer();
        channelResultItems = DatasetHelper
                .jsonFileToObject(new File("test/dataset/channelinformation/Channel_Golang_UC-R1UuxHVDyNoJN0Tn4nkiQ.json"), ChannelResultItems.class);
        commentResults = DatasetHelper.jsonFileToObject(new File("test/dataset/comments/happy_video.json"), CommentResults.class);
    }

    /**
     * This method tests the <code>processCommentStream</code> method and matches the result with the expected result.
     *
     * @author Rajan Shah
     */
    @Test
    public void processCommentsStreamTest0() {
        Stream<String> commentStream = commentResults.items.parallelStream()
                .map(commentResultItem -> commentResultItem.getSnippet().getTopLevelComment().getSnippet().getTextOriginal().trim());
        String happyEmojiUnicode = EmojiManager.getForAlias("heart_eyes").getUnicode();
        String expectedFilteredCommentString = String.join("", Collections.nCopies(5, happyEmojiUnicode));
        Assert.assertEquals(expectedFilteredCommentString, EmojiAnalyzer.processCommentStream(commentStream));
    }

    /**
     * This method tests the <code>processCommentStream</code> method for empty/ comments with no emojis
     * and matches the result with the expected result.
     *
     * @author Rajan Shah
     */
    @Test   // Stream of string without emojis or an empty string return empty string.
    public void processCommentsStreamTest1() {
        Assert.assertEquals("", EmojiAnalyzer.processCommentStream(Stream.of("comments")));
        Assert.assertEquals("", EmojiAnalyzer.processCommentStream(Stream.of("")));
    }

    /**
     * This method tests the <code>encodeEmojiSentiment</code> method for <code>grin</code> emoji
     * and expects to be encoded to <code>happy</code>.
     *
     * @author Rajan Shah
     */
    @Test   //encodes "grin" emoji as "happy"
    public void encodeEmojiSentimentTest0() {
        String grinEmoji = EmojiManager.getForAlias("grin").getUnicode();
        Assert.assertEquals("happy", EmojiAnalyzer.encodeEmojiSentiment(grinEmoji));
    }

    /**
     * This method tests the <code>encodeEmojiSentiment</code> method for <code>pensive</code> emoji
     * and expects to be encoded to <code>sad</code>.
     *
     * @author Rajan Shah
     */
    @Test
    public void encodeEmojiSentimentTest1() {
        String pensiveEmoji = EmojiManager.getForAlias("pensive").getUnicode();
        Assert.assertEquals("sad", EmojiAnalyzer.encodeEmojiSentiment(pensiveEmoji));
    }

    /**
     * This method tests the <code>generateReport</code> method for <code>commentResults</code> for happy comments
     * and expects the sentiment to be <code>happy</code>.
     *
     * @author Rajan Shah
     */
    @Test
    public void generateReportTest0() {
        String grinEmoji = EmojiManager.getForAlias("grin").getUnicode();
        Assert.assertEquals(grinEmoji, EmojiAnalyzer.generateReport(youtubeAnalyzer.getCommentsString(commentResults)));
    }

    /**
     * This method tests the <code>generateReport</code> method for <code>commentResults</code> for sad comments
     * and expects the sentiment to be <code>sad</code>.
     *
     * @author Rajan Shah
     */
    @Test
    public void generateReportTest1() {
        String pensiveEmoji = EmojiManager.getForAlias("pensive").getUnicode();
        commentResults = DatasetHelper.jsonFileToObject(new File("test/dataset/comments/sad_video.json"), CommentResults.class);
        assert commentResults != null;
        Assert.assertEquals(pensiveEmoji, EmojiAnalyzer.generateReport(youtubeAnalyzer.getCommentsString(commentResults)));
    }

    /**
     * This method tests the <code>generateReport</code> method for <code>commentResults</code> for neutral comments
     * and expects the sentiment to be <code>neutral</code>.
     *
     * @author Rajan Shah
     */
    @Test
    public void generateReportTest2() {
        String neutralEmoji = EmojiManager.getForAlias("neutral_face").getUnicode();
        commentResults = DatasetHelper.jsonFileToObject(new File("test/dataset/comments/neutral_video.json"), CommentResults.class);
        assert commentResults != null;
        Assert.assertEquals(neutralEmoji, EmojiAnalyzer.generateReport(youtubeAnalyzer.getCommentsString(commentResults)));
    }

    /**
     * This method tests the <code>generateReport</code> method for <code>commentResults</code> for zero comments
     * and expects the sentiment to be <code>neutral</code>.
     *
     * @author Rajan Shah
     */
    @Test
    public void generateReportTest3() {
        String neutralEmoji = EmojiManager.getForAlias("neutral_face").getUnicode();
        commentResults = DatasetHelper.jsonFileToObject(new File("test/dataset/comments/zero_comments.json"), CommentResults.class);
        assert commentResults != null;
        Assert.assertEquals(neutralEmoji, EmojiAnalyzer.generateReport(youtubeAnalyzer.getCommentsString(commentResults)));
    }

    /**
     * This method destroys every object.
     */
    @After
    public void destroy() {
        searchResults = null;
        youtubeAnalyzer = null;
        channelResultItems = null;
        commentResults = null;
    }
}
