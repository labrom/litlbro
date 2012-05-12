package labrom.litlbro.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import labrom.litlbro.suggestion.Suggestion;
import android.database.Cursor;
import android.net.Uri;

public class DBHistoryManager implements HistoryManager {
    
    private final class HistoryBlacklistPersister implements Runnable {
        private final boolean removeFromSuggestions;
        private final boolean removeFromShortcuts;
        private final String host;

        private HistoryBlacklistPersister(String host, boolean removeFromShortcuts, boolean removeFromSuggestions) {
            this.removeFromSuggestions = removeFromSuggestions;
            this.removeFromShortcuts = removeFromShortcuts;
            this.host = host;
        }

        @Override
        public void run() {
            HistoryBlacklist bl = db.getUnique(new HistoryBlacklist(), "host=?", new String[] {host});
            if(bl != null) {
                boolean modified = bl.hideShortcut != removeFromShortcuts || bl.hideSuggestion != removeFromSuggestions;
                if(modified) {
                    bl.hideShortcut = removeFromShortcuts;
                    bl.hideSuggestion = removeFromSuggestions;
                    if(!removeFromShortcuts && !removeFromSuggestions)
                        bl.reinstate();
                    bl.update();
                }
            } else if(removeFromShortcuts || removeFromSuggestions) {
                bl = new HistoryBlacklist("http://" + host);
                bl.hideShortcut = removeFromShortcuts;
                bl.hideSuggestion = removeFromSuggestions;
                db.persist(bl);
            }
            
            // Flag previous history (the one used for shortcuts only) as hidden
            // Also set the hide_suggestion flag although it's not used as of now
            if(removeFromShortcuts || removeFromSuggestions) {
                List<History> history = db.query(new History(), "host=?", new String[] {host}, "last_viewed DESC").asList();
                for(History h : history) {
                    h.hiddenFromShortcuts = removeFromShortcuts;
                    h.hiddenFromSuggestions = removeFromSuggestions;
                    h.update();
                }
            }
            else if(bl.reinstated > 0) {
//                List<History> history = db.query(new History(), "last_viewed>? AND host=? AND (hide_suggestion=1 OR hide_shortcut=1)", new String[] {String.valueOf(bl.reinstated), host}, null).asList();
                List<History> history = db.query(new History(), "host=? AND (hide_suggestion=1 OR hide_shortcut=1)", new String[] {host}, null).asList();
                for(History h : history) {
                    h.hiddenFromShortcuts = false;
                    h.hiddenFromSuggestions = false;
                    h.update();
                }
            }
        }
    }

    final Database db;
    private final Executor executor = Executors.newFixedThreadPool(2);
    
    // A bunch of caches
    private final Set<String> starredUrls = new HashSet<String>();
    private final Set<String> starredHosts = Collections.synchronizedSet(new HashSet<String>());
    private final Set<String> unstarredHosts = Collections.synchronizedSet(new HashSet<String>());
    private final Set<String> blacklistedShortcuts = new HashSet<String>();
    private final Set<String> sessionBlacklistedShortcuts = new HashSet<String>();
    private final Set<String> blacklistedSuggestions = new HashSet<String>();
    
    public DBHistoryManager(Database db) {
        this.db = db;
        refresh();
    }
    
    @Override
    public void refresh() {
        executor.execute(new Runnable() {
            public void run() {
                Cursor c = null;
                
                // Load starred pages
                ActiveRecordList<History> histo = db.query(new History(), new String[] {"url"}, "is_starred=1", null, null, "popularity DESC", "50");
                c = histo.getCursor();
                try {
                    if(c.moveToFirst()) {
                        do {
                            starredUrls.add(c.getString(0));
                        } while(c.moveToNext());
                    }
                    
                } finally {
                    c.close();
                }
                
                // Load host blacklist
                List<HistoryBlacklist> blacklist = db.query(new HistoryBlacklist(), null, null, null).asList();
                for(HistoryBlacklist bl : blacklist) {
                    if(bl.hideShortcut)
                        blacklistedShortcuts.add(bl.host);
                    if(bl.hideSuggestion)
                        blacklistedSuggestions.add(bl.host);
                }
                
            }
        });
    }

    

    @Override
    public void recordUrl(final String url, final String title) {
        if(url == null)
            return;
        doRecordUrl(url, title, true);
    }

    @Override
    public void recordUrlIfPresent(String url) {
        if(url == null)
            return;
        doRecordUrl(url, null, false);
    }
    
    private void doRecordUrl(final String url, final String title, final boolean createIfNotPresent) {
        executor.execute(new Runnable() {
            public void run() {
                History existing = db.getUnique(new History(), "url=?", new String[] {url});
                if(existing != null) {
                    existing.recordLastViewed();
                    existing.hiddenFromShortcuts = blacklistedShortcuts.contains(existing.host);
                    existing.hiddenFromSuggestions = blacklistedSuggestions.contains(existing.host);
                    existing.update();
                } else if(createIfNotPresent && !isGoogleBaseUrl(url)) {
                    // Create new history entry
                    History h = new History(url);
                    h.title = title;
                    boolean blacklistedSuggestion = blacklistedSuggestions.contains(h.host);
                    h.hiddenFromShortcuts = blacklistedShortcuts.contains(h.host);
                    db.persist(h);
                    
                    // Create history suggestion
                    // If host is blacklisted, stop recording suggestions
                    if(!blacklistedSuggestion) {
                        HistorySuggestion sugg = new HistorySuggestion();
                        sugg.url = h.url;
                        sugg.host = h.host;
                        sugg.title = h.title;
                        sugg.createSearchable();
                        db.persist(sugg);
                    }
                }
                
            }

            /**
             * FIXME This needs to be reworked.
             * @param url
             * @return
             */
            private boolean isGoogleBaseUrl(String url) {
                return "www.google.com".equals(Uri.parse(url).getHost());
            }
        });
    }
    


