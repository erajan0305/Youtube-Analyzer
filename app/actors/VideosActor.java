package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

/**
 * This actor handles the messages for fetching videos.
 *
 * @author Rajan Shah
 */
public class VideosActor extends AbstractActor {

    /**
     * Reference to {@link SupervisorActor} to send messages to {@link YoutubeApiClientActor}
     */
    private final ActorRef supervisorActor;

    /**
     * This class is a message type for fetching videos list by channelId and keyword.
     *
     * @author Rajan Shah
     */
    public static final class VideosList {
        private final String channelId;
        private final String keyword;

        /**
         * Instantiates a new Videos list.
         *
         * @param channelId the channel id
         * @param keyword   the keyword
         * @author Rajan Shah
         */
        public VideosList(String channelId, String keyword) {
            this.channelId = channelId;
            this.keyword = keyword;
        }
    }

    /**
     * Factor method for the {@link VideosActor}.
     *
     * @param supervisorActor the supervisor actor
     * @return the props
     * @author Rajan Shah
     */
    public static Props props(ActorRef supervisorActor) {
        return Props.create(VideosActor.class, supervisorActor);
    }

    /**
     * Instantiates a new Videos actor.
     *
     * @param supervisorActor the supervisor actor
     * @author Rajan Shah
     */
    public VideosActor(ActorRef supervisorActor) {
        this.supervisorActor = supervisorActor;
    }

    /**
     * Message handling for the {@link VideosActor}.
     *
     * @author Rajan Shah
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder().
                match(VideosList.class, t ->
                        supervisorActor.tell(new YoutubeApiClientActor.GetVideosJsonByChannelId(t.channelId, t.keyword), getSender()))
                .build();
    }
}
