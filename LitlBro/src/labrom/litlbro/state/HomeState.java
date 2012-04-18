package labrom.litlbro.state;

class HomeState extends StateBase {

    @Override
    public State change(Event e) {
        switch(e) {
        case SEARCH_BOX_TEXT:
            return goTo(SUGGESTIONS, e);
        
        case TAP_SHORTCUT:
            return goTo(NAVIGATE_INTENT, e);
        case EDIT_SHORTCUTS:
            return goTo(EDIT_SHORTCUTS, e);
        case BACK:
            return null;
        }
        return this;
    }



}
