package models.Actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class ChannelInfoActor extends AbstractActor {

    private final ActorRef supervisorActor;

    public static final class ChannelInfo {
        private final String channelId;

        public ChannelInfo(String channelId) {
            this.channelId = channelId;
        }
    }

    public static Props props(ActorRef supervisorActor) {
        return Props.create(ChannelInfoActor.class, supervisorActor);
    }

    public ChannelInfoActor(ActorRef supervisorActor) {
        this.supervisorActor = supervisorActor;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ChannelInfo.class, t ->
                        supervisorActor.tell(new YoutubeApiClientActor.GetChannelInformationByChannelId(t.channelId), getSender()))
                .build();
    }
}
