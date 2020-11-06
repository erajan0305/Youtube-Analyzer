package models;


import models.Helper.SessionHelper;
import models.POJO.Channel.ChannelResultItems;
import models.POJO.SearchResults.SearchResults;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

public class SessionHelperTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    Http.RequestBuilder request;

    @Before
    public void init() {
        request = fakeRequest(GET, "/");
        request.header("User-Agent", "chrome");
        request.session(SessionHelper.SESSION_KEY, request.getHeaders().get("User-Agent").get());
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testIsSessionExist() {
        assertTrue(SessionHelper.isSessionExist(request.build()));
    }

    @Test
    public void testSearchResultsSessionData() {
        SearchResults searchResults = new SearchResults();
        LinkedHashMap<String, SearchResults> searchResultsLinkedHashMap = new LinkedHashMap<String, SearchResults>() {{
            put("searchKeyword", searchResults);
        }};
        SessionHelper.setSessionSearchResultsHashMap(request.build(), "searchKeyword", searchResults);
        assertEquals(searchResultsLinkedHashMap.get("searchKeyword"), SessionHelper.getSearchResultsHashMapFromSession(request.build()).get("searchKeyword"));
    }

    @Test
    public void testChannelInformationSessionData() {
        ChannelResultItems channelResultItems = new ChannelResultItems();
        HashMap<String, ChannelResultItems> channelItemHashMap = new HashMap<String, ChannelResultItems>() {{
            put("abcxyz", channelResultItems);
        }};
        SessionHelper.setSessionChannelItemHashMap(request.build(), "abcxyz", channelResultItems);
        assertEquals(channelItemHashMap.get("abcxyz"), SessionHelper.getChannelItemFromSession(request.build()).get("abcxyz"));
    }

    @Test
    public void testVideosByChannelIdSessionData() {
        SearchResults searchResults = new SearchResults();
        LinkedHashMap<String, SearchResults> searchResultsLinkedHashMap = new LinkedHashMap<String, SearchResults>() {{
            put("abcxyz" + "searchKeyword", searchResults);
        }};
        SessionHelper.setSessionVideosForChannelIdHashMap(request.build(), "abcxyz", "searchKeyword", searchResults);
        assertEquals(searchResultsLinkedHashMap.get("abcxyz"), SessionHelper.getVideosByChannelIdFromSession(request.build()).get("abcxyz"));
    }

    @Test
    public void getSessionValueTest() {
        assertEquals(request.getHeaders().get("User-Agent").get(), SessionHelper.getSessionValue(request.build()));
    }

    @Test
    public void getUserAgentNameFromRequestTest() {
        System.out.println(SessionHelper.getUserAgentNameFromRequest(request.build()));
    }
}
