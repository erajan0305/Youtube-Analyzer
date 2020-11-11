package models;

import com.vdurmont.emoji.EmojiManager;
import dataset.DatasetHelper;
import models.Helper.YouTubeApiClient;
import models.Helper.YoutubeAnalyzer;
import models.POJO.Channel.ChannelResultItems;
import models.POJO.Comments.CommentResults;
import models.POJO.SearchResults.SearchResults;
import models.POJO.VideoSearch.Videos;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test cases for the YoutubeAnalyzer class.
 *
 * @author Umang J Patel
 */
public class YoutubeAnalyzerTest {
    SearchResults searchResults;
    ChannelResultItems channelResultItems;
    LinkedHashMap<String, SearchResults> searchResultsLinkedHashMap;
    YoutubeAnalyzer youtubeAnalyzer;
    YouTubeApiClient youTubeApiClient;
    CommentResults commentResults;
    Videos videos;

    /**
     * Initializes the {@link SearchResults} object with data from <code>dataset</code>
     * Initializes the {@link CommentResults} object with data from <code>dataset</code>
     * Initializes the {@link ChannelResultItems} object with data from <code>dataset</code>
     * Initializes the {@link YoutubeAnalyzer} object
     * Initializes the caching mechanism
     */
    @Before
    public void init() {
        searchResults = DatasetHelper.jsonFileToObject(new File("test/dataset/searchresults/Golang.json"), SearchResults.class);
        searchResultsLinkedHashMap = new LinkedHashMap<String, SearchResults>() {
            {
                put("golang", searchResults);
            }
        };
        youtubeAnalyzer = new YoutubeAnalyzer();
        channelResultItems = DatasetHelper.jsonFileToObject(new File("test/dataset/channelinformation/Channel_Golang_UC-R1UuxHVDyNoJN0Tn4nkiQ.json"), ChannelResultItems.class);
        commentResults = DatasetHelper.jsonFileToObject(new File("test/dataset/comments/happy_video.json"), CommentResults.class);
    }

    /**
     * This method tests the <code>getSimilarityStats</code> method of <code>YoutubeAnalyzer</code> class.
     * It checks whether the similarity stats are not null as well as not empty
     *
     * @author Umang J Patel
     */
    @Test
    public void getSimilarityStatsTest() {
        Map<String, Long> golangSimilarityStats = youtubeAnalyzer.getSimilarityStats(searchResultsLinkedHashMap, "golang");
        Assert.assertNotNull(golangSimilarityStats);
        Assert.assertTrue(golangSimilarityStats.size() > 0);
    }

    /**
     * This method tests the <code>getSimilarityStats</code> method of <code>YoutubeAnalyzer</code> class.
     * It checks for the absence of similarity stats for a particular keyword.
     *
     * @author Umang J Patel
     */
    @Test
    public void getSimilarityStatsForSearchWordNotPresentTest() {
        String keyword = "hello world";
        Map<String, Long> golangSimilarityStats = youtubeAnalyzer.getSimilarityStats(searchResultsLinkedHashMap, keyword);
        Map<String, Long> similarityStatsNotFoundMap = new HashMap<String, Long>() {{
            put(keyword, (long) 0);
        }};
        Assert.assertEquals(similarityStatsNotFoundMap.get(keyword), golangSimilarityStats.get(keyword));
    }

    /**
     * This method tests the <code>getSentimentPerVideo</code> method of <code>YoutubeAnalyzer</code> class.
     * It checks whether a sentiment (happy, sad or neutral) is returned for a stream of comments when a particular videoId is provided.
     *
     * @author Umang J Patel
     */
    @Test
    public void getSentimentByVideoIdTest() throws ExecutionException, InterruptedException {
        youTubeApiClient = mock(YouTubeApiClient.class);
        youtubeAnalyzer.setYouTubeApiClient(youTubeApiClient);
        String videoId = "abcXyz";
        when(youTubeApiClient.getSentimentByVideoId(anyString())).thenReturn(CompletableFuture.supplyAsync(() -> commentResults));
        String sentimentPerVideo = youtubeAnalyzer.getSentimentPerVideo(videoId).toCompletableFuture().get();
        Assert.assertEquals(EmojiManager.getForAlias("grin").getUnicode(), sentimentPerVideo);
    }

