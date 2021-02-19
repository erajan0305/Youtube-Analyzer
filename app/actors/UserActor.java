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

/**
 * Documentation for the {@link UserActor} class
 *
 * @author Kishan Bhimani, Rajan Shah and Umang Patel
 */
public class UserActor extends AbstractActor {
    private String userId;
    private final LinkedHashMap<String, SearchResults> userSearchResultsBySearchKeywordHashMap = new LinkedHashMap<>();
    private final ActorRef supervisorActor;

    /**
     * Message protocol for storing search results of a particular user identified by a user ID.
     *
     * @author Kishan Bhimani
     */
    public static class AddSearchResult {
        public final String userId;
        public final String key;
        public SearchResults searchResults;

        /**
         * Constructor of the {@link AddSearchResult} message protocol
         *
         * @param userId        is the user ID
         * @param key           is the search keyword
         * @param searchResults is the search results obtained from the Youtube API.
         * @author Kishan Bhimani
         */
        public AddSearchResult(String userId, String key, SearchResults searchResults) {
            this.userId = userId;
            this.key = key;
            this.searchResults = searchResults;
        }
    }

    /**
     * Message protocol for fetching the search results given a user ID.
     *
     * @author Umang Patel
     */
    public static class GetSearchResults {
        public final String userId;

        /**
         * Constructor for the {@link GetSearchResults} message protocol.
         *
         * @param userId is the user ID.
         * @author Umang Patel
         */
        public GetSearchResults(String userId) {
            this.userId = userId;
        }
    }

    /**
     * Message protocol for requests pertaining to updation of the search results.
     *
     * @author Rajan Shah
     */
    public static class UpdateSearchResultsRequest {
    }

    /**
     * Helper method for updating the search results of a particular user.
     *
     * @author Rajan Shah
     */
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

    /**
     * Constructor of the {@link UserActor}.
     *
     * @param userId          is the user ID.
     * @param supervisorActor is the supervisor actor supervising this actor.
     * @author Kishan Bhimani
     */
    public UserActor(String userId, ActorRef supervisorActor) {
        this.userId = userId;
        this.supervisorActor = supervisorActor;
    }

    /**
     * Factory method for instantiating the {@link UserActor}
     *
     * @param userId          is the user ID.
     * @param supervisorActor is the supervisor actor supervising this actor.
     * @return actor configuration in the form of {@link Props} object
     * @author Umang Patel
     */
    public static Props props(String userId, ActorRef supervisorActor) {
        return Props.create(UserActor.class, userId, supervisorActor);
    }

    /**
     * Message handling method for the {@link UserActor}.
     * Overridden from the {@link AbstractActor} class.
     *
     * @author Umang Patel
     */
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
