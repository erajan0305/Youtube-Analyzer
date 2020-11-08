package controllers;

import models.Helper.SessionHelper;
import models.Helper.YoutubeAnalyzer;
import models.POJO.Channel.ChannelItem;
import models.POJO.Channel.ChannelResultItems;
import models.POJO.Channel.ChannelStatistics;
import models.POJO.SearchResults.Id;
import models.POJO.SearchResults.SearchResultItem;
import models.POJO.SearchResults.SearchResults;
import models.POJO.SearchResults.Snippet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import play.Application;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;

@RunWith(MockitoJUnitRunner.class)
public class YoutubeAnalyzerControllerTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    YoutubeAnalyzerController youtubeAnalyzerController;
    FormFactory _mockFormFactory;
    MessagesApi messagesApi;
    YoutubeAnalyzer youtubeAnalyzerMock;
    SearchResults searchResults;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        youtubeAnalyzerController = new YoutubeAnalyzerController();
        _mockFormFactory = new GuiceApplicationBuilder().injector().instanceOf(FormFactory.class);
        messagesApi = new GuiceApplicationBuilder().injector().instanceOf(MessagesApi.class);
        youtubeAnalyzerMock = mock(YoutubeAnalyzer.class);
        youtubeAnalyzerController.setYoutubeAnalyzer(youtubeAnalyzerMock);
        youtubeAnalyzerController.setFormFactory(_mockFormFactory);
        youtubeAnalyzerController.setMessagesApi(messagesApi);
        searchResults = new SearchResults();
        when(youtubeAnalyzerMock.fetchVideos(anyString())).thenReturn(CompletableFuture.supplyAsync(() -> searchResults));
        when(youtubeAnalyzerMock.getViewCountByVideoId(anyString())).thenReturn(CompletableFuture.supplyAsync(ArgumentMatchers::anyString));
        when(youtubeAnalyzerMock.getSentimentPerVideo(anyString())).thenReturn(CompletableFuture.supplyAsync(ArgumentMatchers::anyString));
        when(youtubeAnalyzerMock.getSimilarityStats(any(LinkedHashMap.class), anyString())).thenReturn(new HashMap<String, Long>());
    }

    @Test
    public void indexTestWithSession() {
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest(routes.YoutubeAnalyzerController.index());
        requestBuilder.header("User-Agent", "chrome");
        requestBuilder.session(SessionHelper.SESSION_KEY, requestBuilder.getHeaders().get("User-Agent").get());
        Result result = youtubeAnalyzerController.index(requestBuilder.build());
        Assert.assertEquals(OK, result.status());
    }

    @Test
    public void fetchVideosByKeywordsTest() throws ExecutionException, InterruptedException {
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest(routes.YoutubeAnalyzerController.fetchVideosByKeywords());
        requestBuilder.header("User-Agent", "chrome");
        requestBuilder.session(SessionHelper.SESSION_KEY, requestBuilder.getHeaders().get("User-Agent").get());
        Map<String, String[]> requestBody = new HashMap<>();
        String[] searchKeyWord = new String[]{"hello world"};
        requestBody.put("searchKeyword", searchKeyWord);
        requestBuilder.bodyFormArrayValues(requestBody);
        CompletionStage<Result> resultCompletionStage = youtubeAnalyzerController.fetchVideosByKeywords(requestBuilder.build());
        Assert.assertEquals(OK, resultCompletionStage.toCompletableFuture().get().status());
    }

    @Test   // returns 404 as no search words present to find similar stats for.
    public void getSimilarityStatsTest0() {
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest(routes.YoutubeAnalyzerController.fetchSimilarityStats("hello world"));
        requestBuilder.header("User-Agent", "chrome");
        requestBuilder.session(SessionHelper.SESSION_KEY, requestBuilder.getHeaders().get("User-Agent").get());
        Result result = youtubeAnalyzerController.fetchSimilarityStats(requestBuilder.build(), "hello world");
        Assert.assertEquals(NOT_FOUND, result.status());
    }

    @Test
    public void getSimilarityStatsTest1() throws ExecutionException, InterruptedException {
        SearchResults searchResults1 = new SearchResults();
        SearchResultItem searchResultItem = new SearchResultItem();
        searchResultItem.id = new Id("abcXyz");
        searchResultItem.viewCount = "123";
        searchResultItem.snippet = new Snippet("123", "channelTitle", "title", "description", "publishedAt", "publishTime");
        searchResults1.items = new ArrayList<SearchResultItem>() {{
            add(searchResultItem);
        }};
        when(youtubeAnalyzerMock.fetchVideos("hello world")).thenReturn(CompletableFuture.supplyAsync(() -> searchResults1));
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest(routes.YoutubeAnalyzerController.fetchVideosByKeywords());
        requestBuilder.header("User-Agent", "chrome");
        requestBuilder.session(SessionHelper.SESSION_KEY, requestBuilder.getHeaders().get("User-Agent").get());

        Map<String, String[]> requestBody = new HashMap<>();
        String[] searchKeyWord = new String[]{"hello world"};
        requestBody.put("searchKeyword", searchKeyWord);
        requestBuilder.bodyFormArrayValues(requestBody);
        CompletionStage<Result> resultCompletionStage = youtubeAnalyzerController.fetchVideosByKeywords(requestBuilder.build());
        Assert.assertEquals(OK, resultCompletionStage.toCompletableFuture().get().status());

        requestBuilder = Helpers.fakeRequest(routes.YoutubeAnalyzerController.fetchSimilarityStats("hello world"));
        requestBuilder.header("User-Agent", "chrome");
        requestBuilder.session(SessionHelper.SESSION_KEY, requestBuilder.getHeaders().get("User-Agent").get());
        Result result = youtubeAnalyzerController.fetchSimilarityStats(requestBuilder.build(), "hello world");
        Assert.assertEquals(OK, result.status());
    }

    @Test   // returns 404 as no channel information is available
    public void fetchChannelInformationAndTop10VideosTest0() throws ExecutionException, InterruptedException {
        when(youtubeAnalyzerMock.getChannelInformationByChannelId(anyString())).thenReturn(CompletableFuture.supplyAsync(ChannelResultItems::new));
        when(youtubeAnalyzerMock.getVideosJsonByChannelId(anyString(), anyString())).thenReturn(CompletableFuture.supplyAsync(SearchResults::new));
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest(routes.YoutubeAnalyzerController.fetchChannelInformationAndTop10Videos("abcXyz", "hello world"));
        requestBuilder.header("User-Agent", "chrome");
        requestBuilder.session(SessionHelper.SESSION_KEY, requestBuilder.getHeaders().get("User-Agent").get());
        CompletionStage<Result> resultCompletionStage = youtubeAnalyzerController.fetchChannelInformationAndTop10Videos(requestBuilder.build(), "abcXyz", "hello wolrd");
        Assert.assertEquals(NOT_FOUND, resultCompletionStage.toCompletableFuture().get().status());
    }

    @Test
    public void fetchChannelInformationAndTop10VideosTest1() throws ExecutionException, InterruptedException {
        ChannelResultItems channelResultItems = new ChannelResultItems();
        ChannelItem channelItem = new ChannelItem();
        channelItem.id = "abcXyz";
        channelItem.snippet = new models.POJO.Channel.Snippet("title", "description", "country", "customUrl", "publishedAt");
        channelItem.channelStatistics = new ChannelStatistics("123", "456", "11");
        channelResultItems.items = new ArrayList<ChannelItem>() {{
            add(channelItem);
        }};
        SearchResults searchResults1 = new SearchResults();
        SearchResultItem searchResultItem = new SearchResultItem();
        searchResultItem.id = new Id("abcXyz");
        searchResultItem.viewCount = "123";
        searchResultItem.snippet = new Snippet("123", "channelTitle", "title", "description", "publishedAt", "publishTime");
        searchResults1.items = new ArrayList<SearchResultItem>() {{
            add(searchResultItem);
        }};
        when(youtubeAnalyzerMock.getChannelInformationByChannelId(anyString())).thenReturn(CompletableFuture.supplyAsync(() -> channelResultItems));
        when(youtubeAnalyzerMock.getVideosJsonByChannelId(anyString(), anyString())).thenReturn(CompletableFuture.supplyAsync(() -> searchResults1));
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest(routes.YoutubeAnalyzerController.fetchChannelInformationAndTop10Videos("abcXyz", "hello world"));
        requestBuilder.header("User-Agent", "chrome");
        requestBuilder.session(SessionHelper.SESSION_KEY, requestBuilder.getHeaders().get("User-Agent").get());
        CompletionStage<Result> resultCompletionStage = youtubeAnalyzerController.fetchChannelInformationAndTop10Videos(requestBuilder.build(), "abcXyz", "hello world");
        Assert.assertEquals(200, resultCompletionStage.toCompletableFuture().get().status());

        // returning from session
        requestBuilder = Helpers.fakeRequest(routes.YoutubeAnalyzerController.fetchChannelInformationAndTop10Videos("abcXyz", "hello world"));
        requestBuilder.header("User-Agent", "chrome");
        requestBuilder.session(SessionHelper.SESSION_KEY, requestBuilder.getHeaders().get("User-Agent").get());
        resultCompletionStage = youtubeAnalyzerController.fetchChannelInformationAndTop10Videos(requestBuilder.build(), "abcXyz", "hello world");
        Assert.assertEquals(200, resultCompletionStage.toCompletableFuture().get().status());
    }
}
