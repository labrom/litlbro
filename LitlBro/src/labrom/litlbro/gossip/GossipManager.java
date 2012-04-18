package labrom.litlbro.gossip;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import labrom.litlbro.suggestion.Suggestion;
import android.content.res.Resources;
import android.os.Handler;

public class GossipManager {
    
    public static final int MSG_SUGGESTION_LIST_READY = 0;

    ConcurrentHashMap<String, Future<GossipSuggestionList>> suggestions = new ConcurrentHashMap<String, Future<GossipSuggestionList>>();

    private ExecutorService gossipPool;
    private GossipSuggestionList latest;
    private final Resources res;
    
    public GossipManager(Resources res) {
        setupPools();
        this.res = res;
    }
    
    public void clear() {
        this.latest = null;
        if(this.gossipPool != null)
            this.gossipPool.shutdownNow();
        this.suggestions.clear();
        setupPools();
    }
    
    private void setupPools() {
        this.gossipPool = Executors.newFixedThreadPool(2);
    }
    
    public List<Suggestion> queryGossip(String query, final Handler h) {
        // Remove other queries
        for(Future<GossipSuggestionList> l : this.suggestions.values()) {
            if(!l.isDone() && !l.isCancelled())
                l.cancel(true);
        }
        
        Future<GossipSuggestionList> f = this.suggestions.get(query);
        if(f != null && f.isDone() && !f.isCancelled()) {
            try {
                return new ArrayList<Suggestion>(f.get()); // Shouldn't wait since it's complete
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
        
        GossipCallable.GossipCallback callback = new GossipCallable.GossipCallback() {
            @Override
            public void onComplete(GossipSuggestionList l) {
                Future<GossipSuggestionList> selfFuture = GossipManager.this.suggestions.get(l.getQuery());
                if(selfFuture != null && selfFuture.isCancelled())
                    return;
                
                if(shouldNotify(l))
                    h.sendMessage(h.obtainMessage(MSG_SUGGESTION_LIST_READY, l));
            }
        };
        GossipCallable callable = new GossipCallable(new GossipRequest(res, query), callback);
        Future<GossipSuggestionList> future = this.gossipPool.submit(callable);
        this.suggestions.put(query, future);
        return null;
    }
    
    synchronized boolean shouldNotify(GossipSuggestionList l) {
        if(this.latest == null || latest.getTimestamp() < l.getTimestamp()) {
            this.latest = l;
            return true;
        }
        return false;
    }

    


    
}
