package models.Helper;

import models.POJO.Channel.ChannelResultItems;
import models.POJO.SearchResults.SearchResults;
import play.mvc.Http;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * This class manages session for the Application.
 */
public class SessionHelper {
    private static final HashMap<String, LinkedHashMap<String, SearchResults>> sessionSearchResultsBySearchKeywordHashMap = new HashMap<>();
    public static final String SESSION_KEY = "sessionId";

    /**
     * @param request Http Request
     * @return User-Agent obtained from the request headers.
     * @author Kishan Bhimani
     */
    public static String getUserAgentNameFromRequest(Http.Request request) {
        return request.getHeaders().get("User-Agent").orElse(null);
    }

    /**
     * This method returns the SearchResults stored for the current session
     *
     * @param request Http Request
     * @return {@link LinkedHashMap} of SearchKeyword and {@link SearchResults}
     * @author Kishan Bhimani
     */
    public static LinkedHashMap<String, SearchResults> getSearchResultsHashMapFromSession(Http.Request request) {
        String key = getSessionValue(request);
        return sessionSearchResultsBySearchKeywordHashMap.get(key);
    }

    /**
     * This method stores the SearchResults for the current session
     *
     * @param request       Http Request
     * @param searchKeyword keyword for which SearchResults are fetched.
     * @param searchResults Response from {@link YouTubeApiClient} {@see fetchVideos}
     * @author Kishan Bhimani, Rajan Shah
     */
    public static void setSessionSearchResultsHashMap(Http.Request request, String searchKeyword, SearchResults searchResults) {
        String key = getSessionValue(request);
        LinkedHashMap<String, SearchResults> searchResultsLinkedHashMap = getSearchResultsHashMapFromSession(request);
        if (searchResultsLinkedHashMap == null) {
            searchResultsLinkedHashMap = new LinkedHashMap<>();
        }
        searchResultsLinkedHashMap.put(searchKeyword, searchResults);
        sessionSearchResultsBySearchKeywordHashMap.put(key, searchResultsLinkedHashMap);
    }

    /**
     * @param request Http Request
     * @return Boolean whether session exists or not.
     * @author Kishan Bhimani
     */
    public static boolean isSessionExist(Http.Request request) {
        return request.session().get(SESSION_KEY).orElse(null) != null;
    }

    /**
     * @param request Http Request
     * @return String Session value or null.
     * @author Kishan Bhimani
     */
    public static String getSessionValue(Http.Request request) {
        return request.session().get(SESSION_KEY).orElse(null);
    }
}
