package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.testkit.typed.javadsl.TestKitJunitResource;
import akka.testkit.TestKit;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.ws.WSClient;

import static org.junit.Assert.assertEquals;

public class SupervisorActorTest {

    static ActorSystem actorSystem;
    static WSClient wsClient;

    @ClassRule
    public static final TestKitJunitResource testKit = new TestKitJunitResource();

    @Before
    public void init() {
        wsClient = new GuiceApplicationBuilder().injector().instanceOf(WSClient.class);
        actorSystem = ActorSystem.create();
    }

    @AfterClass
    public static void destroy() {
        actorSystem = null;
    }

    // NOT WORKING
    @Test
    public void fetchVideosMessageTest() {
        final TestKit testKit = new TestKit(actorSystem);
        final ActorRef supervisorActor = actorSystem.actorOf(SupervisorActor.props(wsClient));
        supervisorActor.tell(new YoutubeApiClientActor.FetchVideos("keyword"), ActorRef.noSender());
        YoutubeApiClientActor.FetchVideos fetchVideos = testKit.expectMsgClass(YoutubeApiClientActor.FetchVideos.class);
        assertEquals("keyword", fetchVideos.getSearchKey());
    }
}