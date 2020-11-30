package models.Actors;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import play.libs.ws.WSClient;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

public class SupervisorActor extends AbstractActor {
    private final ActorRef youtubeApiClientActor;
    private final ActorRef channelInfoActor;
    private final SupervisorStrategy strategy = new OneForOneStrategy(10,
            Duration.ofSeconds(5),
            DeciderBuilder
                    .match(TimeoutException.class, e -> (SupervisorStrategy.Directive) SupervisorStrategy.restart())
                    .build());

    public static Props props(WSClient wsClient) {
        return Props.create(SupervisorActor.class, wsClient);
    }

    public SupervisorActor(WSClient wsClient) {
        youtubeApiClientActor = getContext().actorOf(YoutubeApiClientActor.props(wsClient));
        channelInfoActor = getContext().actorOf(ChannelInfoActor.props(youtubeApiClientActor));
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(YoutubeApiClientActor.SetWSClient.class, t -> youtubeApiClientActor.tell(t, getSender()))
                .match(YoutubeApiClientActor.FetchVideos.class, t -> youtubeApiClientActor.tell(t, getSender()))
                .match(YoutubeApiClientActor.GetViewCountByVideoId.class, t -> youtubeApiClientActor.tell(t, getSender()))
                .match(YoutubeApiClientActor.GetVideosJsonByChannelId.class, t -> youtubeApiClientActor.tell(t, getSender()))
                .match(ChannelInfoActor.ChannelInfo.class, t -> channelInfoActor.tell(t, getSender()))
                .match(YoutubeApiClientActor.GetSentimentByVideoId.class, t -> youtubeApiClientActor.tell(t, getSender()))
                .build();
    }
}
