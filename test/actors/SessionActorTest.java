package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.testkit.typed.javadsl.TestKitJunitResource;
import akka.testkit.TestKit;
import dataset.DatasetHelper;
import models.Helper.SessionHelper;
import models.POJO.SearchResults.SearchResults;
import org.junit.*;

import static akka.pattern.Patterns.ask;
import static org.junit.Assert.*;

import play.mvc.Http;
import scala.compat.java8.FutureConverters;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.concurrent.CompletableFuture;

import static play.test.Helpers.GET;
import static play.test.Helpers.fakeRequest;

/**
 * Unit tests for the {@link SessionActor}
 *
 * @author Umang Patel
 */
public class SessionActorTest {

    @ClassRule
    public static final TestKitJunitResource testKit = new TestKitJunitResource();

    private static ActorSystem actorSystem;
    private static ActorRef sessionActor;
    private static Http.RequestBuilder request;

    /**
     * Initializes the actor system for tests
     *
     * @author Rajan Shah
     */
    @BeforeClass
    public static void init() {
        actorSystem = ActorSystem.create();
        sessionActor = actorSystem.actorOf(SessionActor.props());
        request = fakeRequest(GET, "/");
        request.header("User-Agent", "chrome");
        request.session(SessionHelper.getSessionKey(), request.getHeaders().get("User-Agent").get());
    }

    /**
     * Destroys the instantiated objects at the end of all the tests.
     *
     * @author Umang Patel
     */
    @AfterClass
    public static void destroy() {
        actorSystem = null;
    }

    /**
     * This test checks whether the session is stored and returned.
     *
     * @author Umang Patel
     */
    @Test
    public void testSessionUserCreation() {
        final TestKit testKit = new TestKit(actorSystem);
        final ActorRef supervisorActor = actorSystem.actorOf(SupervisorActor.props(testKit.testActor()));
        String userAgentName = SessionHelper.getUserAgentNameFromRequest(request.build());
        // Create the user from session
        sessionActor.tell(new SessionActor.CreateUser(userAgentName, supervisorActor), ActorRef.noSender());

        // Fetch the UserActor from the user ID
        sessionActor.tell(new SessionActor.GetUser(userAgentName), ActorRef.noSender());
        ActorRef userActor = FutureConverters.toJava(ask(sessionActor,
                new SessionActor.GetUser(userAgentName), 5000))
                .toCompletableFuture().thenApply(o -> (ActorRef) o).join();
        assertNotNull(userActor);
    }

    /**
     * This test checks if empty search results are obtained for a new session.
     *
     * @author Umang Patel
     */
    @Test
    public void testEmptySearchResultsForNewSession() {
        final TestKit testKit = new TestKit(actorSystem);
        final ActorRef supervisorActor = actorSystem.actorOf(SupervisorActor.props(testKit.testActor()));
        String userAgentName = SessionHelper.getUserAgentNameFromRequest(request.build());
        sessionActor.tell(new SessionActor.CreateUser(userAgentName, supervisorActor), ActorRef.noSender());

        LinkedHashMap<String, SearchResults> emptyResults = FutureConverters.toJava(ask(sessionActor, new SessionActor.GetUserSearchResults(userAgentName), 5000))
                .toCompletableFuture()
                .thenApplyAsync(o -> (LinkedHashMap<String, SearchResults>) o).join();
        assertTrue(emptyResults.isEmpty());
    }

    /**
     * This test checks if search results are being stored for a session.
     * @author Umang Patel
     */
    @Test
    public void testAddSearchResultsForNewSession() {
        final TestKit testKit = new TestKit(actorSystem);
        final ActorRef supervisorActor = actorSystem.actorOf(SupervisorActor.props(testKit.testActor()));
        String userAgentName = SessionHelper.getUserAgentNameFromRequest(request.build());
        sessionActor.tell(new SessionActor.CreateUser(userAgentName, supervisorActor), ActorRef.noSender());
        LinkedHashMap<String, SearchResults> emptyResults = FutureConverters.toJava(ask(sessionActor, new SessionActor.GetUserSearchResults(userAgentName), 5000))
                .toCompletableFuture()
                .thenApplyAsync(o -> (LinkedHashMap<String, SearchResults>) o).join();
        assertTrue(emptyResults.isEmpty());
        SearchResults searchResults = fetchDummyResults("Java").join();
        sessionActor.tell(new SessionActor.AddSearchResultsToUser(userAgentName, "Java", searchResults), ActorRef.noSender());
        LinkedHashMap<String, SearchResults> nonEmptyResults = FutureConverters.toJava(ask(sessionActor, new SessionActor.GetUserSearchResults(userAgentName), 5000))
                .toCompletableFuture()
                .thenApplyAsync(o -> (LinkedHashMap<String, SearchResults>) o).join();
        assertFalse(nonEmptyResults.isEmpty());
        assertEquals(searchResults, nonEmptyResults.get("Java"));
    }

    /**
     * This test checks if different search results are maintained for a session.
     *
     * @author Umang Patel
     */
    @Test
    public void testGetDifferentSearchResultsForSameSession() {
        final TestKit testKit = new TestKit(actorSystem);
        final ActorRef supervisorActor = actorSystem.actorOf(SupervisorActor.props(testKit.testActor()));
        String userAgentName = SessionHelper.getUserAgentNameFromRequest(request.build());
        sessionActor.tell(new SessionActor.CreateUser(userAgentName, supervisorActor), ActorRef.noSender());
        LinkedHashMap<String, SearchResults> emptyResults = FutureConverters.toJava(ask(sessionActor, new SessionActor.GetUserSearchResults(userAgentName), 5000))
                .toCompletableFuture()
                .thenApplyAsync(o -> (LinkedHashMap<String, SearchResults>) o).join();
        assertTrue(emptyResults.isEmpty());
        SearchResults javaActualSearchResults = fetchDummyResults("Java").join();
        SearchResults pythonActualSearchResults = fetchDummyResults("Python").join();
        sessionActor.tell(new SessionActor.AddSearchResultsToUser(userAgentName, "Java", javaActualSearchResults), ActorRef.noSender());
        LinkedHashMap<String, SearchResults> expectedSearchResults = FutureConverters.toJava(ask(sessionActor, new SessionActor.GetUserSearchResults(userAgentName), 5000))
                .toCompletableFuture()
                .thenApplyAsync(o -> (LinkedHashMap<String, SearchResults>) o).join();
        assertFalse(expectedSearchResults.isEmpty());
        assertEquals(javaActualSearchResults, expectedSearchResults.get("Java"));
        sessionActor.tell(new SessionActor.AddSearchResultsToUser(userAgentName, "Python", pythonActualSearchResults), ActorRef.noSender());
        expectedSearchResults = FutureConverters.toJava(ask(sessionActor, new SessionActor.GetUserSearchResults(userAgentName), 5000))
                .toCompletableFuture()
                .thenApplyAsync(o -> (LinkedHashMap<String, SearchResults>) o).join();
        assertEquals(pythonActualSearchResults, expectedSearchResults.get("Python"));
    }

    /**
     * Helper method for fetching search results from a downloaded JSON file.
     *
     * @param fileName represents the file name (without extension) in string
     * @return search results in the form of {@link CompletableFuture}
     * @author Umang Patel
     */
    @Ignore
    private static CompletableFuture<SearchResults> fetchDummyResults(String fileName) {
        return fileName.equals("null") ? null : CompletableFuture.supplyAsync(() ->
                DatasetHelper.jsonFileToObject(new File("test/dataset/searchresults/" + fileName + ".json"),
                        SearchResults.class)
        );
    }

}