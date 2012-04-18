package labrom.litlbro.data;

import android.content.ContentValues;
import android.database.Cursor;


public class SitePrefs extends ActiveRecord {
    
    
    
    public static final String CREATE_STMT =
            "CREATE TABLE siteprefs (_id INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", host TEXT" +
                ", created INT" +
                ", last_updated INT" +
                ", javascript TINYINT" +
                ", javascript_last1 TINYINT" +
                ", javascript_last2 TINYINT" +
                ", javascript_last3 TINYINT" +
                ");" +
            "CREATE INDEX siteprefs_host ON siteprefs(host);";

    
    
    private String host;
    private int created;
    private int lastUpdated;
    private boolean javascriptEnabled;
    private boolean javascriptEnabledLast1;
    private boolean javascriptEnabledLast2;
    private boolean javascriptEnabledLast3;
    
    
    @Override
    public String getTableName() {
        return "siteprefs";
    }
    
    public SitePrefs() {
        this.created = (int)(System.currentTimeMillis() / 1000);
        lastUpdated = this.created;
    }
    
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
        lastUpdated = (int)(System.currentTimeMillis() / 1000);
    }

    public int getCreated() {
        return created;
    }

    public int getLastUpdated() {
        return lastUpdated;
    }

    public boolean isJavascriptEnabled() {
        return javascriptEnabled;
    }

    /**
     * Reports whether Javascript was enabled or not for this host.
     * Based on this result and possibly previous results, will affect the Javascript preference.
     * @param javascriptEnabled
     */
    public void reportJavascriptEnabled(boolean javascriptEnabled) {

//        javascriptEnabledLast3 = javascriptEnabledLast2;
//        javascriptEnabledLast2 = javascriptEnabledLast1;
//        javascriptEnabledLast1 = javascriptEnabled;
//        if(javascriptEnabledLast1 && javascriptEnabledLast2 && javascriptEnabledLast3)
//            this.javascriptEnabled = true;
//        else /*if(!javascriptEnabledLast1 && !javascriptEnabledLast2 && !javascriptEnabledLast3)*/
//            this.javascriptEnabled = false;
        this.javascriptEnabled = javascriptEnabled;
        
        lastUpdated = (int)(System.currentTimeMillis() / 1000);
    }









    @Override
    protected void populateForUpdate(ContentValues v) {
        populateFull(v);
    }
    
    @Override
    protected void populateFull(ContentValues v) {
        v.put("created", this.created);
        v.put("last_updated", this.lastUpdated);
        v.put("host", this.host);
        v.put("javascript", this.javascriptEnabled);
        v.put("javascript_last1", this.javascriptEnabledLast1);
        v.put("javascript_last2", this.javascriptEnabledLast2);
        v.put("javascript_last3", this.javascriptEnabledLast3);
    }
    
    @Override
    protected void hydrateFromCursor(Cursor c) {
        this.created = c.getInt(c.getColumnIndexOrThrow("created"));
        this.lastUpdated = c.getInt(c.getColumnIndexOrThrow("last_updated"));
        this.host = c.getString(c.getColumnIndexOrThrow("host"));
        this.javascriptEnabled = c.getShort(c.getColumnIndexOrThrow("javascript")) > 0;
        this.javascriptEnabledLast1 = c.getShort(c.getColumnIndexOrThrow("javascript_last1")) > 0;
        this.javascriptEnabledLast2 = c.getShort(c.getColumnIndexOrThrow("javascript_last2")) > 0;
        this.javascriptEnabledLast3 = c.getShort(c.getColumnIndexOrThrow("javascript_last3")) > 0;
    }
}
