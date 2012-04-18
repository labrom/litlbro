package labrom.litlbro.gossip;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import labrom.litlbro.R;
import labrom.litlbro.util.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.res.Resources;


/**
 * Yahoo! Gossip suggestions query.
 * 
 * TODO Google suggestions: http://google.com/complete/search?output=toolbar&q=bike
 * 
 * @author Romain Laboisse labrom@gmail.com
 *
 */
public class GossipRequest extends Request {
    

    private final String query;
    private final String apiUrl;
    
    private String q;
    private final List<String> fields = new ArrayList<String>();
    private final GossipSuggestionList r;
    
        
    
    public GossipRequest(Resources res, String query) {
        this.query = query;
        this.apiUrl = res.getString(R.string.gossipApi);
        this.r = new GossipSuggestionList(query);
    }
    
    public String getQuery() {
        return this.query;
    }
    
    public String getQ() {
        return q;
    }

    public List<String> getFields() {
        return fields;
    }

    /**
     * Suggestions built from the "r" field.
     * @return
     */
    public GossipSuggestionList getSuggestions() {
        return r;
    }

    @Override
    public void buildUrl() {
        this.url =  apiUrl + "?output=yjsonp&command=" + URLEncoder.encode(this.query);
    }

    @Override
    protected void extractResult() {
        // Remove the function call that wraps the JSON
        int start = this.result.indexOf('{');
        int end = this.result.lastIndexOf('}');
        String json = this.result.substring(start, end + 1);
        JSONTokener tkner = new JSONTokener(json);
        try {
            JSONObject root = (JSONObject)tkner.nextValue();
            this.q = root.getString("q");
            JSONArray fJson = (JSONArray)root.get("f");
            for(int i = 0; i < fJson.length(); i ++)
                this.fields.add(fJson.getString(i));
            JSONArray rJson = (JSONArray)root.get("r");
            for(int i = 0; i < rJson.length(); i ++)
                this.r.add(new GossipSuggestion((JSONArray)rJson.get(i), this.fields, this.query));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    


}
