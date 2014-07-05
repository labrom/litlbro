package labrom.litlbro.suggestion;

import java.util.Comparator;

import labrom.litlbro.gossip.GossipSuggestion;

public class SuggestionComparator implements Comparator<Suggestion> {

    @Override
    public int compare(Suggestion sugg1, Suggestion sugg2) {
        if(sugg1.getClass() == sugg2.getClass()) {
            if(sugg1 instanceof GossipSuggestion)
                return ((GossipSuggestion)sugg1).compareTo((GossipSuggestion)sugg2);
            return sugg1.equals(sugg2) ? 0 : -1;
        }
        return sugg2.getPriority() - sugg1.getPriority() > 0 ? 1 : -1;
    }

}
