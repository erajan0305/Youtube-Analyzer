package models.Helper;

import models.POJO.Channel.ChannelResultItems;
import models.POJO.SearchResults.SearchResults;
import play.mvc.Http;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class SessionHelper {
    private static final HashMap<String, LinkedHashMap<String, SearchResults>> sessionSearchResultsBySearchKeywordHashMap = new HashMap<>();
    private static final HashMap<String, HashMap<String, ChannelResultItems>> sessionChannelItemHashMap = new HashMap<>();
    private static final HashMap<String, HashMap<String, SearchResults>> sessionVideosForChannelId = new HashMap<>();
    public static final String SESSION_KEY = "sessionId";

    public static String getUserAgentNameFromRequest(Http.Request request) {
        return request.getHeaders().get(Http.HeaderNames.USER_AGENT).orElse(null);
    }

    public static LinkedHashMap<String, SearchResults> getSearchResultsHashMapFromSession(Http.Request request) {
        String key = getSessionValue(request);
        return sessionSearchResultsBySearchKeywordHashMap.get(key);
    }

    public static void setSearchResultsHashMapFromSession(Http.Request request, String searchKeyword, SearchResults searchResults) {
        String key = getSessionValue(request);
        LinkedHashMap<String, SearchResults> searchResultsLinkedHashMap = getSearchResultsHashMapFromSession(request);
        searchResultsLinkedHashMap.put(searchKeyword, searchResults);
        sessionSearchResultsBySearchKeywordHashMap.put(key, searchResultsLinkedHashMap);
    }

    public static void setSearchResultsSession(Http.Request request, LinkedHashMap<String, SearchResults> searchResultsLinkedHashMap) {
        String key = getUserAgentNameFromRequest(request);
        sessionSearchResultsBySearchKeywordHashMap.put(key, searchResultsLinkedHashMap);
    }

    public static HashMap<String, ChannelResultItems> getChannelItemFromSession(Http.Request request) {
        String key = getSessionValue(request);
        return sessionChannelItemHashMap.get(key);
    }

    public static void setSessionChannelItemHashMap(Http.Request request, String channelId, ChannelResultItems channelResultItems) {
        String key = getSessionValue(request);
        HashMap<String, ChannelResultItems> channelResultItemsHashMap = getChannelItemFromSession(request);
        channelResultItemsHashMap.put(channelId, channelResultItems);
        sessionChannelItemHashMap.put(key, channelResultItemsHashMap);
    }

    public static void setSessionVideosForChannelId(Http.Request request, HashMap<String, SearchResults> videosByChannelId) {
        String key = getUserAgentNameFromRequest(request);
        sessionVideosForChannelId.put(key, videosByChannelId);
    }

    public static HashMap<String, SearchResults> getVideosByChannelIdFromSession(Http.Request request) {
        String key = getSessionValue(request);
        return sessionVideosForChannelId.get(key);
    }

    public static void setSessionVideosForChannelIdHashMap(Http.Request request, String channelId, SearchResults searchResults) {
        String key = getSessionValue(request);
        HashMap<String, SearchResults> videosByChannelIdHashMap = getVideosByChannelIdFromSession(request);
        videosByChannelIdHashMap.put(channelId, searchResults);
        sessionVideosForChannelId.put(key, videosByChannelIdHashMap);
    }

    public static void setChannelResultItemsSession(Http.Request request, HashMap<String, ChannelResultItems> channelResultItemsHashMap) {
        String key = getUserAgentNameFromRequest(request);
        sessionChannelItemHashMap.put(key, channelResultItemsHashMap);
    }


    public static boolean isSessionExist(Http.Request request) {
        return request.session().get(SESSION_KEY).orElse(null) != null;
    }

    public static String getSessionValue(Http.Request request) {
        return request.session().get(SESSION_KEY).orElse(null);
    }
}
