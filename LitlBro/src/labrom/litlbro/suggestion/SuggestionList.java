package labrom.litlbro.suggestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import labrom.litlbro.util.ObservableListProxy;

public class SuggestionList {

    private String query;
    private List<Suggestion> historySuggestions;
    private final List<Suggestion> webSuggestions = new ArrayList<Suggestion>();
    private List<Suggestion> mergedList = new ArrayList<Suggestion>();
    private Comparator<Suggestion> comparator = new SuggestionComparator();
    
    public SuggestionList(String query) {
        this.query = query;
    }
    
    public String getQuery() {
        return query;
    }
    
    public List<Suggestion> getMergedList() {
        return mergedList;
    }

    public void setMergedList(List<Suggestion> mergedList) {
        if(mergedList != null)
            this.mergedList = mergedList;
        else
            this.mergedList = new ArrayList<Suggestion>();
    }

    public List<Suggestion> getHistorySuggestions() {
        return historySuggestions;
    }

    public void setHistorySuggestions(List<Suggestion> historySuggestions) {
        this.historySuggestions = historySuggestions;
    }

    public List<Suggestion> getWebSuggestions() {
        return webSuggestions;
    }

    public void addWebSuggestions(List<Suggestion> suggestions) {
        if(suggestions != null)
            this.webSuggestions.addAll(suggestions);
    }

    public void merge() {
        if(mergedList instanceof ObservableListProxy<?>)
            ((ObservableListProxy<?>)mergedList).holdNotify();
        
        mergedList.clear();
        mergedList.addAll(historySuggestions);
        mergedList.addAll(webSuggestions);
        Collections.sort(mergedList, comparator);
        
        // Dedupe
        Iterator<Suggestion> iter = mergedList.iterator();
        Set<String> uniqueUrls = new HashSet<String>();
        while(iter.hasNext()) {
            Suggestion sugg = iter.next();
            String url = sugg.getUrl();
            if(url != null && !uniqueUrls.add(cleanUrl(url)))
                iter.remove();
        }
        
        if(mergedList instanceof ObservableListProxy<?>)
            ((ObservableListProxy<?>)mergedList).forceNotify();
    }
    
    /**
     * Cleans an URL for dedupe.
     * 
     * Current: removes fragment.
     * @param url
     * @return
     */
    private String cleanUrl(String url) {
        if(url == null)
            return null;
        int i = url.lastIndexOf('#');
        if(i > 0)
            return url.substring(0, i);
        return url;
    }
    
    @Override
    public String toString() {
        return String.format("History: %s - Web: %s - Merged: %s", historySuggestions, webSuggestions, mergedList);
    }

}
