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

public class SimilarityContentActorTest {

    static ActorSystem actorSystem;

    @BeforeClass
    public static void init() {
        actorSystem = ActorSystem.create();
    }

    @AfterClass
    public static void destroy() {
        actorSystem = null;
    }

    @Test
    public void test1() {
        final TestKit testKit = new TestKit(actorSystem);
        final ActorRef similarityContentActor = actorSystem.actorOf(SimilarityContentActor.props(testKit.testActor()));
        similarityContentActor.tell(new SimilarityContentActor.SimilarContentByKeyword("xyz", "hello world"), ActorRef.noSender());
        SessionActor.GetUserSearchResults userSearchResults = testKit.expectMsgClass(SessionActor.GetUserSearchResults.class);
        assertEquals("xyz", userSearchResults.userId);
    }

    @Test
    public void test2() {
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

    @Test
    public void test3() {
        final TestProbe child = new TestProbe(actorSystem);

        child.setAutoPilot(new TestActor.AutoPilot() {
            @Override
            public TestActor.AutoPilot run(ActorRef sender, Object msg) {
                sender.tell(new LinkedHashMap<String, SearchResults>() {{
                    put("python", DatasetHelper.jsonFileToObject(new File("test/dataset/searchResults/Python.json"), SearchResults.class));
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
