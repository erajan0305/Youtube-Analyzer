package models;

/**
 * Model Class for Search Input.
 *
 * @author Rajan Shah
 */
public class Search {
    /**
     * The Search keyword.
     */
    public String searchKeyword;

    /**
     * Instantiates a new Search.
     */
    public Search() {
    }

    /**
     * Instantiates a new Search.
     *
     * @param searchKeyword the search keyword
     */
    public Search(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }
}
