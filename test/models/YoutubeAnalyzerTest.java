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

public class YoutubeAnalyzerTest {
    SearchResults searchResults;
    ChannelResultItems channelResultItems;
    LinkedHashMap<String, SearchResults> searchResultsLinkedHashMap;
    YoutubeAnalyzer youtubeAnalyzer;
    YouTubeApiClient youTubeApiClient;
    CommentResults commentResults;
    Videos videos;

    @Before
    public void init() {
        searchResults = DatasetHelper
                .jsonFileToObject(new File("test/dataset/searchresults/Golang.json"), SearchResults.class);
        searchResultsLinkedHashMap = new LinkedHashMap<String, SearchResults>() {
            {
                put("golang", searchResults);
            }
        };
        youtubeAnalyzer = new YoutubeAnalyzer();
        channelResultItems = DatasetHelper
                .jsonFileToObject(new File("test/dataset/channelinformation/Channel_Golang_UC-R1UuxHVDyNoJN0Tn4nkiQ.json"), ChannelResultItems.class);
        commentResults = DatasetHelper.jsonFileToObject(new File("test/dataset/comments/happy_video.json"), CommentResults.class);
    }

    @Test
    public void getSimilarityStatsTest() {
        Map<String, Long> golangSimilarityStats = youtubeAnalyzer.getSimilarityStats(searchResultsLinkedHashMap, "golang");
        Assert.assertNotNull(golangSimilarityStats);
        Assert.assertTrue(golangSimilarityStats.size() > 0);
    }

    @Test
    public void getSimilarityStatsForSearchWordNotPresentTest() {
        String keyword = "hello world";
        Map<String, Long> golangSimilarityStats = youtubeAnalyzer.getSimilarityStats(searchResultsLinkedHashMap, keyword);
        Map<String, Long> similarityStatsNotFoundMap = new HashMap<String, Long>() {{
            put(keyword, (long) 0);
        }};
        Assert.assertEquals(similarityStatsNotFoundMap.get(keyword), golangSimilarityStats.get(keyword));
    }

    @Test
    public void getSentimentByVideoIdTest() throws ExecutionException, InterruptedException {
        youTubeApiClient = mock(YouTubeApiClient.class);
        youtubeAnalyzer.setYouTubeApiClient(youTubeApiClient);
        String videoId = "abcXyz";
        when(youTubeApiClient.getSentimentByVideoId(anyString())).thenReturn(CompletableFuture.supplyAsync(() -> commentResults));
        String sentimentPerVideo = youtubeAnalyzer.getSentimentPerVideo(videoId).toCompletableFuture().get();
        Assert.assertEquals(EmojiManager.getForAlias("grin").getUnicode(), sentimentPerVideo);
    }

    @Test   // This only tests Happy Path, i.e. result is returned. Extensive tests done on YoutubeApiClientTest.
    public void fetchVideosTest() throws ExecutionException, InterruptedException {
        youTubeApiClient = mock(YouTubeApiClient.class);
        youtubeAnalyzer.setYouTubeApiClient(youTubeApiClient);
        when(youTubeApiClient.fetchVideos(anyString())).thenReturn(CompletableFuture.supplyAsync(() -> searchResults));
        SearchResults searchResultsResponse = youtubeAnalyzer.fetchVideos("hello world").toCompletableFuture().get();
        Assert.assertEquals(searchResults, searchResultsResponse);
    }

    @Test   // This only tests Happy Path, i.e. result is returned. Extensive tests done on YoutubeApiClientTest.
    public void getViewCountByVideoIdTest() throws ExecutionException, InterruptedException {
        youTubeApiClient = mock(YouTubeApiClient.class);
        youtubeAnalyzer.setYouTubeApiClient(youTubeApiClient);
        String videoId = "abcXyz";
        when(youTubeApiClient.getViewCountByVideoId(anyString())).thenReturn(CompletableFuture.supplyAsync(() -> "10"));
        String viewCountResponse = youtubeAnalyzer.getViewCountByVideoId(videoId).toCompletableFuture().get();
        Assert.assertEquals("10", viewCountResponse);
    }

