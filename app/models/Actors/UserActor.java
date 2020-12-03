package models.Actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import models.POJO.SearchResults.SearchResultItem;
import models.POJO.SearchResults.SearchResults;
import play.libs.Json;
import scala.compat.java8.FutureConverters;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import static akka.pattern.Patterns.ask;

public class UserActor extends AbstractActor {
    private String userId;
    private final LinkedHashMap<String, SearchResults> userSearchResultsBySearchKeywordHashMap = new LinkedHashMap<>();
    private final ActorRef supervisorActor;

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

    public static class UpdateSearchResultsRequest {
    }

    private void updateSearchResults() {
        Set<String> strings = this.userSearchResultsBySearchKeywordHashMap.keySet();
        strings.parallelStream().forEach(keyword -> {
            SearchResults keywordSearchResults = FutureConverters.toJava(
                    ask(supervisorActor, new YoutubeApiClientActor.FetchVideos(keyword), 5000))
                    .toCompletableFuture().thenApply(o -> (SearchResults) o).join();
            // TODO: think how to append only new data to existing data.
            // 1) Append everything (because any part of existing data might have been updated
            // 2) Append only new data (but how)?
            if (!keywordSearchResults.toString().equals(userSearchResultsBySearchKeywordHashMap.get(keyword).toString())) {
                List<SearchResultItem> tempSearchResultItem = userSearchResultsBySearchKeywordHashMap.get(keyword).getItems();
                tempSearchResultItem.addAll(keywordSearchResults.getItems());
            }
        });
        System.out.println("Sender in updateSearchResults: " + getSender());
        JsonNode jsonNode = Json.toJson(this.userSearchResultsBySearchKeywordHashMap);
        getSender().tell(jsonNode, getSelf());
    }

    public UserActor(String userId, ActorRef supervisorActor) {
        this.userId = userId;
        this.supervisorActor = supervisorActor;
    }

    public static Props props(String userId, ActorRef supervisorActor) {
        return Props.create(UserActor.class, userId, supervisorActor);
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
        }).match(UpdateSearchResultsRequest.class, t -> {
            System.out.println("Update Search Results");
            this.updateSearchResults();
        })
                .build();
    }
}
