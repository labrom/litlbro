package labrom.litlbro.state;

public abstract class StateBase implements State {
    
    public static final StateBase HOME = new HomeState();
    public static final StateBase EDIT_SHORTCUTS = new EditShortcutsState();
    public static final StateBase SUGGESTIONS = new SuggestionsState();
    public static final StateBase PAGE_LOADED = new PageLoadedState();
    public static final StateBase PAGE_LOADING = new PageLoadingState();
    public static final StateBase PAGE_OPTIONS = new PageOptionsState();
    public static final StateBase NAVIGATE_INTENT = new NavigateIntentState();
    public static final StateBase SEARCH_INTENT = new SearchIntentState();
    

    
    State previous;
    Event lastEvent;
    
    StateBase() {}
    
    State goTo(StateBase next, Event e) {
        this.previous = null;
        next.previous = this;
        next.lastEvent = e;
        return next;
    }
    
    @Override
    public String toString() {
        String name = getStateName();
        StringBuilder sb = new StringBuilder(name);
        if(previous instanceof StateBase) {
            sb.insert(0, " > ");
            sb.insert(0, ((StateBase)previous).getStateName());
        }
        return sb.toString();
    }

    private String getStateName() {
        String name = getClass().getCanonicalName();
        if(name.endsWith("State"))
            name = name.substring(0, name.length() - 5);
        return name;
    }
    
    

    @Override
    public Event getLastEvent() {
        return lastEvent;
    }
}
