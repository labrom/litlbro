package labrom.litlbro.duckduckgo;

import labrom.litlbro.R;
import labrom.litlbro.suggestion.Suggestion;

public class DuckyExtResultSuggestion implements Suggestion {
    
    private final DuckyResult res;
    
    public DuckyExtResultSuggestion(DuckyResult res) {
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
        return res.iconUrl;
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
        return 1;
    }

    @Override
    public String toString() {
        return "Site: " + (res.text != null && res.text.length() > 0 ? res.text : res.url);
    }
}
