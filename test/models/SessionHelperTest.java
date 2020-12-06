package models;

import models.Helper.SessionHelper;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.test.WithApplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.GET;
import static play.test.Helpers.fakeRequest;

/**
 * This is test class for {@link SessionHelper}
 *
 * @author Kishan Bhimani
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
        request.session(SessionHelper.getSessionKey(), request.getHeaders().get("User-Agent").get());
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
     * This method is testing method for {@link SessionHelper#getUserAgentNameFromRequest(Http.Request)}
     *
     * @author Kishan Bhimani
     */
    @Test
    public void getUserAgentNameFromRequestTest() {
        assertEquals("chrome", SessionHelper.getUserAgentNameFromRequest(request.build()));
    }
}
