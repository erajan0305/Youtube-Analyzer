package models.Helper;

import models.SearchResults.SearchResults;
import play.mvc.Http;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class SessionHelper {
    private static final HashMap<String, LinkedHashMap<String, SearchResults>> sessionHashMap = new HashMap<>();
    public static final String SESSION_KEY = "sessionId";

    public static String getUserAgentNameFromRequest(Http.Request request) {
        return request.getHeaders().get(Http.HeaderNames.USER_AGENT).orElse(null);
    }

    public static LinkedHashMap<String, SearchResults> getSearchResultsHashMapFromSession(Http.Request request) {
        String key = getSessionValue(request);
        return sessionHashMap.get(key);
    }

    public static boolean isSessionExist(Http.Request request) {
        return request.session().get(SESSION_KEY).orElse(null) != null;
    }

    public static String getSessionValue(Http.Request request) {
        return request.session().get(SESSION_KEY).orElse(null);
    }

    public static void setSession(Http.Request request, LinkedHashMap<String, SearchResults> searchResultsLinkedHashMap) {
        String key = getUserAgentNameFromRequest(request);
        sessionHashMap.put(key, searchResultsLinkedHashMap);
    }
}
