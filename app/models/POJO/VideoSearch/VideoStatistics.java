package models.POJO.VideoSearch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Model Class for {@link VideoSearchResultItem} Statistics.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"viewCount", "likeCount", "dislikeCount", "favouriteCount", "commentCount"})

public class VideoStatistics {
    @JsonProperty("viewCount")
    private String viewCount;
    @JsonProperty("likeCount")
    private String likeCount;

    /**
     * Gets view count.
     *
     * @return the view count
     */
    public String getViewCount() {
        return viewCount;
    }

    /**
     * Sets view count.
     *
     * @param viewCount the view count
     */
    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    /**
     * Gets like count.
     *
     * @return the like count
     */
    public String getLikeCount() {
        return likeCount;
    }

    /**
     * Sets like count.
     *
     * @param likeCount the like count
     */
    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    /**
     * Gets dislike count.
     *
     * @return the dislike count
     */
    public String getDislikeCount() {
        return dislikeCount;
    }

    /**
     * Sets dislike count.
     *
     * @param dislikeCount the dislike count
     */
    public void setDislikeCount(String dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    /**
     * Gets favourite count.
     *
     * @return the favourite count
     */
    public String getFavouriteCount() {
        return favouriteCount;
    }

    /**
     * Sets favourite count.
     *
     * @param favouriteCount the favourite count
     */
    public void setFavouriteCount(String favouriteCount) {
        this.favouriteCount = favouriteCount;
    }

    /**
     * Gets comment count.
     *
     * @return the comment count
     */
    public String getCommentCount() {
        return commentCount;
    }

    /**
     * Sets comment count.
     *
     * @param commentCount the comment count
     */
    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    @JsonProperty("dislikeCount")
    private String dislikeCount;
    @JsonProperty("favouriteCount")
    private String favouriteCount;
    @JsonProperty("commentCount")
    private String commentCount;

    @Override
    public String toString() {
        return "VideoStatistics{" +
                "viewCount='" + viewCount + '\'' +
                ", likeCount='" + likeCount + '\'' +
                ", dislikeCount='" + dislikeCount + '\'' +
                ", favouriteCount='" + favouriteCount + '\'' +
                ", commentCount='" + commentCount + '\'' +
                '}';
    }
}
