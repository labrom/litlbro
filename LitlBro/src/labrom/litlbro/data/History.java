package labrom.litlbro.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import labrom.litlbro.util.UrlUtil;
import android.content.ContentValues;
import android.database.Cursor;

public class History extends ActiveRecord implements Comparable<Object> {

    /*
     * V1
    public static final String CREATE_STMT =
            "CREATE TABLE history (_id INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", host TEXT" +
                ", url TEXT" +
                ", title TEXT" +
                ", created INT" +
                ", last_viewed INT" +
                ", starred INT" +
                ", is_starred TINYINT" +
                ", nb_views INT" +
                ", popularity DOUBLE" +
                ");" +
            "CREATE INDEX history_host ON history(host);" +
            "CREATE INDEX history_url ON history(url);";
     */

    /*
     * V2
     */
    public static final String CREATE_STMT =
            "CREATE TABLE history (_id INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", host TEXT" +
                ", url TEXT" +
                ", title TEXT" +
                ", created INT" +
                ", last_viewed INT" +
                ", starred INT" +
                ", is_starred TINYINT" +
                ", nb_views INT" +
                ", popularity DOUBLE" +
                ", hide_shortcut TINYINT" +
                ", hide_suggestion TINYINT" +
                ");" +
            "CREATE INDEX history_host ON history(host);" +
            "CREATE INDEX history_url ON history(url);"+
            "CREATE INDEX history_hide_shortcut ON history(hide_shortcut);" +
            "CREATE INDEX history_hide_suggestion ON history(hide_suggestion);";


    /**
     * Upgrade statements from version 1 to 2.
     * Running all statements at once separated by a semi-column doesn't work, they need to be run separately.
     */
    public static final List<String> UPGRADE_STMT_1_TO_2 = Collections.unmodifiableList(Arrays.asList(
            "ALTER TABLE history ADD COLUMN hide_shortcut TINYINT DEFAULT 0", 
            "ALTER TABLE history ADD COLUMN hide_suggestion TINYINT DEFAULT 0",
            "CREATE INDEX history_hide_shortcut ON history(hide_shortcut)",
            "CREATE INDEX history_hide_suggestion ON history(hide_suggestion)"
    ));
    
    
    public String host;
    public String url;
    public String title;
    private int created = nowSeconds();
    public boolean isStarred;
    public int starred;
    public boolean hiddenFromShortcuts;
    public boolean hiddenFromSuggestions;
    
    private int lastViewed;
    private double popularity; // Popularity is actually the average of views timestamps (in seconds)
    private int nbViews = 0;
    
    public History() {
        
    }
    
    public History(String url) {
        this.url = url;
        this.host = UrlUtil.getHost(url);
        recordLastViewed();
    }
    
    @Override
    public String getTableName() {
        return "history";
    }
    
    public void recordLastViewed() {
        lastViewed = nowSeconds();
        popularity = (popularity * nbViews + lastViewed) / ++ nbViews;
    }
    

    

    /**
     * One mandatory column: host.
     */
    @Override
    protected void hydrateFromCursor(Cursor c) {
        host = c.getString(c.getColumnIndexOrThrow("host"));

        int col = -1;
        
        col = c.getColumnIndex("title");
        if(col >= 0)
            title = c.getString(col);
        
        col = c.getColumnIndex("url");
        if(col >= 0)
            url = c.getString(col);
        
        col = c.getColumnIndex("created");
        if(col >= 0)
            created = c.getInt(col);
        
        col = c.getColumnIndex("is_starred");
        if(col >= 0)
            isStarred = c.getShort(col) > 0;
        
        col = c.getColumnIndex("starred");
        if(col >= 0)
            starred = c.getInt(col);
        
        col = c.getColumnIndex("last_viewed");
        if(col >= 0)
            lastViewed = c.getInt(col);
        
        col = c.getColumnIndex("nb_views");
        if(col >= 0)
            nbViews = c.getInt(col);
        
        col = c.getColumnIndex("popularity");
        if(col >= 0)
            popularity = c.getDouble(col);
        
        col = c.getColumnIndex("hide_shortcut");
        if(col >= 0)
            hiddenFromShortcuts = c.getShort(col) > 0;
            
        col = c.getColumnIndex("hide_suggestion");
        if(col >= 0)
            hiddenFromSuggestions = c.getShort(col) > 0;
    }

    @Override
    protected void populateForUpdate(ContentValues v) {
        populateFull(v);
    }

    @Override
    protected void populateFull(ContentValues v) {
        v.put("host", host);
        v.put("url", url);
        v.put("title", title);
        v.put("created", created);
        v.put("last_viewed", lastViewed);
        v.put("starred", starred);
        v.put("is_starred", isStarred);
        v.put("nb_views", nbViews);
        v.put("popularity", popularity);
        v.put("hide_shortcut", hiddenFromShortcuts);
        v.put("hide_suggestion", hiddenFromSuggestions);
    }

    /**
     * Compares using weighted popularity.
     * @see #getWeightedPopularity()
     */
    @Override
    public int compareTo(Object another) {
        if(!(another instanceof History))
            return -1;
        History h = (History)another;
        return (int)(h.getWeightedPopularity() - getWeightedPopularity());
    }

    private double getWeightedPopularity() {
        double now = nowSeconds();
        double nbHours = (now - popularity) / 3600;
        double weight = 1 / Math.log(nbHours + Math.E);
        return popularity * nbViews * weight;
    }

    public void star() {
        isStarred = true;
        starred = nowSeconds();
    }
    
    public void unstar() {
        isStarred = false;
        starred = 0;
    }



}
