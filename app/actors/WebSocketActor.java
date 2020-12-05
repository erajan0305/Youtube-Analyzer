package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.Duration;

public class WebSocketActor extends AbstractActor {

    private final ActorRef webSocketResponseActor;
    private final ActorRef userActor;

    public static Props props(ActorRef webSocketResponseActor, ActorRef userActor) {
        return Props.create(WebSocketActor.class, webSocketResponseActor, userActor);
    }

    public WebSocketActor(ActorRef webSocketResponseActor, ActorRef userActor) {
        this.webSocketResponseActor = webSocketResponseActor;
        this.userActor = userActor;
        getContext().getSystem()
                .scheduler()
                .scheduleWithFixedDelay(Duration.ZERO, Duration.ofSeconds(30),
                        userActor, new UserActor.UpdateSearchResultsRequest(), getContext().getSystem().getDispatcher(), getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JsonNode.class, t -> {
                    System.out.println("json message received");
                    this.webSocketResponseActor.tell(t, getSelf());
                }).build();
    }
}
