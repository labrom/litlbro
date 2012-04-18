package labrom.litlbro.data;


public interface SitePreferencesManager {
    
    public interface Delegate {
        void notifyJavascriptEnabled(boolean enabled);
    }
    
    /**
     * Asks whether Javascript should be enabled for a URL.
     * May or may not be asynchronous but the {@link Delegate} will be notified regardless.
     * @param url
     * @param d
     */
    void askWhetherJavascriptEnabled(String url, Delegate d);
    
    /**
     * Stores a preference for a URL, whether Javascript should be enabled.
     * Asynchronous.
     * @param url
     * @param javascriptEnabled
     */
    void recordPagePref(String url, boolean javascriptEnabled);
    
}
