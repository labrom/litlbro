package labrom.litlbro.shortcut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import labrom.litlbro.data.History;

public class ShortcutList {
    
    private final List<History> history;
    private final List<History> starred;
    private final int targetNumber;
    private final List<History> merged = new ArrayList<History>();
    private boolean isMerged = false;
    
    /**
     * Creates a shortcuts list that contains both starred and non-starred sites.
     * @param historyList a sorted list of shortcuts - it can contain starred sites. this is considered as the base
     * list, it will be augmented with other starred sites if possible.
     * @param starredHosts a sorted list of shortcuts that contains only starred sites - some of those sites
     * might already be present in {@code historyList}.
     * @param targetNumber the target number of shortcuts to display.
     */
    public ShortcutList(List<History> historyList, List<History> starredHosts, int targetNumber) {
        this.history = historyList;
        this.starred = starredHosts;
        this.targetNumber = targetNumber;
    }
    
    
    
    public List<History> getFinalHistoryList() {
        ensureMerged();
        return Collections.unmodifiableList(merged);
    }



    /**
     * Basically, we'd like to make sure that the shortcuts list contains
     * at least half of starred sites (if we have enough starred sites, of course).
     */
    private void ensureMerged() {
        if(isMerged)
            return;

        // TODO Temporary
        merged.addAll(history);
        isMerged = true;

        if(history.size() < targetNumber || starred == null || starred.isEmpty()) {
            return;
        }
        
        
        Set<String> hosts = new HashSet<String>();
        Set<String> starredHosts = new HashSet<String>();
        for(History h : history) {
            hosts.add(h.host);
        }
        int nbStarredAlreadyIn = 0;
        for(Iterator<History> iter = starred.iterator(); iter.hasNext(); ) {
            String host = iter.next().host;
            starredHosts.add(host);
            if(hosts.contains(host)) {
                iter.remove();
                nbStarredAlreadyIn ++;
            }
        }
        if(nbStarredAlreadyIn >= targetNumber / 2) {
            // We have enough starred sites in the shortcut list
            return;
        }
        
        // Now we need to add more starred sites, we'll do this by replacing non-starred sites with starred ones
        int nbStarredToAdd = Math.min(targetNumber / 2 - nbStarredAlreadyIn, starred.size()); // Should be at least 1
        int addToPos = merged.size();
        for(int i = nbStarredToAdd - 1; i >= 0; i --) {
            while(merged.size() > addToPos && addToPos > 0 && merged.get(addToPos).isStarred)
                addToPos --;
            merged.add(addToPos, starred.get(i));
        }
    }

}
