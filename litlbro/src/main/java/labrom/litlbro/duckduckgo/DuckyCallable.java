package labrom.litlbro.duckduckgo;

import java.util.concurrent.Callable;

public class DuckyCallable implements Callable<DuckySuggestionList> {
    
    private DuckyRequest req;
    private DuckyCallback cb;
    
    interface DuckyCallback {
        void onComplete(DuckySuggestionList l);
    }
    
    public DuckyCallable(DuckyRequest req, DuckyCallback cb) {
        this.req = req;
        this.cb = cb;
    }

    @Override
    public DuckySuggestionList call() throws Exception {
        this.req.send();
        DuckySuggestionList sugg = req.getSuggestions();
        
        if(this.cb != null) {
            this.cb.onComplete(sugg);
        }
            
        return sugg;
    }

}
