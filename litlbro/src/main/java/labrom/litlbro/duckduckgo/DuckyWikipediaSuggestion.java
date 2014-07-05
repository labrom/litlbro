package labrom.litlbro.duckduckgo;

import labrom.litlbro.R;
import labrom.litlbro.suggestion.Suggestion;

public class DuckyWikipediaSuggestion implements Suggestion {
    
    private final DuckyTopic t;
    
    public DuckyWikipediaSuggestion(DuckyTopic t) {
        this.t = t;
    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public String getTitle() {
        return t.heading;
    }

    @Override
    public String getUrl() {
        return t.abstractUrl;
    }
    
    @Override
    public String getIconUrl() {
        return null;
    }

    @Override
    public int getIconResouceId() {
        return R.drawable.wikipedia_icon;
    }

    @Override
    public boolean isSite() {
        return true;
    }

    @Override
    public boolean isStarred() {
        return false;
    }
    
    
    @Override
    public float getPriority() {
        return 0.9f;
    }

    @Override
    public String toString() {
        return "Wikipedia: " + t.heading;
    }
    
}
