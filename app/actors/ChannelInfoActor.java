package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

/**
 * This actor handles the messages for fetching channel information.
 *
 * @author Rajan Shah
 */
public class ChannelInfoActor extends AbstractActor {

    /**
     * Reference to {@link SupervisorActor} to send messages to {@link YoutubeApiClientActor}
     */
    private final ActorRef supervisorActor;

    /**
     * This class is a message type for fetching channel information by channelId.
     *
     * @author Rajan Shah
     */
    public static final class ChannelInfo {

        private final String channelId;

        /**
         * Instantiates a new Channel info.
         *
         * @param channelId the channel id
         * @author Rajan Shah
         */
        public ChannelInfo(String channelId) {
            this.channelId = channelId;
        }
    }

    /**
     * Factory method for the {@link ChannelInfoActor}
     *
     * @param supervisorActor the supervisor actor
     * @return the props
     * @author Rajan Shah
     */
    public static Props props(ActorRef supervisorActor) {
        return Props.create(ChannelInfoActor.class, supervisorActor);
    }

    /**
     * Instantiates a new Channel info actor.
     *
     * @param supervisorActor the supervisor actor
     * @author Rajan Shah
     */
    public ChannelInfoActor(ActorRef supervisorActor) {
        this.supervisorActor = supervisorActor;
    }

    /**
     * Message handling for the {@link ChannelInfoActor}.
     *
     * @author Rajan Shah
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ChannelInfo.class, t ->
                        supervisorActor.tell(new YoutubeApiClientActor.GetChannelInformationByChannelId(t.channelId), getSender()))
                .build();
    }
}
