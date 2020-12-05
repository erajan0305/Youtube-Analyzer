package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.TestKit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VideosActorTest {
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
        final ActorRef videosActor = actorSystem.actorOf(VideosActor.props(testKit.testActor()));
        videosActor.tell(new VideosActor.VideosList("channelId", "keyword"), ActorRef.noSender());
        YoutubeApiClientActor.GetVideosJsonByChannelId videosList = testKit.expectMsgClass(YoutubeApiClientActor.GetVideosJsonByChannelId.class);
        assertEquals("channelId", videosList.getChannelId());
        assertEquals("keyword", videosList.getKeyword());
    }
}
