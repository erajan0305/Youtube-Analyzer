package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.TestKit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This is a test class for {@link ChannelInfoActor}.
 *
 * @author Rajan Shah
 */
public class ChannelInfoActorTest {

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
     * This tests the message passing between {@link ChannelInfoActor} and {@link YoutubeApiClientActor} to fetch
     * channelInfo by channelId
     *
     * @author Rajan Shah
     */
    @Test
    public void channelInfoMessageTest() {
        final TestKit testKit = new TestKit(actorSystem);
        final ActorRef channelInfoActor = actorSystem.actorOf(ChannelInfoActor.props(testKit.testActor()));
        channelInfoActor.tell(new ChannelInfoActor.ChannelInfo("channelId"), ActorRef.noSender());
        YoutubeApiClientActor.GetChannelInformationByChannelId channelInfo = testKit.expectMsgClass(YoutubeApiClientActor.GetChannelInformationByChannelId.class);
        assertEquals("channelId", channelInfo.getChannelId());
    }
}
