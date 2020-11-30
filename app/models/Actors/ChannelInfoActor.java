package models.Actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class ChannelInfoActor extends AbstractActor {

    private final ActorRef youtubeApiClientActor;

    public static final class ChannelInfo {
        private final String channelId;

        public ChannelInfo(String channelId) {
            this.channelId = channelId;
        }
    }

    public static Props props(ActorRef youtubeApiClientActor) {
        return Props.create(ChannelInfoActor.class, youtubeApiClientActor);
    }

    public ChannelInfoActor(ActorRef youtubeApiClientActor) {
        this.youtubeApiClientActor = youtubeApiClientActor;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ChannelInfo.class, t ->
                        youtubeApiClientActor.tell(new YoutubeApiClientActor.GetChannelInformationByChannelId(t.channelId), getSender()))
                .build();
    }
}
