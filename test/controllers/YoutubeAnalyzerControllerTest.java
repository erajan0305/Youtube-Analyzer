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
import org.junit.After;
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

/**
 * YoutubeAnalyzerController Test Class
 * This class tests only the Result Status and not the response body.
 *
 * @author Rajan Shah
 */

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

    /**
     * This is an initialization method which Injects dependencies using {@link GuiceApplicationBuilder},
     * initializes {@link YoutubeAnalyzerController} Object, mocks {@link YoutubeAnalyzer} Object and
     * mocks the {@link YoutubeAnalyzer}'s methods.
     *
     * @author Rajan Shah
     */
    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
        youtubeAnalyzerController = new YoutubeAnalyzerController();
        _mockFormFactory = new GuiceApplicationBuilder().injector().instanceOf(FormFactory.class);
        messagesApi = new GuiceApplicationBuilder().injector().instanceOf(MessagesApi.class);
        youtubeAnalyzerMock = mock(YoutubeAnalyzer.class);
        youtubeAnalyzerController.setYoutubeAnalyzer(youtubeAnalyzerMock);
        youtubeAnalyzerController.setFormFactory(_mockFormFactory);
        youtubeAnalyzerController.setMessagesApi(messagesApi);
        searchResults = new SearchResults();
        when(youtubeAnalyzerMock.getViewCountByVideoId(anyString())).thenReturn(CompletableFuture.supplyAsync(ArgumentMatchers::anyString));
        when(youtubeAnalyzerMock.getSentimentPerVideo(anyString())).thenReturn(CompletableFuture.supplyAsync(ArgumentMatchers::anyString));
        when(youtubeAnalyzerMock.getSimilarityStats(any(LinkedHashMap.class), anyString())).thenReturn(new HashMap<String, Long>());
    }

    /**
     * This method tests the <code>GET</code> request with a path of <code>/</code>, to Index page of the application
     * with valid Session and expects
     * {@link Result} 200
     *
     * @author Rajan Shah
     */
    @Test
    public void indexTestWithSession() throws ExecutionException, InterruptedException {
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest(routes.YoutubeAnalyzerController.index());
        requestBuilder.header("User-Agent", "chrome");
        requestBuilder.session(SessionHelper.getSessionKey(), requestBuilder.getHeaders().get("User-Agent").get());
        Result result = youtubeAnalyzerController.index(requestBuilder.build()).get();
        Assert.assertEquals(OK, result.status());
    }

    /**
     * This method tests the <code>POST</code> request with a path of <code>/</code>, to Index page of the application
     * with valid Session and expects {@link Result} 200.
     *
     * @throws ExecutionException   Exception might occur on calling get() on {@link CompletableFuture}.
     * @throws InterruptedException Exception might occur on calling get() on {@link CompletableFuture}.
     * @author Rajan Shah
     */
    @Test
    public void fetchVideosByKeywordsTest() throws ExecutionException, InterruptedException {
        SearchResults searchResults1 = new SearchResults();
        SearchResultItem searchResultItem = new SearchResultItem();
        searchResultItem.setId(new Id("abcXyz"));
        searchResultItem.setViewCount("123");
        searchResultItem.setSnippet(new Snippet("123", "channelTitle", "title", "description", "publishedAt", "publishTime"));
        searchResults1.setItems(new ArrayList<SearchResultItem>() {{
            add(searchResultItem);
        }});
        when(youtubeAnalyzerMock.fetchVideos("hello world")).thenReturn(CompletableFuture.supplyAsync(() -> searchResults1));
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest(routes.YoutubeAnalyzerController.fetchVideosByKeywords());
        requestBuilder.header("User-Agent", "chrome");
        requestBuilder.session(SessionHelper.getSessionKey(), requestBuilder.getHeaders().get("User-Agent").get());
        Map<String, String[]> requestBody = new HashMap<>();
        String[] searchKeyWord = new String[]{"hello world"};
        requestBody.put("searchKeyword", searchKeyWord);
        requestBuilder.bodyFormArrayValues(requestBody);
        CompletionStage<Result> resultCompletionStage = youtubeAnalyzerController.fetchVideosByKeywords(requestBuilder.build());
        Assert.assertEquals(OK, resultCompletionStage.toCompletableFuture().get().status());
    }

    /**
     * This method tests the <code>GET</code> request with a path of <code>/:keyword</code>, to Similarity States page
     * of the application with valid Session and expects {@link Result} 404 since this method requires at least one
     * search result to find similarity stats for.
     *
     * @author Rajan Shah
     */
    @Test
    public void getSimilarityStatsTest0() throws ExecutionException, InterruptedException {
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest(routes.YoutubeAnalyzerController.fetchSimilarityStats("hello world"));
        requestBuilder.header("User-Agent", "chrome");
        requestBuilder.session(SessionHelper.getSessionKey(), requestBuilder.getHeaders().get("User-Agent").get());
        Result result = youtubeAnalyzerController.fetchSimilarityStats(requestBuilder.build(), "hello world").toCompletableFuture().get();
        Assert.assertEquals(NOT_FOUND, result.status());
    }

    /**
     * This method tests the <code>GET</code> request with a path of <code>/:keyword</code>, to Similarity States page
     * of the application with valid Session and a call to {@link YoutubeAnalyzerController}'s
     * <code>fetchVideosByKeywords</code> and expects {@link Result} 200.
     *
     * @throws ExecutionException   Exception might occur on calling get() on {@link CompletableFuture}.
     * @throws InterruptedException Exception might occur on calling get() on {@link CompletableFuture}.
     * @author Rajan Shah
     */
    @Test
    public void getSimilarityStatsTest1() throws ExecutionException, InterruptedException {
        SearchResults searchResults1 = new SearchResults();
        SearchResultItem searchResultItem = new SearchResultItem();
        searchResultItem.setId(new Id("abcXyz"));
        searchResultItem.setViewCount("123");
        searchResultItem.setSnippet(new Snippet("123", "channelTitle", "title", "description", "publishedAt", "publishTime"));
        searchResults1.setItems(new ArrayList<SearchResultItem>() {{
            add(searchResultItem);
        }});
        when(youtubeAnalyzerMock.fetchVideos("hello world")).thenReturn(CompletableFuture.supplyAsync(() -> searchResults1));
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest(routes.YoutubeAnalyzerController.fetchVideosByKeywords());
        requestBuilder.header("User-Agent", "chrome");
        requestBuilder.session(SessionHelper.getSessionKey(), requestBuilder.getHeaders().get("User-Agent").get());

        Map<String, String[]> requestBody = new HashMap<>();
        String[] searchKeyWord = new String[]{"hello world"};
        requestBody.put("searchKeyword", searchKeyWord);
        requestBuilder.bodyFormArrayValues(requestBody);
        CompletionStage<Result> resultCompletionStage = youtubeAnalyzerController.fetchVideosByKeywords(requestBuilder.build());
        Assert.assertEquals(OK, resultCompletionStage.toCompletableFuture().get().status());

        requestBuilder = Helpers.fakeRequest(routes.YoutubeAnalyzerController.fetchSimilarityStats("hello world"));
        requestBuilder.header("User-Agent", "chrome");
        requestBuilder.session(SessionHelper.getSessionKey(), requestBuilder.getHeaders().get("User-Agent").get());
        Result result = youtubeAnalyzerController.fetchSimilarityStats(requestBuilder.build(), "hello world").toCompletableFuture().get();
        Assert.assertEquals(OK, result.status());
    }

    /**
     * This method tests the <code>GET</code> request with a path of <code>/:id/:keyword</code>, to Channel Information
     * page of the application with valid Session and expects {@link Result} 404 when no channel information is available.
     *
     * @throws ExecutionException   Exception might occur on calling get() on {@link CompletableFuture}.
     * @throws InterruptedException Exception might occur on calling get() on {@link CompletableFuture}.
     * @author Rajan Shah
     */
    @Test
    public void fetchChannelInformationAndTop10VideosTest0() throws ExecutionException, InterruptedException {
        when(youtubeAnalyzerMock.getChannelInformationByChannelId(anyString())).thenReturn(CompletableFuture.supplyAsync(ChannelResultItems::new));
        when(youtubeAnalyzerMock.getVideosJsonByChannelId(anyString(), anyString())).thenReturn(CompletableFuture.supplyAsync(SearchResults::new));
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest(routes.YoutubeAnalyzerController.fetchChannelInformationAndTop10Videos("abcXyz", "hello world"));
        requestBuilder.header("User-Agent", "chrome");
        requestBuilder.session(SessionHelper.getSessionKey(), requestBuilder.getHeaders().get("User-Agent").get());
        CompletionStage<Result> resultCompletionStage = youtubeAnalyzerController.fetchChannelInformationAndTop10Videos(requestBuilder.build(), "abcXyz", "hello wolrd");
        Assert.assertEquals(NOT_FOUND, resultCompletionStage.toCompletableFuture().get().status());
    }

    /**
     * This method tests the <code>GET</code> request with a path of <code>/:id/:keyword</code>, to Channel Information
     * page of the application with valid Session and expects {@link Result} 200 when channel information and
     * videos for channel id and keyword is available.
     *
     * @throws ExecutionException   Exception might occur on calling get() on {@link CompletableFuture}.
     * @throws InterruptedException Exception might occur on calling get() on {@link CompletableFuture}.
     * @author Rajan Shah
     */
    @Test
    public void fetchChannelInformationAndTop10VideosTest1() throws ExecutionException, InterruptedException {
        ChannelResultItems channelResultItems = new ChannelResultItems();
        ChannelItem channelItem = new ChannelItem();
        channelItem.setId("abcXyz");
        channelItem.setSnippet(new models.POJO.Channel.Snippet("title", "description", "country", "customUrl", "publishedAt"));
        channelItem.setChannelStatistics(new ChannelStatistics("123", "456", "11"));
        channelResultItems.setItems(new ArrayList<ChannelItem>() {{
            add(channelItem);
        }});
        SearchResults searchResults1 = new SearchResults();
        SearchResultItem searchResultItem = new SearchResultItem();
        searchResultItem.setId(new Id("abcXyz"));
        searchResultItem.setViewCount("123");
        searchResultItem.setSnippet(new Snippet("123", "channelTitle", "title", "description", "publishedAt", "publishTime"));
        searchResults1.setItems(new ArrayList<SearchResultItem>() {{
            add(searchResultItem);
        }});
        when(youtubeAnalyzerMock.getChannelInformationByChannelId(anyString())).thenReturn(CompletableFuture.supplyAsync(() -> channelResultItems));
        when(youtubeAnalyzerMock.getVideosJsonByChannelId(anyString(), anyString())).thenReturn(CompletableFuture.supplyAsync(() -> searchResults1));
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest(routes.YoutubeAnalyzerController.fetchChannelInformationAndTop10Videos("abcXyz", "hello world"));
        requestBuilder.header("User-Agent", "chrome");
        requestBuilder.session(SessionHelper.getSessionKey(), requestBuilder.getHeaders().get("User-Agent").get());
        CompletionStage<Result> resultCompletionStage = youtubeAnalyzerController.fetchChannelInformationAndTop10Videos(requestBuilder.build(), "abcXyz", "hello world");
        Assert.assertEquals(200, resultCompletionStage.toCompletableFuture().get().status());
    }

    @After
    public void destroy() {
        youtubeAnalyzerController = null;
        youtubeAnalyzerMock = null;
        messagesApi = null;
        _mockFormFactory = null;
        searchResults = null;
    }
}
