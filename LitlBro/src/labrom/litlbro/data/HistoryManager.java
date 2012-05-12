package labrom.litlbro.data;

import java.util.List;

import labrom.litlbro.suggestion.Suggestion;

public interface HistoryManager {
    
    /**
     * Asynchronous.
     */
    void refresh();
    
    /**
     * Asynchronous.
     * @param url
     * @param title
     */
    void recordUrl(String url, String title);
    
    /**
     * Asynchronous.
     * @param url
     */
    void recordUrlIfPresent(String url);
    
    /**
     * Updating cache is synchronous. The rest is asynchronous.
     * @param host
     */
    void removeHost(String host, boolean removeFromShortcuts, boolean removeFromSuggestions);
    
    /**
     * Updating cache is synchronous. The rest is asynchronous.
     * @param host
     */
    void reinstate(String host);

    /**
     * Updating cache is synchronous. The rest is asynchronous.
     * @param url
     */
    void starUrl(String url);
    /**
     * Updating cache is synchronous. The rest is asynchronous.
     * @param url
     */
    void unstarUrl(String url);
    
    /**
     * Updating cache is synchronous. The rest is asynchronous.
     * @param url
     */
    boolean isStarred(String url);
    
    /**
     * Asynchronous.
     * @param host
     */
    void unstarHost(final String host);

    /**
     * Asynchronous.
     * @param host
     */
    void starHost(final String host);

    List<History> getMostPopularSites(int nb);
    List<History> getStarredSites(int nb);

    /**
     * Finds suggestions out of history.
     * @param query
     * @return
     */
    List<Suggestion> getSuggestions(String query);



}
