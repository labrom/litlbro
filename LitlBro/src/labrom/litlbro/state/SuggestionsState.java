package labrom.litlbro.state;

public class SuggestionsState extends StateBase {

    @Override
    public State change(Event e) {
        switch(e) {
        case SEARCH_BOX_TEXT :
            return this;
        case SEARCH_BOX_DONE :
        case TAP_SEARCH_SUGGESTION :
            return goTo(SEARCH_INTENT, e);
        case TAP_SITE_SUGGESTION :
            return goTo(NAVIGATE_INTENT, e);
        case BACK:
        case SEARCH_BOX_EMPTY:
            return goTo(HOME, e);
        }
        return this;
    }

}
