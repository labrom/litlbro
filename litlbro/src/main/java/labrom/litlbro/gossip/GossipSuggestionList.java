package labrom.litlbro.gossip;

import java.util.ArrayList;

public class GossipSuggestionList extends ArrayList<GossipSuggestion> {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String query;
    private long ts;
    
    public GossipSuggestionList(String query) {
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
