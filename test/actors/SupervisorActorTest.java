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

/**
 * This is a test class for {@link SupervisorActor}
 *
 * @author Rajan Shah
 */
public class SupervisorActorTest {

    /**
     * The Actor system.
     */
    static ActorSystem actorSystem;

    /**
     * The Ws client.
     */
    static WSClient wsClient;

    /**
     * The constant testKit.
     */
    @ClassRule
    public static final TestKitJunitResource testKit = new TestKitJunitResource();

    /**
     * Initializes the actor system and injects {@link WSClient} for tests.
     *
     * @author Rajan Shah
     */
    @Before
    public void init() {
        wsClient = new GuiceApplicationBuilder().injector().instanceOf(WSClient.class);
        actorSystem = ActorSystem.create();
    }

    /**
     * Destroys the instantiated objects.
     *
     * @author Rajan Shah
     */
    @AfterClass
    public static void destroy() {
        actorSystem = null;
        wsClient = null;
    }

    /**
     * This tests the message passing between {@link SupervisorActor} and {@link YoutubeApiClientActor} to fetch
     * videos by keyword.
     *
     * @author Rajan Shah
     */
    @Test
    public void fetchVideosMessageTest() {
        final TestKit testKit = new TestKit(actorSystem);
        final ActorRef supervisorActor = actorSystem.actorOf(SupervisorActor.props(testKit.testActor()));
        supervisorActor.tell(new YoutubeApiClientActor.FetchVideos("keyword"), ActorRef.noSender());
        YoutubeApiClientActor.FetchVideos fetchVideos = testKit.expectMsgClass(YoutubeApiClientActor.FetchVideos.class);
        assertEquals("keyword", fetchVideos.getSearchKey());
    }

    /**
     * This tests the message passing between {@link SupervisorActor} and {@link YoutubeApiClientActor} to fetch
     * videos by channelId and keyword.
     *
     * @author Rajan Shah
     */
    @Test
    public void videosByChannelIdAndKeywordTest() {
        final TestKit testKit = new TestKit(actorSystem);
        final ActorRef supervisorActor = actorSystem.actorOf(SupervisorActor.props(testKit.testActor()));
        supervisorActor.tell(new YoutubeApiClientActor.GetVideosJsonByChannelId("channelId", "keyword"), ActorRef.noSender());
        YoutubeApiClientActor.GetVideosJsonByChannelId videoByChannelIdAndKeyword = testKit.expectMsgClass(YoutubeApiClientActor.GetVideosJsonByChannelId.class);
        assertEquals("channelId", videoByChannelIdAndKeyword.getChannelId());
        assertEquals("keyword", videoByChannelIdAndKeyword.getKeyword());
    }

    /**
     * This tests the message passing between {@link SupervisorActor} and {@link YoutubeApiClientActor} to fetch
     * channel info by channelId.
     *
     * @author Rajan Shah
     */
    @Test
    public void channelInfoByChannelId() {
        final TestKit testKit = new TestKit(actorSystem);
        final ActorRef supervisorActor = actorSystem.actorOf(SupervisorActor.props(testKit.testActor()));
        supervisorActor.tell(new YoutubeApiClientActor.GetChannelInformationByChannelId("channelId"), ActorRef.noSender());
        YoutubeApiClientActor.GetChannelInformationByChannelId channelInfo = testKit.expectMsgClass(YoutubeApiClientActor.GetChannelInformationByChannelId.class);
        assertEquals("channelId", channelInfo.getChannelId());
    }

    /**
     * This tests the message passing between {@link SupervisorActor} and {@link YoutubeApiClientActor} to fetch
     * sentiment for a video by videoId.
     *
     * @author Rajan Shah
     */
    @Test
    public void sentimentByVideoIdTest() {
        final TestKit testKit = new TestKit(actorSystem);
        final ActorRef supervisorActor = actorSystem.actorOf(SupervisorActor.props(testKit.testActor()));
        supervisorActor.tell(new YoutubeApiClientActor.GetSentimentByVideoId("videoId"), ActorRef.noSender());
        YoutubeApiClientActor.GetSentimentByVideoId sentimentByVideoId = testKit.expectMsgClass(YoutubeApiClientActor.GetSentimentByVideoId.class);
        assertEquals("videoId", sentimentByVideoId.getVideoId());
    }

    /**
     * This tests the message passing between {@link SupervisorActor} and {@link YoutubeApiClientActor} to set
     * {@link WSClient}.
     *
     * @author Rajan Shah
     */
    @Test
    public void setWsClientTest() {
        final TestKit testKit = new TestKit(actorSystem);
        final ActorRef supervisorActor = actorSystem.actorOf(SupervisorActor.props(testKit.testActor()));
        supervisorActor.tell(new YoutubeApiClientActor.SetWSClient(wsClient), ActorRef.noSender());
        YoutubeApiClientActor.SetWSClient setWSClient = testKit.expectMsgClass(YoutubeApiClientActor.SetWSClient.class);
        assertEquals(wsClient, setWSClient.getWsClient());
    }
}