    /**
     * This method tests the <code>fetchVideos</code> method of <code>YoutubeAnalyzer</code> class.
     * It checks whether the JSON results for YouTube videos for a particular search keyword matches with a JSON result
     * manually fetched from a file.
     *
     * @author Umang J Patel
     */
    @Test   // This only tests Happy Path, i.e. result is returned. Extensive tests done on YoutubeApiClientTest.
    public void fetchVideosTest() throws ExecutionException, InterruptedException {
        youTubeApiClient = mock(YouTubeApiClient.class);
        youtubeAnalyzer.setYouTubeApiClient(youTubeApiClient);
        when(youTubeApiClient.fetchVideos(anyString())).thenReturn(CompletableFuture.supplyAsync(() -> searchResults));
        SearchResults searchResultsResponse = youtubeAnalyzer.fetchVideos("hello world").toCompletableFuture().get();
        Assert.assertEquals(searchResults, searchResultsResponse);
    }

    /**
     * This method tests the <code>getViewCountByVideoId</code> method of <code>YoutubeAnalyzer</code> class.
     * It tests fetching the view count of video (given the video ID)
     *
     * @author Umang J Patel
     */
    @Test   // This only tests Happy Path, i.e. result is returned. Extensive tests done on YoutubeApiClientTest.
    public void getViewCountByVideoIdTest() throws ExecutionException, InterruptedException {
        youTubeApiClient = mock(YouTubeApiClient.class);
        youtubeAnalyzer.setYouTubeApiClient(youTubeApiClient);
        String videoId = "abcXyz";
        when(youTubeApiClient.getViewCountByVideoId(anyString())).thenReturn(CompletableFuture.supplyAsync(() -> "10"));
        String viewCountResponse = youtubeAnalyzer.getViewCountByVideoId(videoId).toCompletableFuture().get();
        Assert.assertEquals("10", viewCountResponse);
    }

    /**
     * This method tests the <code>getChannelInformationByChannelId</code> method of <code>YoutubeAnalyzer</code> class.
     * It checks whether the JSON results for channel information (given a channel ID) matches with a JSON result manually
     * fetched from a file.
     *
     * @author Umang J Patel
     */
    @Test   // This only tests Happy Path, i.e. result is returned. Extensive tests done on YouTubeApiClient.
    public void getChannelInformationByChannelIdTest() throws ExecutionException, InterruptedException {
        youTubeApiClient = mock(YouTubeApiClient.class);
        youtubeAnalyzer.setYouTubeApiClient(youTubeApiClient);
        String channelId = "abcXyz";
        when(youTubeApiClient.getChannelInformationByChannelId(anyString())).thenReturn(CompletableFuture.supplyAsync(() -> channelResultItems));
        ChannelResultItems channelResultItemsResponse = youtubeAnalyzer.getChannelInformationByChannelId(channelId).toCompletableFuture().get();
        Assert.assertEquals(channelResultItems, channelResultItemsResponse);
    }

    /**
     * This method tests the <code>getVideosJsonByChannelId</code> method of <code>YoutubeAnalyzer</code> class.
     * It checks whether the JSON results for YouTube videos of a particular channel (given channel ID) matches with a
     * JSON result manually fetched from a file.
     *
     * @author Umang J Patel
     */
    @Test   // This only tests Happy Path, i.e. result is returned. Extensive tests done on YouTubeApiClient.
    public void getVideosJsonByChannelIdTest() throws ExecutionException, InterruptedException {
        youTubeApiClient = mock(YouTubeApiClient.class);
        youtubeAnalyzer.setYouTubeApiClient(youTubeApiClient);
        String channelId = "abcXyz";
        when(youTubeApiClient.getVideosJsonByChannelId(anyString(), anyString())).thenReturn(CompletableFuture.supplyAsync(() -> searchResults));
        SearchResults searchResultsResponse = youtubeAnalyzer.getVideosJsonByChannelId(channelId, "searchKeyword").toCompletableFuture().get();
        Assert.assertEquals(searchResults, searchResultsResponse);
    }

