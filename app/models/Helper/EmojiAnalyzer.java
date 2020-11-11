package models.Helper;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Helper class for analyzing emojis.
 *
 * @author Umang J Patel
 */
public class EmojiAnalyzer {

    // Set of happy emojis
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

    // Set of sad emojis
    private static final List<Emoji> SAD_EMOJI_SET = Arrays.asList(
            EmojiManager.getForAlias("pensive"),
            EmojiManager.getForAlias("disappointed"),
            EmojiManager.getForAlias("persevere"),
            EmojiManager.getForAlias("cry"),
            EmojiManager.getForAlias("sob"),
            EmojiManager.getForAlias("worried")
    );

    // Set of all emojis of our domain of interest
    private static final List<Emoji> EMOJIS_SET = Stream.concat(
            HAPPY_EMOJI_SET.parallelStream(), SAD_EMOJI_SET.parallelStream())
            .collect(Collectors.toList());

    // Set of happy emojis in unicode format
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
    public static String processCommentStream(Stream<String> commentStream) {
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
    public static String encodeEmojiSentiment(String emoji) {
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
    public static String generateReport(String comments) {
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
}
