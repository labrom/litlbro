package labrom.litlbro.suggestion;

import java.util.List;

import labrom.litlbro.data.HistoryManager;
import labrom.litlbro.duckduckgo.DuckyManager;
import labrom.litlbro.gossip.GossipManager;
import labrom.litlbro.widget.SuggestionAdapter;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

public class GetSuggestionsTask extends AsyncTask<String, Void, List<Suggestion>> {
    
    private final HistoryManager historySuggestions;
    private final GossipManager gossip;
    private final DuckyManager ducky;
    private final Handler handler;
    final SuggestionAdapter adapter;
    final SuggestionList suggestions;
    
    public GetSuggestionsTask(HistoryManager histo, GossipManager gossip, DuckyManager ducky, SuggestionAdapter adapter) {
        this.historySuggestions = histo;
        this.adapter = adapter;
        this.gossip = gossip;
        this.ducky = ducky;
        this.suggestions = new SuggestionList(null); // TODO Get query
        
        this.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                @SuppressWarnings("unchecked")
                List<Suggestion> l = (List<Suggestion>)msg.obj;
                if(l != null) {
                    List<Suggestion> merged = GetSuggestionsTask.this.adapter.getSuggestions();
                    suggestions.setMergedList(merged);
                    suggestions.addWebSuggestions(l);
                    suggestions.merge();
                }
            }
        };
    }
    
    @Override
    protected void onPreExecute() {
        
    }
    

    @Override
    protected List<Suggestion> doInBackground(String... params) {
        this.suggestions.setHistorySuggestions(this.historySuggestions.getSuggestions(params[0]));
        this.suggestions.addWebSuggestions(this.gossip.queryGossip(params[0], this.handler));
        this.suggestions.addWebSuggestions(this.ducky.queryDucky(params[0], this.handler));
        this.suggestions.merge();
        return this.suggestions.getMergedList();
    }
    
    @Override
    protected void onPostExecute(List<Suggestion> result) {
        this.adapter.setSuggestions(result);
    }

}
