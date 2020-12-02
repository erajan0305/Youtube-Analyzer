package models.Actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import models.POJO.Comments.CommentResults;
import scala.compat.java8.FutureConverters;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static akka.pattern.Patterns.ask;

public class EmojiAnalyzerActor extends AbstractActor {

    private final ActorRef supervisorActor;

    private EmojiAnalyzerActor(ActorRef supervisorActor) {
        this.supervisorActor = supervisorActor;
    }

    public static Props props(ActorRef supervisorActor) {
        return Props.create(EmojiAnalyzerActor.class, supervisorActor);
    }


    public static final class GetComments {
        private final String videoId;

        public GetComments(String videoId) {
            this.videoId = videoId;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GetComments.class, this::onGetComments)
                .build();
    }

    private void onGetComments(GetComments getComments) {
        CompletableFuture<String> analysisResultFuture =
                FutureConverters.toJava(ask(supervisorActor, new YoutubeApiClientActor.GetSentimentByVideoId(getComments.videoId), 2000))
                        .thenApplyAsync(item -> (CompletableFuture<CommentResults>) item)
                        .toCompletableFuture()
                        .thenApplyAsync(CompletableFuture::join)
                        .thenApplyAsync(EmojiAnalyzer::getAnalysisResult);
        getSender().tell(analysisResultFuture, getSelf());
    }

    /**
     * Helper class for analyzing emojis.
     *
     * @author Umang J Patel
     */
    private static final class EmojiAnalyzer {

        /**
         * Set of Happy Emojis
         */
        private static final List<Emoji> HAPPY_EMOJI_SET = Arrays.asList(
                EmojiManager.getForAlias("smiley"),
                EmojiManager.getForAlias("heart_eyes"),
                EmojiManager.getForAlias("smile"),
                EmojiManager.getForAlias("joy"),
                EmojiManager.getForAlias("blush"),
                EmojiManager.getForAlias("grinning"),
                EmojiManager.getForAlias("grin"),
                EmojiManager.getForAlias("stuck_out_tongue_closed_eyes"),
                EmojiManager.getForAlias("relaxed")
        );

        /**
         * Set of Sad Emojis
         */
        private static final List<Emoji> SAD_EMOJI_SET = Arrays.asList(
                EmojiManager.getForAlias("pensive"),
                EmojiManager.getForAlias("disappointed"),
                EmojiManager.getForAlias("persevere"),
                EmojiManager.getForAlias("cry"),
                EmojiManager.getForAlias("sob"),
                EmojiManager.getForAlias("worried")
        );

        /**
         * Set of all the Emojis, Happy and Sad.
         */
        private static final List<Emoji> EMOJIS_SET = Stream.concat(
                HAPPY_EMOJI_SET.parallelStream(), SAD_EMOJI_SET.parallelStream())
                .collect(Collectors.toList());

        /**
         * Set of Happy Emojis in Unicode Format
         */
        private static final List<String> HAPPY_EMOJI_UNICODE_SET = HAPPY_EMOJI_SET
                .parallelStream()
                .map(Emoji::getUnicode).collect(Collectors.toList());

        /**
         * Process the Youtube comment stream.
         * 1. Extracting only emojis from the comments.
         * 2. Filtering emojis from our domain of interest (happy and sad)
         * 3. Joining all the comments into a single string
         *
         * @param commentStream represents the stream of Youtube comments
         * @return all the concatenated string consisting of only emojis.
         * @author Umang J Patel
         */
        private static String processCommentStream(Stream<String> commentStream) {
            return commentStream
                    .filter(comment -> EmojiParser.extractEmojis(comment).size() != 0)
                    .map(comment -> String.join("", EmojiParser.extractEmojis(comment)))
                    .map(comment -> EmojiParser.removeAllEmojisExcept(comment, EMOJIS_SET))
                    .filter(comment -> EmojiParser.extractEmojis(comment).size() != 0)
                    .collect(Collectors.joining(""));
        }

        /**
         * Encode every emoji to a sentiment (happy or sad).
         *
         * @param emoji represents the emoji
         * @return the string whether the emoji represents 'happy' or 'sad'
         * @author Umang J Patel
         */
        private static String encodeEmojiSentiment(String emoji) {
            String parsedEmoji = EmojiParser.parseToUnicode(emoji);
            if (HAPPY_EMOJI_UNICODE_SET.contains(parsedEmoji))
                return "happy";
            else
                return "sad";
        }

        /**
         * Generate the sentiment analysis report to an emoji (happy, sad or neutral).
         *
         * @param comments represents the comments of a Youtube video
         * @return the emoji representing the sentiment (happy, sad or neutral)
         * @author Umang J Patel
         */
        private static String generateReport(String comments) {
            Map<String, Long> emojiCounts = EmojiParser.extractEmojis(comments).parallelStream()
                    .collect(Collectors.groupingBy(EmojiAnalyzer::encodeEmojiSentiment,
                            Collectors.counting()));
            Long totalCounts = emojiCounts.values().parallelStream().reduce(0L, Long::sum);
            Map<String, Float> result = emojiCounts.entrySet().parallelStream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> (e.getValue() * 100.0f) / totalCounts));
            if (result.getOrDefault("happy", 0.0f) >= 70.0f)
                return EmojiManager.getForAlias("grin").getUnicode();
            else if (result.getOrDefault("sad", 0.0f) >= 70.0f)
                return EmojiManager.getForAlias("pensive").getUnicode();
            else
                return EmojiManager.getForAlias("neutral_face").getUnicode();
        }

        /**
         * This method processes the {@link CommentResults} to filter out the emojis.
         * Use {@link #getAnalysisResult(CommentResults)} to get sentiment analysis report of the comments.
         *
         * @param commentResults {@link CommentResults} object containing list of 100 Comments for video
         * @return String of filtered emojis.
         * @author Umang J Patel
         */
        private static String getCommentsString(CommentResults commentResults) {
            if (commentResults.getItems() == null)
                return "";  //Empty String
            Stream<String> commentStream = commentResults.getItems().parallelStream()
                    .map(commentResultItem -> commentResultItem.getCommentSnippet().getTopLevelComment().getSnippet().getTextOriginal().trim());
            return processCommentStream(commentStream);
        }

        /**
         * This method processes the {@link CommentResults} to generate sentiment analysis report of comments.
         *
         * @param commentResults {@link CommentResults} object containing list of 100 Comments for video
         * @return Sentiment emoji as a string (happy: grin emoji, sad: pensive emoji, neutral: neutral_face emoji).
         * @author Umang J Patel
         */
        private static String getAnalysisResult(CommentResults commentResults) {
            return generateReport(getCommentsString(commentResults));
        }
    }
}