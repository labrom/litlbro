package labrom.litlbro.duckduckgo;

import java.util.ArrayList;

import labrom.litlbro.suggestion.Suggestion;

public class DuckySuggestionList extends ArrayList<Suggestion> {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String query;
    private long ts;
    
    public DuckySuggestionList(String query) {
        this.query = query;
        setTimestamp();
    }
    
    public String getQuery() {
        return query;
    }
    
    public String toString() {
        return this.query + ":" + super.toString();
    }
    
    public long getTimestamp() {
        return this.ts;
    }
    
    public void setTimestamp() {
        this.ts = System.currentTimeMillis();
    }

}
