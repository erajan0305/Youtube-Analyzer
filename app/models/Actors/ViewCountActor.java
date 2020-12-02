package models.Actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class ViewCountActor extends AbstractActor {

    private final ActorRef supervisorActor;

    public static Props props(ActorRef supervisorActor) {
        return Props.create(ViewCountActor.class, supervisorActor);
    }

    private ViewCountActor(ActorRef supervisorActor) {
        this.supervisorActor = supervisorActor;
    }

    public static final class GetViewCount {
        private final String videoId;

        public GetViewCount(String videoId) {
            this.videoId = videoId;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GetViewCount.class, this::onGetViewCount)
                .build();
    }

    private void onGetViewCount (GetViewCount getViewCount) {
        supervisorActor.tell(new YoutubeApiClientActor.GetViewCountByVideoId(getViewCount.videoId), getSender());
    }
}
