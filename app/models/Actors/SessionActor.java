package models.Actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import models.POJO.SearchResults.SearchResults;

import java.util.HashMap;

public class SessionActor extends AbstractActor {
    private final HashMap<String, ActorRef> activeUsers = new HashMap<>();

    public static class CreateUser {
        public final String userId;

        public CreateUser(String userId) {
            this.userId = userId;
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
                .match(CreateUser.class, t -> this.addUserToCurrentRecord(t))
                .match(AddSearchResultsToUser.class, t -> this.addSearchResultsToUserRecord(t))
                .match(GetUser.class, t -> this.getUser(t))
                .match(GetUserSearchResults.class, t-> this.getUserSearchResults(t))
                .build();
    }

    private void addUserToCurrentRecord(CreateUser createUser) {
        ActorRef user = getContext().actorOf(UserActor.props(createUser.userId));
        activeUsers.put(createUser.userId, user);
        System.out.println("\n\nAdding to Hashmap");
        activeUsers.entrySet().forEach(System.out::println);
    }

    private void addSearchResultsToUserRecord(AddSearchResultsToUser addSearchResultsToUser) {
        System.out.println("\n\nSearching Hashmap");
        activeUsers.entrySet().forEach(System.out::println);
        ActorRef user = activeUsers.get(addSearchResultsToUser.userId);
        user.tell(new UserActor.AddSearchResult(addSearchResultsToUser.userId, addSearchResultsToUser.key, addSearchResultsToUser.searchResults), self());
    }

    private void getUser(GetUser getUser) {
        ActorRef user = activeUsers.get(getUser.userId);
        getSender().tell(user,getSelf());
    }

    private void getUserSearchResults(GetUserSearchResults getUserSearchResults){
        ActorRef user = activeUsers.get(getUserSearchResults.userId);
        user.tell(new UserActor.GetSearchResults(getUserSearchResults.userId), getSender());
//        pipe(FutureConverters.toJava(
//                ask(user, new UserActor.GetSearchResults(getUser.userId), 1000))
//                .thenApply(o -> (LinkedHashMap<String, SearchResults>) o)
//                .toCompletableFuture(), context().dispatcher()).to(sender());
//
    }

}
