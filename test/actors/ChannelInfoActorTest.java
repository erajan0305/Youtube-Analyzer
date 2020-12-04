package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.TestKit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ChannelInfoActorTest {
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
        final ActorRef channelInfoActor = actorSystem.actorOf(ChannelInfoActor.props(testKit.testActor()));
        channelInfoActor.tell(new ChannelInfoActor.ChannelInfo("channelId"), ActorRef.noSender());
        YoutubeApiClientActor.GetChannelInformationByChannelId channelInfo = testKit.expectMsgClass(YoutubeApiClientActor.GetChannelInformationByChannelId.class);
        assertEquals("channelId", channelInfo.channelId);
    }
}
