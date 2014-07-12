package labrom.litlbro.gossip;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import labrom.litlbro.suggestion.Suggestion;

public class GossipSuggestion implements Suggestion, Comparable<GossipSuggestion> {
    
    private Map<String, String> data = new HashMap<String, String>();
    private String query;
    
    /**
     * Constructs a suggestion from the Gossip results (query and a few fields, not HTML yet).
     * @param data
     * @param fields
     * @throws JSONException
     */
    public GossipSuggestion(JSONArray data, List<String> fields, String query) throws JSONException {
        int index = 0;
        this.query = query;
        for(String field : fields) {
            if (index >= data.length()) break;
            this.data.put(field, data.getString(index++));
        }
    }
    
    @Override
    public long getId() {
        return 0;
    }
    
    @Override
    public String getTitle() {
        return getKeywords();
    }
    
    @Override
    public boolean isSite() {
        return false;
    }
    
    @Override
    public String getUrl() {
        return null;
    }
    
    @Override
    public String getIconUrl() {
        return null;
    }
    
    @Override
    public boolean isStarred() {
        return false;
    }

    @Override
    public int getIconResouceId() {
        return 0;
    }
    
    @Override
    public float getPriority() {
        return 0.8f - Math.min(3, query.length()) * 0.1f;
    }
    
    /**
     * "k" field.
     * @return
     */
    public String getKeywords() {
        return this.data.get("k");
    }
    

    
    @Override
    public String toString() {
        return this.getKeywords();
    }

    @Override
    public int compareTo(GossipSuggestion another) {
        return this.getKeywords().length() - another.getKeywords().length();
    }


}
