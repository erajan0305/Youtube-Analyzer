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
        wsClient = null;
    }

    @Test
    public void fetchVideosMessageTest() {
        final TestKit testKit = new TestKit(actorSystem);
        final ActorRef supervisorActor = actorSystem.actorOf(SupervisorActor.props(testKit.testActor()));
        supervisorActor.tell(new YoutubeApiClientActor.FetchVideos("keyword"), ActorRef.noSender());
        YoutubeApiClientActor.FetchVideos fetchVideos = testKit.expectMsgClass(YoutubeApiClientActor.FetchVideos.class);
        assertEquals("keyword", fetchVideos.getSearchKey());
    }

    @Test
    public void videosByChannelIdAndKeywordTest() {
        final TestKit testKit = new TestKit(actorSystem);
        final ActorRef supervisorActor = actorSystem.actorOf(SupervisorActor.props(testKit.testActor()));
        supervisorActor.tell(new YoutubeApiClientActor.GetVideosJsonByChannelId("channelId", "keyword"), ActorRef.noSender());
        YoutubeApiClientActor.GetVideosJsonByChannelId videoByChannelIdAndKeyword = testKit.expectMsgClass(YoutubeApiClientActor.GetVideosJsonByChannelId.class);
        assertEquals("channelId", videoByChannelIdAndKeyword.getChannelId());
        assertEquals("keyword", videoByChannelIdAndKeyword.getKeyword());
    }

    @Test
    public void channelInfoByChannelId() {
        final TestKit testKit = new TestKit(actorSystem);
        final ActorRef supervisorActor = actorSystem.actorOf(SupervisorActor.props(testKit.testActor()));
        supervisorActor.tell(new YoutubeApiClientActor.GetChannelInformationByChannelId("channelId"), ActorRef.noSender());
        YoutubeApiClientActor.GetChannelInformationByChannelId channelInfo = testKit.expectMsgClass(YoutubeApiClientActor.GetChannelInformationByChannelId.class);
        assertEquals("channelId", channelInfo.getChannelId());
    }

    @Test
    public void sentimentByVideoIdTest() {
        final TestKit testKit = new TestKit(actorSystem);
        final ActorRef supervisorActor = actorSystem.actorOf(SupervisorActor.props(testKit.testActor()));
        supervisorActor.tell(new YoutubeApiClientActor.GetSentimentByVideoId("videoId"), ActorRef.noSender());
        YoutubeApiClientActor.GetSentimentByVideoId sentimentByVideoId = testKit.expectMsgClass(YoutubeApiClientActor.GetSentimentByVideoId.class);
        assertEquals("videoId", sentimentByVideoId.getVideoId());
    }

    @Test
    public void setWsClientTest() {
        final TestKit testKit = new TestKit(actorSystem);
        final ActorRef supervisorActor = actorSystem.actorOf(SupervisorActor.props(testKit.testActor()));
        supervisorActor.tell(new YoutubeApiClientActor.SetWSClient(wsClient), ActorRef.noSender());
        YoutubeApiClientActor.SetWSClient setWSClient = testKit.expectMsgClass(YoutubeApiClientActor.SetWSClient.class);
        assertEquals(wsClient, setWSClient.getWsClient());
    }
}
