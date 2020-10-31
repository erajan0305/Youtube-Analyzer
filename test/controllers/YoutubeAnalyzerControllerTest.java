package controllers;

import models.Helper.SessionHelper;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

public class YoutubeAnalyzerControllerTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

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
        Map<String, String[]> requestBody = new HashMap<>();
        String[] searchKeyWord = new String[]{"hello world"};
        requestBody.put("searchKeyword", searchKeyWord);
        request.bodyFormArrayValues(requestBody);
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }
}
