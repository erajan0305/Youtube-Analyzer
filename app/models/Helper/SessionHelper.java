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
    private static final HashMap<String, HashMap<String, ChannelResultItems>> sessionChannelItemHashMap = new HashMap<>();
    private static final HashMap<String, HashMap<String, SearchResults>> sessionVideosForChannelId = new HashMap<>();
    public static final String SESSION_KEY = "sessionId";

    /**
     * @param request Http Request
     * @return User-Agent obtained from the request headers.
     */
    public static String getUserAgentNameFromRequest(Http.Request request) {
        return request.getHeaders().get("User-Agent").orElse(null);
    }

    /**
     * This method returns the SearchResults stored for the current session
     *
     * @param request Http Request
     * @return HashMap of SearchKeyword and {@link SearchResults}
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
     * @author Kishan Bhimani
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
     * This method returns the {@link ChannelResultItems} stored for the current session
     *
     * @param request Http Request
     * @return HashMap of ChannelId and {@link ChannelResultItems}
     * @author Rajan Shah
     */
    public static HashMap<String, ChannelResultItems> getChannelItemFromSession(Http.Request request) {
        String key = getSessionValue(request);
        return sessionChannelItemHashMap.get(key);
    }

    /**
     * This method stores the ChannelInformation for the current session by channelId
     *
     * @param request            Http Request
     * @param channelId          keyword for which SearchResults are fetched.
     * @param channelResultItems Response from {@link YouTubeApiClient} {@see getChannelInformationByChannelId}
     * @author Rajan Shah
     */
    public static void setSessionChannelItemHashMap(Http.Request request, String channelId, ChannelResultItems channelResultItems) {
        String key = getSessionValue(request);
        HashMap<String, ChannelResultItems> channelResultItemsHashMap = getChannelItemFromSession(request);
        if (channelResultItemsHashMap == null) {
            channelResultItemsHashMap = new HashMap<>();
        }
        channelResultItemsHashMap.put(channelId, channelResultItems);
        sessionChannelItemHashMap.put(key, channelResultItemsHashMap);
    }

    /**
     * This method returns the {@link SearchResults} stored for the current session for channelId and keyword
     *
     * @param request Http Request
     * @return HashMap of ChannelId and {@link SearchResults}
     * @author Rajan Shah
     */
    public static HashMap<String, SearchResults> getVideosByChannelIdFromSession(Http.Request request) {
        String key = getSessionValue(request);
        return sessionVideosForChannelId.get(key);
    }

    /**
     * This method stores the Videos for the current session by channelId and keyword
     *
     * @param request       Http Request
     * @param channelId     keyword for which SearchResults are fetched.
     * @param searchResults Response from {@link YouTubeApiClient} {@see fetchVideos}
     * @author Rajan Shah
     */
    public static void setSessionVideosForChannelIdHashMap(Http.Request request, String channelId, String keyword, SearchResults searchResults) {
        String key = getSessionValue(request);
        HashMap<String, SearchResults> videosByChannelIdHashMap = getVideosByChannelIdFromSession(request);
        if (videosByChannelIdHashMap == null) {
            videosByChannelIdHashMap = new HashMap<>();
        }
        videosByChannelIdHashMap.put(channelId + keyword, searchResults);
        sessionVideosForChannelId.put(key, videosByChannelIdHashMap);
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
