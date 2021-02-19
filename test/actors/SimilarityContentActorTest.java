package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.TestActor;
import akka.testkit.TestKit;
import akka.testkit.TestProbe;
import dataset.DatasetHelper;
import models.POJO.SearchResults.SearchResults;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import scala.compat.java8.FutureConverters;

import java.io.File;
import java.util.LinkedHashMap;

import static akka.pattern.Patterns.ask;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the {@link SimilarityContentActor}
 *
 * @author Kishan Bhimani
 */
public class SimilarityContentActorTest {

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
     * This test checks messageType for {@link SimilarityContentActor}.
     *
     * @author Kishan Bhimani
     */
    @Test
    public void similarityContentMessageTest() {
        final TestKit testKit = new TestKit(actorSystem);
        final ActorRef similarityContentActor = actorSystem.actorOf(SimilarityContentActor.props(testKit.testActor()));
        similarityContentActor.tell(new SimilarityContentActor.SimilarContentByKeyword("xyz", "hello world"), ActorRef.noSender());
        SessionActor.GetUserSearchResults userSearchResults = testKit.expectMsgClass(SessionActor.GetUserSearchResults.class);
        assertEquals("xyz", userSearchResults.userId);
    }

    /**
     * This method tests {@link SimilarityContentActor} when there is no similarity at all.
     *
     * @author Kishan Bhimani
     */
    @Test
    public void noSimilarityContentTest() {
        final TestProbe child = new TestProbe(actorSystem);

        child.setAutoPilot(new TestActor.AutoPilot() {
            @Override
            public TestActor.AutoPilot run(ActorRef sender, Object msg) {
                sender.tell(new LinkedHashMap<String, SearchResults>(), ActorRef.noSender());
                return null;
            }
        });

        final ActorRef similarityContentActor = actorSystem.actorOf(SimilarityContentActor.props(child.ref()));

        LinkedHashMap<String, Long> similarityContentLinkedHashmap = FutureConverters
                .toJava(ask(similarityContentActor, new SimilarityContentActor
                        .SimilarContentByKeyword("xyz", "hello world"), 2000))
                .toCompletableFuture()
                .thenApply(o -> (LinkedHashMap<String, Long>) o).join();
        assertEquals(0, similarityContentLinkedHashmap.size());
    }

    /**
     * This method tests {@link SimilarityContentActor} for similarity content.
     *
     * @author Kishan Bhimani
     */
    @Test
    public void similarityContentCountTest() {
        final TestProbe child = new TestProbe(actorSystem);

        child.setAutoPilot(new TestActor.AutoPilot() {
            @Override
            public TestActor.AutoPilot run(ActorRef sender, Object msg) {
                sender.tell(new LinkedHashMap<String, SearchResults>() {{
                    put("python", DatasetHelper.jsonFileToObject(new File("test/dataset/searchresults/Python.json"), SearchResults.class));
                }}, ActorRef.noSender());
                return null;
            }
        });

        final ActorRef similarityContentActor = actorSystem.actorOf(SimilarityContentActor.props(child.ref()));
        LinkedHashMap<String, Long> similarityContentLinkedHashmap = FutureConverters
                .toJava(ask(similarityContentActor, new SimilarityContentActor
                        .SimilarContentByKeyword("xyz", "python"), 2000))
                .toCompletableFuture()
                .thenApply(o -> (LinkedHashMap<String, Long>) o).join();
        assertEquals(12, (long) similarityContentLinkedHashmap.get("python"));
    }

}
