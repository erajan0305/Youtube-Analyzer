package controllers;

import models.Helper.SessionHelper;
import models.Helper.YoutubeAnalyzer;
import models.POJO.SearchResults.SearchResults;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

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
    }

    @Test
    public void testIndex() {
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest(routes.YoutubeAnalyzerController.index());
        requestBuilder.session(SessionHelper.SESSION_KEY, SessionHelper.SESSION_KEY);
        requestBuilder.header("USER_AGENT", "chrome");
        Result result = youtubeAnalyzerController.index(requestBuilder.build());
        Assert.assertEquals(OK, result.status());
    }

    @Test
    public void testIndexWithSearchWords() throws ExecutionException, InterruptedException {
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest(routes.YoutubeAnalyzerController.fetchVideosByKeywords());
        requestBuilder.session(SessionHelper.SESSION_KEY, SessionHelper.SESSION_KEY);
        requestBuilder.header("USER_AGENT", "chrome");
        Map<String, String[]> requestBody = new HashMap<>();
        String[] searchKeyWord = new String[]{"hello world"};
        requestBody.put("searchKeyword", searchKeyWord);
        requestBuilder.bodyFormArrayValues(requestBody);
        CompletionStage<Result> resultCompletionStage = youtubeAnalyzerController.fetchVideosByKeywords(requestBuilder.build());
        Assert.assertEquals(OK, resultCompletionStage.toCompletableFuture().get().status());
    }
}
