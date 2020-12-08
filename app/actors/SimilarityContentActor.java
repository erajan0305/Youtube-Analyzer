package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import models.POJO.SearchResults.SearchResults;
import scala.compat.java8.FutureConverters;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static akka.pattern.Patterns.ask;
import static java.util.Collections.reverseOrder;
import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.*;

/**
 * This actor handles the messages for fetching Similarity Stats for search keyword.
 *
 * @author Kishan Bhimani
 */
public class SimilarityContentActor extends AbstractActor {

    private final ActorRef sessionActor;

    /**
     * This class is a message type for fetching similarity content by keyword.
     *
     * @author Kishan Bhimani
     */
    public static class SimilarContentByKeyword {
        private final String userId;
        private final String keyword;

        /**
         * Instantiates a new Similar content by keyword.
         *
         * @param userId  the user id
         * @param keyword the keyword
         * @author Kishan Bhimani
         */
        public SimilarContentByKeyword(String userId, String keyword) {
            this.userId = userId;
            this.keyword = keyword;
        }
    }

    /**
     * Factory method for the {@link SimilarityContentActor}
     *
     * @param sessionActor the session actor
     * @return the props
     * @author Kishan Bhimani
     */
    public static Props props(ActorRef sessionActor) {
        return Props.create(SimilarityContentActor.class, sessionActor);
    }

    /**
     * Instantiates a new Similarity content actor.
     *
     * @param sessionActor the session actor
     * @author Kishan Bhimani
     */
    SimilarityContentActor(ActorRef sessionActor) {
        this.sessionActor = sessionActor;
    }

    /**
     * Message Handling for {@link SimilarityContentActor}
     *
     * @author Kishan Bhimani
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder().match(SimilarContentByKeyword.class, this::getSimilarityStats).build();
    }

    /**
     * This method calculates the similarity between the given search keyword and other words in the title of the
     * videos related to the search keyword and sends the similarity stats to the sender of the message.
     *
     * @param similarContentByKeyword the object holding the keyword for which similarity stats are to be found.
     * @author Kishan Bhimani
     */
    private void getSimilarityStats(SimilarContentByKeyword similarContentByKeyword) {
        String keyword = similarContentByKeyword.keyword;
        String userId = similarContentByKeyword.userId;
        final LinkedHashMap<String, Long> computedSimilarityStatsLinkedHashmap;

        computedSimilarityStatsLinkedHashmap = FutureConverters.toJava(
                ask(sessionActor, new SessionActor.GetUserSearchResults(userId), 1000))
                .thenApply(o -> (LinkedHashMap<String, SearchResults>) o)
                .toCompletableFuture()
                .thenApply(searchResultsLinkedHashMap -> {
                    SearchResults searchResults = searchResultsLinkedHashMap.get(keyword);
                    if (searchResults == null || (searchResults.getItems() == null || searchResults.getItems().size() == 0)) {
                        return null;
                    }
                    return searchResults
                            .getItems()
                            .stream()
                            .map(searchResultItem -> searchResultItem.getSnippet().getTitle())
                            .flatMap(title -> Arrays.stream(title.split("\\s+").clone()))       // split into words
                            .map(s -> s.replaceAll("[^a-zA-Z0-9]", ""))                      // discarding special characters
                            .filter(s -> !s.matches("[0-9]+"))                                  // discarding only number strings
                            .filter(s -> !s.isEmpty() && s.length() > 1)                          // accept only non empty string with length more than 1
                            .collect(toList());
                })
                .thenApply(tokens -> tokens != null ? tokens.stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.groupingBy(identity(), counting()))    // creates map of (unique words, count)
                        .entrySet().stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue(reverseOrder()))
                        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a,
                                LinkedHashMap::new)) : new LinkedHashMap<String, Long>()).join();

        getSender().tell(computedSimilarityStatsLinkedHashmap, getSelf());
    }
}
