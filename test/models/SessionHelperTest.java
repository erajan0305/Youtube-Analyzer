package models;

import models.Helper.SessionHelper;
import models.POJO.SearchResults.SearchResults;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

/**
 * This is test class for {@link SessionHelper}
 */
public class SessionHelperTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    Http.RequestBuilder request;

    /**
     * This method initializes necessary testing environment for this class.
     *
     * @author Kishan Bhimani
     */
    @Before
    public void init() {
        request = fakeRequest(GET, "/");
        request.header("User-Agent", "chrome");
        request.session(SessionHelper.SESSION_KEY, request.getHeaders().get("User-Agent").get());
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    /**
     * This method is testing method for {@link SessionHelper#isSessionExist(Http.Request)}, matches
     * actual result with expected result.
     *
     * @author Kishan Bhimani
     */
    @Test
    public void testIsSessionExist() {
        assertTrue(SessionHelper.isSessionExist(request.build()));
    }

    /**
     * This method is testing method for {@link SessionHelper#setSessionSearchResultsHashMap(Http.Request, String, SearchResults)}, matches
     * actual result with expected result.
     *
     * @author Kishan Bhimani
     */
    @Test
    public void testSearchResultsSessionData() {
        SearchResults searchResults = new SearchResults();
        LinkedHashMap<String, SearchResults> searchResultsLinkedHashMap = new LinkedHashMap<String, SearchResults>() {{
            put("searchKeyword", searchResults);
        }};
        SessionHelper.setSessionSearchResultsHashMap(request.build(), "searchKeyword", searchResults);
        assertEquals(searchResultsLinkedHashMap.get("searchKeyword"), SessionHelper.getSearchResultsHashMapFromSession(request.build()).get("searchKeyword"));
    }

    /**
     * This method is testing method for {@link SessionHelper#getSessionValue(Http.Request)}, matches
     * actual result with expected result.
     *
     * @author Kishan Bhimani
     */
    @Test
    public void getSessionValueTest() {
        assertEquals(request.getHeaders().get("User-Agent").get(), SessionHelper.getSessionValue(request.build()));
    }

    /**
     * This method is testing method for {@link SessionHelper#getUserAgentNameFromRequest(Http.Request)}
     *
     * @author Kishan Bhimani
     */
    @Test
    public void getUserAgentNameFromRequestTest() {
        System.out.println(SessionHelper.getUserAgentNameFromRequest(request.build()));
    }
}
