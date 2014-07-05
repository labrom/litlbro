package labrom.litlbro.duckduckgo;

import labrom.litlbro.R;
import labrom.litlbro.suggestion.Suggestion;

public class DuckyTopicSuggestion implements Suggestion {
    
    private final DuckyResult res;
    
    public DuckyTopicSuggestion(DuckyResult res) {
        this.res = res;
    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public String getTitle() {
        return res.text;
    }

    @Override
    public String getUrl() {
        return res.url;
    }
    
    @Override
    public String getIconUrl() {
        return null;
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
    public int getIconResouceId() {
        return R.drawable.ducky_icon;
    }
    
    @Override
    public float getPriority() {
        return 0.8f;
    }

    @Override
    public String toString() {
        return "Ducky: " + res.text;
    }

}
