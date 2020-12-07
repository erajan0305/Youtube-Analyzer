package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import models.POJO.SearchResults.SearchResultItem;
import models.POJO.SearchResults.SearchResults;
import play.libs.Json;
import scala.compat.java8.FutureConverters;

import java.util.ArrayList;
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
            if (keywordSearchResults == null || userSearchResultsBySearchKeywordHashMap.get(keyword) == null) {
                System.out.println("Results Null Pointer");
                JsonNode jsonNode = Json.toJson("");
                getSender().tell(jsonNode, getSelf());
                return;
            }
            List<SearchResultItem> updatedList = keywordSearchResults.getItems();
            List<SearchResultItem> existingList = userSearchResultsBySearchKeywordHashMap.get(keyword).getItems();
            List<SearchResultItem> existingListUpdate = new ArrayList<>();
            List<SearchResultItem> newList = new ArrayList<>();
            if (updatedList == null || existingList == null) {
                System.out.println("Items Null Pointer");
                JsonNode jsonNode = Json.toJson("");
                getSender().tell(jsonNode, getSelf());
                return;
            }
            for (SearchResultItem searchResultItem : existingList) {
                for (SearchResultItem updatedItem : updatedList) {
                    if (searchResultItem.getId().getVideoId().equals(updatedItem.getId().getVideoId())) {
                        if (!searchResultItem.toString().equals(updatedItem.toString())) {
                            newList.add(updatedItem);
                        } else {
                            newList.add(searchResultItem);
                        }
                        existingListUpdate.add(searchResultItem);
                        updatedList.remove(updatedItem);
                        break;
                    }
                }
            }
            existingList.removeAll(existingListUpdate);
            newList.addAll(existingList);
            newList.addAll(updatedList);
            SearchResults updatedSearchResults = new SearchResults();
            updatedSearchResults.setItems(newList);
            this.userSearchResultsBySearchKeywordHashMap.put(keyword, updatedSearchResults);
        });
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
                getSender().tell(this.userSearchResultsBySearchKeywordHashMap, getSelf());
            } else {
                throw new Exception("Unauthorized");
            }
        }).match(UpdateSearchResultsRequest.class, t -> {
            if (this.userSearchResultsBySearchKeywordHashMap.size() > 0) {
                this.updateSearchResults();
            }
        }).build();
    }
}
