package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import models.POJO.SearchResults.SearchResults;

import java.util.HashMap;

public class SessionActor extends AbstractActor {
    private final HashMap<String, ActorRef> activeUsers = new HashMap<>();

    public static class CreateUser {
        private final String userId;
        private final ActorRef supervisorActor;

        public CreateUser(String userId, ActorRef supervisorActor) {
            this.userId = userId;
            this.supervisorActor = supervisorActor;
        }
    }

    public static class GetUser {
        public final String userId;

        public GetUser(String userId) {
            this.userId = userId;
        }
    }

    public static class GetUserSearchResults {
        public final String userId;

        public GetUserSearchResults(String userId) {
            this.userId = userId;
        }
    }

    public static class AddSearchResultsToUser {
        public final String userId;
        public final String key;
        public final SearchResults searchResults;

        public AddSearchResultsToUser(String userId, String key, SearchResults searchResults) {
            this.userId = userId;
            this.key = key;
            this.searchResults = searchResults;
        }
    }

    public static Props props() {
        return Props.create(SessionActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(CreateUser.class, this::addUserToCurrentRecord)
                .match(AddSearchResultsToUser.class, this::addSearchResultsToUserRecord)
                .match(GetUser.class, this::getUser)
                .match(GetUserSearchResults.class, this::getUserSearchResults)
                .build();
    }

    private void addUserToCurrentRecord(CreateUser createUser) {
        ActorRef user = getContext().actorOf(UserActor.props(createUser.userId, createUser.supervisorActor));
        activeUsers.put(createUser.userId, user);
        System.out.println("Adding to Hashmap");
    }

    private void addSearchResultsToUserRecord(AddSearchResultsToUser addSearchResultsToUser) {
        System.out.println("Searching Hashmap");
        activeUsers.entrySet().forEach(System.out::println);
        ActorRef user = activeUsers.get(addSearchResultsToUser.userId);
        user.tell(new UserActor.AddSearchResult(addSearchResultsToUser.userId, addSearchResultsToUser.key, addSearchResultsToUser.searchResults), self());
    }

    private void getUser(GetUser getUser) {
        ActorRef user = activeUsers.get(getUser.userId);
        getSender().tell(user, getSelf());
    }

    private void getUserSearchResults(GetUserSearchResults getUserSearchResults) {
        ActorRef user = activeUsers.get(getUserSearchResults.userId);
        user.tell(new UserActor.GetSearchResults(getUserSearchResults.userId), getSender());
    }
}
