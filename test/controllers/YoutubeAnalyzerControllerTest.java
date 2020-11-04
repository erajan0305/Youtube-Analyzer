package controllers;

import models.Helper.SessionHelper;
import models.Helper.YouTubeApiClient;
import models.Helper.YoutubeAnalyzer;
import models.POJO.SearchResults.SearchResults;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.ws.WSClient;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.mvc.Http.Status.OK;

@RunWith(MockitoJUnitRunner.class)
public class YoutubeAnalyzerControllerTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    @Inject
    WSClient wsClient;
    YoutubeAnalyzerController youtubeAnalyzerController = new YoutubeAnalyzerController();
    @Mock
    YoutubeAnalyzer youtubeAnalyzer;
    @Mock
    YouTubeApiClient youTubeApiClient;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
//        youtubeAnalyzerController = new YoutubeAnalyzerController();
//        youtubeAnalyzer = spy(new YoutubeAnalyzer());
//        youTubeApiClient = spy(new YouTubeApiClient(wsClient));
//        youtubeAnalyzer.setWsClient(wsClient);
//        youtubeAnalyzer.setYouTubeApiClient(youTubeApiClient);
//        youtubeAnalyzerController.setYoutubeAnalyzer(youtubeAnalyzer);
//        doReturn(CompletableFuture.supplyAsync(SearchResults::new)).when(youtubeAnalyzer).fetchVideos(anyString());
//        doReturn(CompletableFuture.supplyAsync(SearchResults::new)).when(youtubeAnalyzer.youTubeApiClient).fetchVideos(anyString());
    }
/*
    @Test
    public void test() {
        Helpers.running(Helpers.fakeApplication(), () -> {
            Http.RequestBuilder request = new Http.RequestBuilder()
                    .method(POST)
                    .uri("/");
            request.session(SessionHelper.SESSION_KEY, SessionHelper.SESSION_KEY);
            request.header("USER_AGENT", "chrome");
            Map<String, String[]> requestBody = new HashMap<>();
            String[] searchKeyWord = new String[]{"hello world"};
            requestBody.put("searchKeyword", searchKeyWord);
//        YoutubeAnalyzer youtubeAnalyzerMock = mock(YoutubeAnalyzer.class);
//        youtubeAnalyzerMock.youTubeApiClient = mock(YouTubeApiClient.class);
//        youTubeApiClientSpy = new YouTubeApiClient(youtubeAnalyzerSpy.wsClient);
//        youtubeAnalyzerSpy.youTubeApiClient = youTubeApiClientSpy;
            request.bodyFormArrayValues(requestBody);
            Result result = route(app, request);
            assertEquals(OK, result.status());
        });
    }*/

    @Test
    public void testIndex() {
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest(routes.YoutubeAnalyzerController.index());
        requestBuilder.session(SessionHelper.SESSION_KEY, SessionHelper.SESSION_KEY);
        requestBuilder.header("USER_AGENT", "chrome");
        Result result1 = Helpers.route(Helpers.fakeApplication(), requestBuilder);
        System.out.println(result1);
    }

    @Test
    public void testIndexWithSearchWords() {
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest(routes.YoutubeAnalyzerController.fetchVideosByKeywords());
        requestBuilder.session(SessionHelper.SESSION_KEY, SessionHelper.SESSION_KEY);
        requestBuilder.header("USER_AGENT", "chrome");
        Map<String, String[]> requestBody = new HashMap<>();
        String[] searchKeyWord = new String[]{"hello world"};
        requestBody.put("searchKeyword", searchKeyWord);
//        youtubeAnalyzerMock.youTubeApiClient = mock(YouTubeApiClient.class);
//        YouTubeApiClient youTubeApiClientSpy = new YouTubeApiClient(youtubeAnalyzerSpy.wsClient);
//        youtubeAnalyzerSpy.youTubeApiClient = youTubeApiClientSpy;
        requestBuilder.bodyFormArrayValues(requestBody);
        Result result = Helpers.route(Helpers.fakeApplication(), requestBuilder);
        YouTubeApiClient youTubeApiClient = mock(YouTubeApiClient.class);
        when(youTubeApiClient.fetchVideos(anyString())).thenReturn(CompletableFuture.supplyAsync(SearchResults::new));
        YoutubeAnalyzer youtubeAnalyzerMock = mock(YoutubeAnalyzer.class);
        when(youtubeAnalyzerMock.fetchVideos(anyString())).thenReturn(CompletableFuture.supplyAsync(SearchResults::new));
        youTubeApiClient.BASE_URL = "/";
        youtubeAnalyzerMock.setYouTubeApiClient(youTubeApiClient);
        youtubeAnalyzerController.setYoutubeAnalyzer(youtubeAnalyzerMock);
        assertEquals(OK, result.status());
    }
}
