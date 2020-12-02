package models.Actors;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.libs.ws.WSClient;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

public class SupervisorActor extends AbstractActor {
    private final ActorRef youtubeApiClientActor;
    private final ActorRef outActorRef;
    private final SupervisorStrategy strategy = new OneForOneStrategy(10,
            Duration.ofSeconds(5),
            DeciderBuilder
                    .match(TimeoutException.class, e -> (SupervisorStrategy.Directive) SupervisorStrategy.restart())
                    .build());

    public static Props props(ActorRef actorRef, WSClient wsClient) {
        return Props.create(SupervisorActor.class, actorRef, wsClient);
    }

    public SupervisorActor(ActorRef actorRef, WSClient wsClient) {
        this.outActorRef = actorRef;
        youtubeApiClientActor = getContext().actorOf(YoutubeApiClientActor.props(wsClient));
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JsonNode.class, t -> {
                    outActorRef.tell(Json.parse("{hello:World}"), getSelf());
                })
                .match(YoutubeApiClientActor.SetWSClient.class, t -> youtubeApiClientActor.tell(t, getSender()))
                .match(YoutubeApiClientActor.FetchVideos.class, t -> youtubeApiClientActor.tell(t, getSender()))
                .match(YoutubeApiClientActor.GetViewCountByVideoId.class, t -> youtubeApiClientActor.tell(t, getSender()))
                .match(YoutubeApiClientActor.GetVideosJsonByChannelId.class, t -> youtubeApiClientActor.tell(t, getSender()))
                .match(YoutubeApiClientActor.GetChannelInformationByChannelId.class, t -> youtubeApiClientActor.tell(t, getSender()))
                .match(YoutubeApiClientActor.GetSentimentByVideoId.class, t -> youtubeApiClientActor.tell(t, getSender()))
                .build();
    }
}