    @Test   // This only tests Happy Path, i.e. result is returned. Extensive tests done on YoutubeApiClientTest.
    public void getChannelInformationByChannelIdTest() throws ExecutionException, InterruptedException {
        youTubeApiClient = mock(YouTubeApiClient.class);
        youtubeAnalyzer.setYouTubeApiClient(youTubeApiClient);
        String channelId = "abcXyz";
        when(youTubeApiClient.getChannelInformationByChannelId(anyString())).thenReturn(CompletableFuture.supplyAsync(() -> channelResultItems));
        ChannelResultItems channelResultItemsResponse = youtubeAnalyzer.getChannelInformationByChannelId(channelId).toCompletableFuture().get();
        Assert.assertEquals(channelResultItems, channelResultItemsResponse);
    }

    @Test   // This only tests Happy Path, i.e. result is returned. Extensive tests done on YoutubeApiClientTest.
    public void getVideosJsonByChannelIdTest() throws ExecutionException, InterruptedException {
        youTubeApiClient = mock(YouTubeApiClient.class);
        youtubeAnalyzer.setYouTubeApiClient(youTubeApiClient);
        String channelId = "abcXyz";
        when(youTubeApiClient.getVideosJsonByChannelId(anyString(), anyString())).thenReturn(CompletableFuture.supplyAsync(() -> searchResults));
        SearchResults searchResultsResponse = youtubeAnalyzer.getVideosJsonByChannelId(channelId, "searchKeyword").toCompletableFuture().get();
        Assert.assertEquals(searchResults, searchResultsResponse);
    }

    @Test   //CommentResults is null. Should return empty string as result.
    public void getCommentsTest0() {
        String comments = youtubeAnalyzer.getCommentsString(new CommentResults());
        Assert.assertEquals(comments, "");
    }

    @Test
    public void getCommentsTest1() {
        String emojiFilteredCommentsString = youtubeAnalyzer.getCommentsString(commentResults);
        String happyEmojiUnicode = EmojiManager.getForAlias("heart_eyes").getUnicode();
        String expectedFilteredCommentString = String.join("", Collections.nCopies(5, happyEmojiUnicode));
        Assert.assertEquals(expectedFilteredCommentString, emojiFilteredCommentsString);
    }

    @Test   //Happy sentiment
    public void getAnalysisResultTest0() {
        String analysisResult = youtubeAnalyzer.getAnalysisResult(commentResults);
        Assert.assertEquals(EmojiManager.getForAlias("grin").getUnicode(), analysisResult);
    }

    @Test   //Sad sentiment
    public void getAnalysisResultTest1() {
        commentResults = DatasetHelper.jsonFileToObject(new File("test/dataset/comments/sad_video.json"), CommentResults.class);
        String analysisResult = youtubeAnalyzer.getAnalysisResult(commentResults);
        Assert.assertEquals(EmojiManager.getForAlias("pensive").getUnicode(), analysisResult);
    }

    @Test   //neutral sentiment
    public void getAnalysisResultTest2() {
        commentResults = DatasetHelper.jsonFileToObject(new File("test/dataset/comments/neutral_video.json"), CommentResults.class);
        String analysisResult = youtubeAnalyzer.getAnalysisResult(commentResults);
        Assert.assertEquals(EmojiManager.getForAlias("neutral_face").getUnicode(), analysisResult);
    }

    @Test   //empty comments returns neutral sentiment
    public void getAnalysisResultTest3() {
        commentResults = new CommentResults();
        String analysisResult = youtubeAnalyzer.getAnalysisResult(commentResults);
        Assert.assertEquals(EmojiManager.getForAlias("neutral_face").getUnicode(), analysisResult);
    }

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
