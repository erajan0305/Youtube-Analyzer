package models.Helper;

import play.mvc.Http;

/**
 * This class manages session for the Application.
 *
 * @author Kishan Bhimani
 */
public class SessionHelper {
    private static final String SESSION_KEY = "sessionId";

    public static String getSessionKey() {
        return SESSION_KEY;
    }

    /**
     * @param request Http Request
     * @return User-Agent obtained from the request headers.
     * @author Kishan Bhimani
     */
    public static String getUserAgentNameFromRequest(Http.Request request) {
        return request.getHeaders().get("User-Agent").orElse(null);
    }

    /**
     * @param request Http Request
     * @return Boolean whether session exists or not.
     * @author Kishan Bhimani
     */
    public static boolean isSessionExist(Http.Request request) {
        return request.session().get(SESSION_KEY).orElse(null) != null;
    }
}