    @Override
    public void starUrl(final String url) {
        if(url == null)
            return;
        starredUrls.add(url);
        executor.execute(new Runnable() {
            public void run() {
                History existing = db.getUnique(new History(), "url=?", new String[] {url});
                if(existing != null && !existing.isStarred) {
                    existing.star();
                    existing.update();
                }
            }
        });
    }
    @Override
    public void unstarUrl(final String url) {
        if(url == null)
            return;
        starredUrls.remove(url);
        executor.execute(new Runnable() {
            public void run() {
                History existing = db.getUnique(new History(), "url=?", new String[] {url});
                if(existing != null && existing.isStarred) {
                    existing.unstar();
                    existing.update();
                }
            }
        });
    }
    @Override
    public void starHost(final String host) {
        if(host == null)
            return;
        starredHosts.add(host);
        unstarredHosts.remove(host);
        executor.execute(new Runnable() {
            public void run() {
                List<History> history = db.query(new History(), null, "is_starred=0 AND host=?", new String[] {host}, null, "created DESC", null).asList();
                for(History h : history) {
                    starredUrls.add(h.url);
                    h.star();
                    h.update();
                }
                starredHosts.remove(host);
            }
        });
    }
    @Override
    public void unstarHost(final String host) {
        if(host == null)
            return;
        starredHosts.remove(host);
        unstarredHosts.add(host);
        executor.execute(new Runnable() {
            public void run() {
                List<History> history = db.query(new History(), null, "is_starred=1 AND host=?", new String[] {host}, null, "created DESC", null).asList();
                for(History h : history) {
                    starredUrls.remove(h.url);
                    h.unstar();
                    h.update();
                }
                unstarredHosts.remove(host);
            }
        });
    }
    
    @Override
    public boolean isStarred(String url) {
        if(url == null)
            return false;
        History existing = db.getUnique(new History(), "url=?", new String[] {url});
        return existing != null && existing.isStarred;
    }
    
    @Override
    public void removeHost(final String host, final boolean removeFromShortcuts, final boolean removeFromSuggestions) {
        if(host == null || (!removeFromShortcuts && !removeFromSuggestions))
            return;
        
        if(removeFromSuggestions)
            blacklistedSuggestions.add(host);

        if(removeFromShortcuts) {
            blacklistedShortcuts.add(host);
            sessionBlacklistedShortcuts.add(host);
        }

        executor.execute(new HistoryBlacklistPersister(host, removeFromShortcuts, removeFromSuggestions));
    }
    
    @Override
    public void reinstate(final String host) {
        if(host == null)
            return;
        blacklistedShortcuts.remove(host);
        blacklistedSuggestions.remove(host);
        executor.execute(new HistoryBlacklistPersister(host, false, false));
    }
    

    @Override
    public List<History> getMostPopularSites(int nb) {
        // Query all history grouped by host
        // Popularity is a weighted average (URLs with many occurrences weigh more than those with fewer occurrences)
        List<History> popularSites = db.query(new History(), 
                new String[] {"host", "max(is_starred) AS is_starred", "sum(popularity * nb_views) / sum(nb_views) AS popularity", "sum(nb_views) AS nb_views", "max(last_viewed) AS last_viewed"}, // projection 
                "hide_shortcut=0", null, // selection + args
                "host", // group by
                "last_viewed DESC", // order by
                String.valueOf(Math.max(1, nb) + sessionBlacklistedShortcuts.size()) // limit
                ).asList();
        // This is needed because we might still be in the process of flagging history
        if(!sessionBlacklistedShortcuts.isEmpty() || !starredHosts.isEmpty() || !unstarredHosts.isEmpty()) {
            for(Iterator<History> iter = popularSites.iterator(); iter.hasNext(); ) {
                History h = iter.next();
                if(sessionBlacklistedShortcuts.contains(h.host)) {
                    iter.remove();
                    continue;
                }
                if(unstarredHosts.contains(h.host))
                    h.starred = 0;
                else if(starredHosts.contains(h.host))
                    h.starred = 1;
            }
        }
        Collections.sort(popularSites); // Natural order
        return popularSites;
    }

    @Override
    public List<History> getStarredSites(int nb) {
        List<History> starredSites = db.query(new History(),
                new String[] {"host", "sum(is_starred) AS nb_starred", "sum(popularity * nb_views) / sum(nb_views) AS popularity", "sum(nb_views) AS nb_views", "max(last_viewed) AS last_viewed"}, // projection 
                "hide_shortcut=0", null, // selection + args
                "host", // group by
                "nb_starred>0", // having
                "last_viewed DESC", // order by
                String.valueOf(Math.max(1, nb) + sessionBlacklistedShortcuts.size()) // limit
                ).asList();
        Collections.sort(starredSites); // Natural order
        return starredSites;
    }

    @Override
    public List<Suggestion> getSuggestions(String query) {
        if(query == null)
            return Collections.emptyList();
        List<Suggestion> suggestions = new ArrayList<Suggestion>(db.query(new HistorySuggestion(), 
                "searchable MATCH ?", new String[] {query + "*"}, null).asList());
        for(Suggestion sugg : suggestions) {
            if(starredUrls.contains(sugg.getUrl()))
                ((HistorySuggestion)sugg).starred = true;
        }
        return suggestions;
    }
    
}
