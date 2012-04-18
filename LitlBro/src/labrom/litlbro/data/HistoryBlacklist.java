package labrom.litlbro.data;

import labrom.litlbro.util.UrlUtil;
import android.content.ContentValues;
import android.database.Cursor;

public class HistoryBlacklist extends ActiveRecord {

    /*
     * V2
     */
    public static final String CREATE_STMT =
            "CREATE TABLE history_blacklist (_id INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", host TEXT" +
                ", url TEXT" +
                ", created INT" +
                ", hide_shortcut TINYINT" +
                ", hide_suggestion TINYINT" +
                ", reinstated INT" +
                ");" +
            "CREATE INDEX history_blacklist_host ON history_blacklist(host);" +
            "CREATE INDEX history_blacklist_url ON history_blacklist(url);";

    public String host;
    public String url;
    private int created = nowSeconds();
    public int reinstated;
    public boolean hideShortcut;
    public boolean hideSuggestion;
    
    public HistoryBlacklist() {
        
    }
    
    public HistoryBlacklist(String url) {
        this.url = url;
        this.host = UrlUtil.getHost(url);
    }
    
    @Override
    public String getTableName() {
        return "history_blacklist";
    }
    
    public void reinstate() {
        reinstated = nowSeconds();
        hideShortcut = false;
        hideSuggestion = false;
    }
    

    /**
     * One mandatory column: host.
     */
    @Override
    protected void hydrateFromCursor(Cursor c) {
        host = c.getString(c.getColumnIndexOrThrow("host"));

        int col = -1;
        
        col = c.getColumnIndex("url");
        if(col >= 0)
            url = c.getString(col);
        
        col = c.getColumnIndex("created");
        if(col >= 0)
            created = c.getInt(col);

        col = c.getColumnIndex("hide_shortcut");
        if(col >= 0)
            hideShortcut = c.getShort(col) > 0;

        col = c.getColumnIndex("hide_suggestion");
        if(col >= 0)
            hideSuggestion = c.getShort(col) > 0;
            
        col = c.getColumnIndex("reinstated");
        if(col >= 0)
            reinstated = c.getInt(col);

    }

    @Override
    protected void populateForUpdate(ContentValues v) {
        populateFull(v);
    }

    @Override
    protected void populateFull(ContentValues v) {
        v.put("host", host);
        v.put("url", url);
        v.put("created", created);
        v.put("hide_shortcut", hideShortcut);
        v.put("hide_suggestion", hideSuggestion);
        v.put("reinstated", reinstated);
    }
    
    public static HistoryBlacklist getFromCursor(Cursor c) {
        HistoryBlacklist bl = new HistoryBlacklist();
        bl.hydrateFromCursor(c);
        return bl;
    }

}
