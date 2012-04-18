package labrom.litlbro.gossip;

import java.util.concurrent.Callable;

public class GossipCallable implements Callable<GossipSuggestionList> {
    
    private GossipRequest req;
    private GossipCallback cb;
    
    interface GossipCallback {
        void onComplete(GossipSuggestionList l);
    }
    
    public GossipCallable(GossipRequest req, GossipCallback cb) {
        this.req = req;
        this.cb = cb;
    }

    @Override
    public GossipSuggestionList call() throws Exception {
        this.req.send();
        GossipSuggestionList sugg = req.getSuggestions();
        
        if(this.cb != null) {
            this.cb.onComplete(sugg);
        }
            
        return sugg;
    }

}
