package labrom.litlbro.data;

import android.os.AsyncTask;

import java.util.Map;
import java.util.WeakHashMap;

import labrom.litlbro.util.UrlUtil;

public class DBSitePreferencesManager implements SitePreferencesManager {
    
    private final Database db;
    private final Map<String, Boolean> cache = new WeakHashMap<String, Boolean>();
    
    public DBSitePreferencesManager(Database db) {
        this.db = db;
    }

    @Override
    public void askWhetherJavascriptEnabled(String url, Delegate d) {
        String host = UrlUtil.getHost(url);
        Boolean enabled = cache.get(host);
        if(enabled != null) {
            d.notifyJavascriptEnabled(enabled);
            return;
        }
        
        new AsyncTask<PrefsNotificationJob, Void, PrefsNotificationJob>() {

            @Override
            protected PrefsNotificationJob doInBackground(PrefsNotificationJob... params) {
                PrefsNotificationJob job = params[0];
                job.execute();
                return job;
            }
            
            @Override
            protected void onPostExecute(PrefsNotificationJob job) {
                job.notifyDelegate();
            }
            
        }.execute(new PrefsNotificationJob(d, host));
    }
    
    private class PrefsNotificationJob {
        Delegate delegate;
        String host;
        boolean jsEnabled;
        
        public PrefsNotificationJob(Delegate d, String host) {
            this.delegate = d;
            this.host = host;
        }
        
        void execute() {
            SitePrefs prefs = loadPrefs(this.host, true);
            if(prefs != null)
                this.jsEnabled = prefs.isJavascriptEnabled();
        }
        
        void notifyDelegate() {
            this.delegate.notifyJavascriptEnabled(this.jsEnabled);
        }
    }

    @Override
    public void recordPagePref(String url, boolean javascriptEnabled) {
        String host = UrlUtil.getHost(url);
        cachePrefs(host, javascriptEnabled);
        new AsyncTask<PrefsStoreJob, Void, Void>() {

            @Override
            protected Void doInBackground(PrefsStoreJob... params) {
                params[0].execute();
                return null;
            }
            
        }.execute(new PrefsStoreJob(host, javascriptEnabled));
    }
    
    private class PrefsStoreJob {
        String host;
        boolean jsEnabled;
        
        public PrefsStoreJob(String host, boolean jsEnabled) {
            this.host = host;
            this.jsEnabled = jsEnabled;
        }
        
        void execute() {
            SitePrefs prefs = loadPrefs(host, false);
            if(prefs != null) {
                prefs.reportJavascriptEnabled(this.jsEnabled);
                prefs.update();
            } else {
                prefs = new SitePrefs();
                prefs.setHost(host);
                prefs.reportJavascriptEnabled(this.jsEnabled);
                persistPref(prefs);
            }
        }
    }

    void persistPref(SitePrefs sp) {
        db.persist(sp);
    }
    
    SitePrefs loadPrefs(String host, boolean putToCache) {
        SitePrefs prefs = db.getUnique(new SitePrefs(), "host=?", new String[] {host});
        if(prefs != null && putToCache)
            cachePrefs(host, prefs.isJavascriptEnabled());
        return prefs;
    }
    
    void cachePrefs(String host, boolean jsEnabled) {
        this.cache.put(host, jsEnabled);
    }
    
}
