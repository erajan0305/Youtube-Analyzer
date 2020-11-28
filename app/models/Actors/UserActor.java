package models.Actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import models.POJO.SearchResults.SearchResults;

import java.util.LinkedHashMap;

public class UserActor extends AbstractActor {
    private String userId;
    private final LinkedHashMap<String, SearchResults> userSearchResultsBySearchKeywordHashMap = new LinkedHashMap<>();

    public static class AddSearchResult {
        public final String userId;
        public final String key;
        public SearchResults searchResults;

        public AddSearchResult(String userId, String key, SearchResults searchResults) {
            this.userId = userId;
            this.key = key;
            this.searchResults = searchResults;
        }
    }

    public static class GetSearchResults {
        public final String userId;

        public GetSearchResults(String userId) {
            this.userId = userId;
        }
    }

    public UserActor(String userId) {
        this.userId = userId;
    }

    public static Props props(String userId) {
        return Props.create(UserActor.class, userId);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(AddSearchResult.class, t -> {
            if (t.userId.equals(this.userId)) {
                userSearchResultsBySearchKeywordHashMap.put(t.key, t.searchResults);
            } else {
                throw new Exception("Unauthorized");
            }
        }).match(GetSearchResults.class, t -> {
            if (t.userId.equals(this.userId)) {
                System.out.println("\n\ntelling sender");
                getSender().tell(this.userSearchResultsBySearchKeywordHashMap, getSelf());
            } else {
                throw new Exception("Unauthorized");
            }
        }).build();
    }
}
