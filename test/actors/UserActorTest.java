package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.TestActor;
import akka.testkit.TestKit;
import akka.testkit.TestProbe;
import com.fasterxml.jackson.databind.JsonNode;
import dataset.DatasetHelper;
import models.POJO.SearchResults.SearchResults;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import play.libs.Json;
import scala.compat.java8.FutureConverters;

import java.io.File;
import java.util.LinkedHashMap;

import static akka.pattern.Patterns.ask;
import static org.junit.Assert.*;

/**
 * Unit tests for the {@link UserActor}
 *
 * @author Kishan Bhimani
 */
public class UserActorTest {

    /**
     * The Actor system.
     */
    static ActorSystem actorSystem;

    /**
     * Initializes the actor system for tests
     *
     * @author Kishan Bhimani
     */
    @BeforeClass
    public static void init() {
        actorSystem = ActorSystem.create();
    }

    /**
     * Destroys the instantiated objects at the end of all the tests.
     *
     * @author Kishan Bhimani
     */
    @AfterClass
    public static void destroy() {
        actorSystem = null;
    }

    /**
     * This test validates creation of user with valid {@link SearchResults}
     *
     * @author Kishan Bhimani
     */
    @Test
    public void setAndGetUserAndSearchResultsTest() {
        final TestKit testKit = new TestKit(actorSystem);
        final ActorRef userActor = actorSystem.actorOf(UserActor.props("xyz", testKit.testActor()));
        SearchResults PythonSearchResults = DatasetHelper.jsonFileToObject(new File("test/dataset/searchresults/Python.json"), SearchResults.class);
        userActor.tell(new UserActor.AddSearchResult("xyz", "python", PythonSearchResults), ActorRef.noSender());

        LinkedHashMap<String, SearchResults> linkedHashMap = FutureConverters.toJava(
                ask(userActor, new UserActor.GetSearchResults("xyz"), 2000))
                .thenApply(o -> (LinkedHashMap<String, SearchResults>) o)
                .toCompletableFuture().join();

        assertNotNull(linkedHashMap);
        assertTrue(linkedHashMap.keySet().contains("python"));
        assertEquals(PythonSearchResults.toString(), linkedHashMap.get("python").toString());
    }

    /**
     * Test for {@link UserActor#updateSearchResults()}
     *
     * @author Kishan Bhimani
     */
    @Test
    public void updateUserSearchResultsTest() {
        final TestProbe child = new TestProbe(actorSystem);
        SearchResults PythonUpdatedSearchResults = DatasetHelper.jsonFileToObject(new File("test/dataset/searchresults/PythonUpdated.json"), SearchResults.class);

        child.setAutoPilot(new TestActor.AutoPilot() {
            @Override
            public TestActor.AutoPilot run(ActorRef sender, Object msg) {
                sender.tell(PythonUpdatedSearchResults, ActorRef.noSender());
                return null;
            }
        });

        final ActorRef userActor = actorSystem.actorOf(UserActor.props("xyz", child.ref()));
        SearchResults PythonSearchResults = DatasetHelper.jsonFileToObject(new File("test/dataset/searchresults/Python.json"), SearchResults.class);

        userActor.tell(new UserActor.AddSearchResult("xyz", "python", PythonSearchResults), ActorRef.noSender());
        JsonNode jsonNode = FutureConverters.toJava(
                ask(userActor, new UserActor.UpdateSearchResultsRequest(), 2000))
                .thenApply(o -> (JsonNode) o)
                .toCompletableFuture().join();
        LinkedHashMap<String, SearchResults> searchResultsUpdated = Json.fromJson(jsonNode, LinkedHashMap.class);

        LinkedHashMap<String, SearchResults> linkedHashMap = FutureConverters.toJava(
                ask(userActor, new UserActor.GetSearchResults("xyz"), 2000))
                .thenApply(o -> (LinkedHashMap<String, SearchResults>) o)
                .toCompletableFuture().join();

        assertNotNull(searchResultsUpdated);
    }

    /**
     * This tests checks for null {@link SearchResults#items} when updating SearchResults using {@link UserActor#updateSearchResults()} .
     *
     * @author Kishan Bhimani
     */
    @Test
    public void nullUserSearchResultsItemsTest() {
        final TestProbe child = new TestProbe(actorSystem);

        child.setAutoPilot(new TestActor.AutoPilot() {
            @Override
            public TestActor.AutoPilot run(ActorRef sender, Object msg) {
                sender.tell(new SearchResults(), ActorRef.noSender());
                return null;
            }
        });

        final ActorRef userActor = actorSystem.actorOf(UserActor.props("xyz", child.ref()));
        SearchResults PythonSearchResults = DatasetHelper.jsonFileToObject(new File("test/dataset/searchresults/Python.json"), SearchResults.class);
        userActor.tell(new UserActor.AddSearchResult("xyz", "python", PythonSearchResults), ActorRef.noSender());

        JsonNode jsonNode = FutureConverters.toJava(
                ask(userActor, new UserActor.UpdateSearchResultsRequest(), 2000))
                .thenApply(o -> (JsonNode) o)
                .toCompletableFuture().join();

        assertNotNull(jsonNode);
        assertEquals("", jsonNode.asText());
    }
}
