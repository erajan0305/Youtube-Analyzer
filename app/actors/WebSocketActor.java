package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.Duration;

/**
 * Documentation of {@link WebSocketActor}
 *
 * @author Kishan Bhimani, Rajan Shah and Umang Patel
 */
public class WebSocketActor extends AbstractActor {

    private final ActorRef webSocketResponseActor;
    private final ActorRef userActor;

    /**
     * Factory method for instantiating the {@link WebSocketActor}.
     *
     * @param webSocketResponseActor is the response actor for the websocket.
     * @param userActor              is the user actor associated with the websocket.
     * @return the actor configuration in the form of {@link Props} object.
     * @author Umang Patel
     */
    public static Props props(ActorRef webSocketResponseActor, ActorRef userActor) {
        return Props.create(WebSocketActor.class, webSocketResponseActor, userActor);
    }

    /**
     * Constructor of the {@link WebSocketActor}.
     *
     * @param webSocketResponseActor is the response actor for the websocket.
     * @param userActor              is the user actor associated with the websocket
     * @author Rajan Shah
     */
    public WebSocketActor(ActorRef webSocketResponseActor, ActorRef userActor) {
        this.webSocketResponseActor = webSocketResponseActor;
        this.userActor = userActor;
        getContext().getSystem()
                .scheduler()
                .scheduleWithFixedDelay(Duration.ZERO, Duration.ofSeconds(30),
                        userActor, new UserActor.UpdateSearchResultsRequest(), getContext().getSystem().getDispatcher(), getSelf());
    }

    /**
     * Message handling method for the {@link WebSocketActor}.
     * Overridden from the {@link AbstractActor} class.
     *
     * @author Kishan Bhimani
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JsonNode.class, t -> {
                    this.webSocketResponseActor.tell(t, getSelf());
                }).build();
    }
}
