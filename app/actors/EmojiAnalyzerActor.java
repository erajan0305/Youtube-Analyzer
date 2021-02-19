package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import models.POJO.Comments.CommentResults;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * EmojiAnalyzerActor documentation
 *
 * @author Umang Patel
 */
public class EmojiAnalyzerActor extends AbstractActor {

    /**
     * Factory method for the {@link EmojiAnalyzerActor}
     *
     * @return Actor configuration in the form of {@link Props}
     * @author Umang Patel
     */
    public static Props props() {
        return Props.create(EmojiAnalyzerActor.class);
    }

    /**
     * Protocol message for retrieving the analysis result.
     *
     * @author Umang Patel
     */
    public static final class GetAnalysis {
        private final CompletableFuture<CommentResults> commentResults;

        /**
         * Constructor of the {@link GetAnalysis} protocol message class that stores the Youtube comments for processing
         * @param commentResults is the Youtube comments that are to be processed.
         * @author Umang Patel
         */
        public GetAnalysis(CompletableFuture<CommentResults> commentResults) {
            this.commentResults = commentResults;
        }
    }

    /**
     * Message handling for the {@link EmojiAnalyzerActor}.
     * @author Umang Patel
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GetAnalysis.class, this::onGetAnalysis)
                .build();
    }

    /**
     * This method is called when it receives the {@link GetAnalysis} protocol message.
     * It processes the comments and generates the overall sentiment of the comments in the emoji form.
     *
     * @param getAnalysis is the protocol message
     * @author Umang Patel
     */
    private void onGetAnalysis(GetAnalysis getAnalysis) {
        CompletableFuture<String> result;
        if (getAnalysis != null && getAnalysis.commentResults != null) {
            result = getAnalysis.commentResults
                    .thenApplyAsync(EmojiAnalyzer::getAnalysisResult)
                    .exceptionallyAsync(throwable -> EmojiManager.getForAlias("neutral_face").getUnicode());
        } else {
            result = CompletableFuture.supplyAsync(() -> EmojiManager.getForAlias("neutral_face").getUnicode());

        }
        getSender().tell(result, getSender());
    }

    /**
     * Helper class for analyzing emojis.
     *
     * @author Umang J Patel
     */
    static final class EmojiAnalyzer {

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
         * @author Umang Patel
         */
        static String processCommentStream(Stream<String> commentStream) {
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
         * @author Umang Patel
         */
        static String encodeEmojiSentiment(String emoji) {
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
         * @author Umang Patel
         */
        static String generateReport(String comments) {
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
         * @author Umang Patel
         */
        static String getCommentsString(CommentResults commentResults) {
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
         * @author Umang Patel
         */
        static String getAnalysisResult(CommentResults commentResults) {
            return generateReport(getCommentsString(commentResults));
        }
    }
}
