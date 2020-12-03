package models.Actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WebSocketActor extends AbstractActor {

    private final ActorRef webSocketResponseActor;
    private final ActorRef userActor;
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public static Props props(ActorRef webSocketResponseActor, ActorRef userActor) {
        return Props.create(WebSocketActor.class, webSocketResponseActor, userActor);
    }

    public WebSocketActor(ActorRef webSocketResponseActor, ActorRef userActor) {
        this.webSocketResponseActor = webSocketResponseActor;
        this.userActor = userActor;
        executorService.scheduleAtFixedRate(this::askUser, 0, 30, TimeUnit.SECONDS);
    }

    private void askUser() {
        this.userActor.tell(UserActor.UpdateSearchResultsRequest.class, getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JsonNode.class, t -> {
                    this.webSocketResponseActor.tell(t, getSelf());
                }).build();
    }
}
