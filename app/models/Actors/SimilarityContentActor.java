package models.Actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import models.POJO.SearchResults.SearchResults;
import scala.compat.java8.FutureConverters;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static akka.pattern.Patterns.ask;
import static java.util.Collections.reverseOrder;
import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.*;

public class SimilarityContentActor extends AbstractActor {

    private final ActorRef sessionActor;
    public static class SimilarContentByKeyword{
        private final String userId;
        private final String keyword;

        public SimilarContentByKeyword(String userId, String keyword) {
            this.userId = userId;
            this.keyword = keyword;
        }
    }

    public static Props props(ActorRef sessionActor){
        return Props.create(SimilarityContentActor.class, sessionActor);
    }

    SimilarityContentActor(ActorRef sessionActor){
        this.sessionActor = sessionActor;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(SimilarContentByKeyword.class , t-> this.getSimilarityStats(t)).build();
    }

    private void getSimilarityStats(SimilarContentByKeyword similarContentByKeyword) {
        String keyword = similarContentByKeyword.keyword;
        String userId = similarContentByKeyword.userId;
        final LinkedHashMap<String, Long> computedSimilarityStatsLinkedHashmap;

        LinkedHashMap<String, SearchResults> searchResultsLinkedHashMap = FutureConverters.toJava(
                ask(sessionActor, new SessionActor.GetUserSearchResults(userId), 1000))
                .thenApply(o -> (LinkedHashMap<String, SearchResults>) o)
                .toCompletableFuture()
                .join();
        
        SearchResults searchResults = searchResultsLinkedHashMap.get(keyword);
        if (searchResults == null || (searchResults.getItems() == null || searchResults.getItems().size() == 0)) {
            getSender().tell(new LinkedHashMap<String, Long>(), getSelf());
        }
        List<String> tokens = searchResults
                .getItems()
                .stream()
                .map(searchResultItem -> searchResultItem.getSnippet().getTitle())
                .flatMap(title -> Arrays.stream(title.split("\\s+").clone()))       // split into words
                .map(s -> s.replaceAll("[^a-zA-Z0-9]", ""))                      // discarding special characters
                .filter(s -> !s.matches("[0-9]+"))                                  // discarding only number strings
                .filter(s -> !s.isEmpty() && s.length() > 1)                          // accept only non empty string with length more than 1
                .collect(toList());

        computedSimilarityStatsLinkedHashmap = tokens.stream()
                .map(String::toLowerCase)
                .collect(Collectors.groupingBy(identity(), counting()))    // creates map of (unique words, count)
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(reverseOrder()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a,
                        LinkedHashMap::new));

        getSender().tell(computedSimilarityStatsLinkedHashmap, getSelf());
    }

}
