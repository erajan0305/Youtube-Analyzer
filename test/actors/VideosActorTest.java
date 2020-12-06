package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.TestKit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This is a test class for {@link VideosActor}.
 *
 * @author Rajan Shah
 */
public class VideosActorTest {

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
     * This tests the message passing between {@link VideosActor} and {@link YoutubeApiClientActor} to fetch
     * videos by channelId and keyword.
     *
     * @author Rajan Shah
     */
    @Test
    public void videosListMessageTest() {
        final TestKit testKit = new TestKit(actorSystem);
        final ActorRef videosActor = actorSystem.actorOf(VideosActor.props(testKit.testActor()));
        videosActor.tell(new VideosActor.VideosList("channelId", "keyword"), ActorRef.noSender());
        YoutubeApiClientActor.GetVideosJsonByChannelId videosList = testKit.expectMsgClass(YoutubeApiClientActor.GetVideosJsonByChannelId.class);
        assertEquals("channelId", videosList.getChannelId());
        assertEquals("keyword", videosList.getKeyword());
    }
}
