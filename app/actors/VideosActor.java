package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class VideosActor extends AbstractActor {

    private final ActorRef supervisorActor;

    public static final class VideosList {
        private final String channelId;
        private final String keyword;

        public VideosList(String channelId, String keyword) {
            this.channelId = channelId;
            this.keyword = keyword;
        }
    }

    public static Props props(ActorRef supervisorActor) {
        return Props.create(VideosActor.class, supervisorActor);
    }

    public VideosActor(ActorRef supervisorActor) {
        this.supervisorActor = supervisorActor;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().
                match(VideosList.class, t ->
                        supervisorActor.tell(new YoutubeApiClientActor.GetVideosJsonByChannelId(t.channelId, t.keyword), getSender()))
                .build();
    }
}
