package models.Actors;

import akka.actor.AbstractActor;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class EmojiAnalyzerActor extends AbstractActor {

    private final ActorRef supervisorActor;

    private EmojiAnalyzerActor(ActorRef supervisorActor) {
        this.supervisorActor = supervisorActor;
    }

    public static Props props(ActorRef supervisorActor) {
        return Props.create(EmojiAnalyzerActor.class, supervisorActor);
    }


    public static final class GetAnalysisResult {
        private final String videoId;

        public GetAnalysisResult(String videoId) {
            this.videoId = videoId;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GetAnalysisResult.class, this::onGetAnalysisResult)
                .build();
    }

    private void onGetAnalysisResult(GetAnalysisResult getAnalysisResult) {
        supervisorActor.tell(new YoutubeApiClientActor.GetSentimentByVideoId(getAnalysisResult.videoId), getSender());
    }
}
