package labrom.litlbro.suggestion;

public interface Suggestion {
    
    long getId();
    String getTitle();
    String getUrl();
    String getIconUrl();
    int getIconResouceId();
    boolean isSite();
    boolean isStarred();
    float getPriority();

}