    /**
     * This method tests the <code>getCommentsString</code> method of <code>YoutubeAnalyzer</code> for empty comments
     * and expects the comments to be empty.
     *
     * @author Umang J Patel
     */
    @Test   //CommentResults is null. Should return empty string as result.
    public void getCommentsTest0() {
        String comments = youtubeAnalyzer.getCommentsString(new CommentResults());
        Assert.assertEquals(comments, "");
    }

    /**
     * This method tests the <code>getCommentsString</code> method of <code>YoutubeAnalyzer</code>
     * and expects the comments to contain only emojis within our domain of interest (sets of happy and sad emojis).
     *
     * @author Umang J Patel
     */
    @Test
    public void getCommentsTest1() {
        String emojiFilteredCommentsString = youtubeAnalyzer.getCommentsString(commentResults);
        String happyEmojiUnicode = EmojiManager.getForAlias("heart_eyes").getUnicode();
        String expectedFilteredCommentString = String.join("", Collections.nCopies(5, happyEmojiUnicode));
        Assert.assertEquals(expectedFilteredCommentString, emojiFilteredCommentsString);
    }

    /**
     * This method tests the <code>getAnalysisResult</code> method of <code>YoutubeAnalyzer</code>
     * and expects the sentiment to be <code>happy</code>.
     *
     * @author Umang J Patel
     */
    @Test   //Happy sentiment
    public void getAnalysisResultTest0() {
        String analysisResult = youtubeAnalyzer.getAnalysisResult(commentResults);
        Assert.assertEquals(EmojiManager.getForAlias("grin").getUnicode(), analysisResult);
    }

    /**
     * This method tests the <code>getAnalysisResult</code> method of <code>YoutubeAnalyzer</code>
     * and expects the sentiment to be <code>sad</code>.
     *
     * @author Umang J Patel
     */
    @Test   //Sad sentiment
    public void getAnalysisResultTest1() {
        commentResults = DatasetHelper.jsonFileToObject(new File("test/dataset/comments/sad_video.json"), CommentResults.class);
        String analysisResult = youtubeAnalyzer.getAnalysisResult(commentResults);
        Assert.assertEquals(EmojiManager.getForAlias("pensive").getUnicode(), analysisResult);
    }

    /**
     * This method tests the <code>getAnalysisResult</code> method of <code>YoutubeAnalyzer</code>
     * and expects the sentiment to be <code>neutral</code>.
     *
     * @author Umang J Patel
     */
    @Test   //neutral sentiment
    public void getAnalysisResultTest2() {
        commentResults = DatasetHelper.jsonFileToObject(new File("test/dataset/comments/neutral_video.json"), CommentResults.class);
        String analysisResult = youtubeAnalyzer.getAnalysisResult(commentResults);
        Assert.assertEquals(EmojiManager.getForAlias("neutral_face").getUnicode(), analysisResult);
    }

    /**
     * This method tests the <code>getAnalysisResult</code> method of <code>YoutubeAnalyzer</code> for empty comments
     * and expects the sentiment to be <code>neutral</code>.
     *
     * @author Umang J Patel
     */
    @Test   //empty comments returns neutral sentiment
    public void getAnalysisResultTest3() {
        commentResults = new CommentResults();
        String analysisResult = youtubeAnalyzer.getAnalysisResult(commentResults);
        Assert.assertEquals(EmojiManager.getForAlias("neutral_face").getUnicode(), analysisResult);
    }

    /**
     * This method destroys every object.
     */
    @After
    public void destroy() {
        searchResults = null;
        searchResultsLinkedHashMap = null;
        youtubeAnalyzer = null;
        channelResultItems = null;
        videos = null;
        commentResults = null;
    }
}