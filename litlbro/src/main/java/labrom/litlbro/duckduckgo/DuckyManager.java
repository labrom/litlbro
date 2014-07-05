package labrom.litlbro.duckduckgo;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import labrom.litlbro.suggestion.Suggestion;

public class DuckyManager {
    
    public static final int MSG_SUGGESTION_LIST_READY = 1;

    ConcurrentHashMap<String, Future<DuckySuggestionList>> suggestions = new ConcurrentHashMap<String, Future<DuckySuggestionList>>();

    private ExecutorService gossipPool;
    private DuckySuggestionList latest;
    
    public DuckyManager() {
        setupPools();
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
    
    public List<Suggestion> queryDucky(String query, final Handler h) {
        // Remove other queries
        for(Future<DuckySuggestionList> l : this.suggestions.values()) {
            if(!l.isDone() && !l.isCancelled())
                l.cancel(true);
        }
        
        Future<DuckySuggestionList> f = this.suggestions.get(query);
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
        
        DuckyCallable.DuckyCallback callback = new DuckyCallable.DuckyCallback() {
            @Override
            public void onComplete(DuckySuggestionList l) {
                Future<DuckySuggestionList> selfFuture = DuckyManager.this.suggestions.get(l.getQuery());
                if(selfFuture != null && selfFuture.isCancelled())
                    return;
                
                if(shouldNotify(l))
                    h.sendMessage(h.obtainMessage(MSG_SUGGESTION_LIST_READY, l));
            }
        };
        DuckyCallable callable = new DuckyCallable(new DuckyRequest(query), callback);
        Future<DuckySuggestionList> future = this.gossipPool.submit(callable);
        this.suggestions.put(query, future);
        return null;
    }
    
    synchronized boolean shouldNotify(DuckySuggestionList l) {
        if(this.latest == null || latest.getTimestamp() < l.getTimestamp()) {
            this.latest = l;
            return true;
        }
        return false;
    }

    


    
}
