package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.TestActor;
import akka.testkit.TestKit;
import akka.testkit.TestProbe;
import com.fasterxml.jackson.databind.JsonNode;
import models.POJO.SearchResults.SearchResults;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import play.libs.Json;

import java.util.LinkedHashMap;

/**
 * This is a test class for {@link WebSocketActor}.
 *
 * @author Rajan Shah
 */
public class WebSocketActorTest {

    /**
     * The Actor system.
     */
    static ActorSystem actorSystem;

    /**
     * Initializes the actor system for tests
     *
     * @author Rajan Shah
     */
    @BeforeClass
    public static void init() {
        actorSystem = ActorSystem.create();
    }

    /**
     * Destroys the instantiated objects at the end of all the tests.
     *
     * @author Rajan Shah
     */
    @AfterClass
    public static void destroy() {
        actorSystem = null;
    }

    /**
     * This tests the message passing between {@link WebSocketActor} and {@link UserActor} to update search results.
     *
     * @author Rajan Shah
     */
    @Test
    public void updateSearchResultsMessageTest() {
        final TestProbe userTestActor = new TestProbe(actorSystem);

        userTestActor.setAutoPilot(new TestActor.AutoPilot() {
            @Override
            public TestActor.AutoPilot run(ActorRef sender, Object msg) {
                sender.tell(Json.toJson(new LinkedHashMap<String, SearchResults>()), ActorRef.noSender());
                return null;
            }
        });

        final TestKit webSocketResponseTestActor = new TestKit(actorSystem);
        final ActorRef webSocketActor = actorSystem.actorOf(WebSocketActor.props(webSocketResponseTestActor.testActor(), userTestActor.ref()));
        webSocketActor.tell(new UserActor.UpdateSearchResultsRequest(), ActorRef.noSender());
        userTestActor.expectMsgClass(UserActor.UpdateSearchResultsRequest.class);
        webSocketResponseTestActor.expectMsgClass(JsonNode.class);
    }
}
