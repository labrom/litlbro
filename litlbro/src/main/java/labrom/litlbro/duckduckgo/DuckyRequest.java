package labrom.litlbro.duckduckgo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URLEncoder;
import java.util.List;

import labrom.litlbro.util.Request;

public class DuckyRequest extends Request {
    
    private final String query;
    private DuckyTopic topic;
    private final DuckySuggestionList suggestions;

    public DuckyRequest(String query) {
        this.suggestions = new DuckySuggestionList(query);
        this.query = query;
    }

    @Override
    protected void buildUrl() {
        this.url = "http://api.duckduckgo.com/?q=" + URLEncoder.encode(this.query) + "&format=json&no_redirect=1";
    }

    @Override
    protected void extractResult() {
        JSONTokener tkner = new JSONTokener(this.result);
        topic = new DuckyTopic();
        try {
            JSONObject root = (JSONObject)tkner.nextValue();
            if(root.has("Type"))
                topic.type = DuckyTopic.ResultType.fromCode(root.getString("Type"));
            if(root.has("Heading"))
                topic.heading = root.getString("Heading");
            if(root.has("AbstractURL"))
                topic.abstractUrl = root.getString("AbstractURL");
            if(root.has("AbstractSource"))
                topic.abstractSource = root.getString("AbstractSource");
            
            if(root.has("RelatedTopics")) {
                JSONArray relatedTopics = (JSONArray)root.get("RelatedTopics");
                populateResultList(relatedTopics, topic.relatedTopics, null);
            }
            if(root.has("Results")) {
                JSONArray results = (JSONArray)root.get("Results");
                populateResultList(results, topic.results, null);
            }
            
            /*
             * Create suggestions, we'll return max 3.
             */
            int count = 0;
            // First, real result(s) that point directly to the result website - limit to two, there is generally just one
            if(!topic.results.isEmpty()) {
                suggestions.add(new DuckyExtResultSuggestion(topic.results.get(0)));
                count ++;
                if(topic.results.size() > 1) {
                    suggestions.add(new DuckyExtResultSuggestion(topic.results.get(1)));
                    count ++;
                }
            }
            // Then a topic (usually Wikipedia) TODO exclude disambiguation pages?
            if(topic.abstractUrl != null && "Wikipedia".equals(topic.abstractSource)) {
                suggestions.add(new DuckyWikipediaSuggestion(topic));
                count ++;
            }
            // Then related topics
            for(int i = 0; i < 3 - count && i < topic.relatedTopics.size() ; i ++) {
                suggestions.add(new DuckyTopicSuggestion(topic.relatedTopics.get(i)));
            }
            
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private void populateResultList(JSONArray a, List<DuckyResult> res, String category) throws JSONException {
        for(int i = 0; i < a.length(); i ++) {
            JSONObject o = a.getJSONObject(i);
            if(o.has("Topics")) {
                String cat = null;
                if(o.has("Name"))
                    cat = o.getString("Name");
                JSONArray aa = o.getJSONArray("Topics");
                populateResultList(aa, res, cat);
            } else {
                DuckyResult r = new DuckyResult();
                res.add(r);
                r.category = category;
                if(o.has("Text"))
                    r.text = o.getString("Text");
                if(o.has("FirstURL"))
                    r.url = o.getString("FirstURL");
                if(o.has("Icon")) {
                    JSONObject icon = o.getJSONObject("Icon");
                    if(icon.has("URL"))
                        r.iconUrl = icon.getString("URL");
                }
            }
        }
    }
    
    public DuckySuggestionList getSuggestions() {
        return suggestions;
    }
    
}
