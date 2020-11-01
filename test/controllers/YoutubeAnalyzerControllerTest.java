package controllers;

import models.Helper.SessionHelper;
import models.Helper.YoutubeAnalyzer;
import models.POJO.SearchResults.SearchResults;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

public class YoutubeAnalyzerControllerTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    YoutubeAnalyzer youtubeAnalyzerSpy;

    @Test
    public void testIndex() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/");
        request.session(SessionHelper.SESSION_KEY, SessionHelper.SESSION_KEY);
        request.header("USER_AGENT", "chrome");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testIndexWithSearchWords() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri("/");
        request.session(SessionHelper.SESSION_KEY, SessionHelper.SESSION_KEY);
        request.header("USER_AGENT", "chrome");
        youtubeAnalyzerSpy = spy(new YoutubeAnalyzer());
        doReturn(CompletableFuture.supplyAsync(SearchResults::new)).when(youtubeAnalyzerSpy).fetchVideos(anyString());
        verify(youtubeAnalyzerSpy).fetchVideos(anyString());
        Map<String, String[]> requestBody = new HashMap<>();
        String[] searchKeyWord = new String[]{"hello world"};
        requestBody.put("searchKeyword", searchKeyWord);
        request.bodyFormArrayValues(requestBody);
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }
}
