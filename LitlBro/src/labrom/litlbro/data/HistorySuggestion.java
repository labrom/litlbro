package labrom.litlbro.data;

import labrom.litlbro.suggestion.Suggestion;
import android.content.ContentValues;
import android.database.Cursor;

public class HistorySuggestion extends ActiveRecord implements Suggestion {
    
    public String searchable; // Used for writes only
    public String host;
    public String title;
    public String url;
    public boolean starred;
    
    public static final String CREATE_STMT =
            "CREATE VIRTUAL TABLE suggestions USING FTS3 (searchable, host, title, url);";

    @Override
    public String getTableName() {
        return "suggestions";
    }

    @Override
    protected void hydrateFromCursor(Cursor c) {
        int colTitle = c.getColumnIndex("title");
        if(colTitle >= 0)
            title = c.getString(colTitle);

        int colHost = c.getColumnIndex("host");
        if(colHost >= 0)
            host = c.getString(colHost);

        int colUrl = c.getColumnIndex("url");
        if(colUrl >= 0)
            url = c.getString(colUrl);
    }

    @Override
    protected void populateForUpdate(ContentValues v) {
        populateFull(v);
    }

    @Override
    protected void populateFull(ContentValues v) {
        v.put("searchable", searchable);
        v.put("host", host);
        v.put("title", title);
        v.put("url", url);
    }

    
    public void createSearchable() {
        String domain = null;
        String[] hostParts = this.host.split(".");
        if(hostParts.length == 3) {
            domain = hostParts[1];
        } else if(hostParts.length == 2) {
            domain = hostParts[0];
        }
        
        String lcTitle = this.title != null ? this.title.toLowerCase() : null;
        if(domain != null && lcTitle != null) {
            lcTitle = lcTitle.replace(domain, "");
        }
        this.searchable = (domain != null ? domain : this.host) + (lcTitle != null ? " " + lcTitle : "");
    }
    
    @Override
    public String getTitle() {
        return title;
    }
    
    @Override
    public String getUrl() {
        return this.url;
    }
    
    @Override
    public String getIconUrl() {
        return null;
    }
    

    @Override
    public boolean isSite() {
        return this.url != null;
    }
    
    @Override
    public boolean isStarred() {
        return starred;
    }
    
    @Override
    public int getIconResouceId() {
        return 0;
    }

    @Override
    public float getPriority() {
        return isStarred() ? 1.1f : 0.8f;
    }
    
    @Override
    public String toString() {
        return "History: " + (title != null && title.length() > 0 ? title : url);
    }
}
