package labrom.litlbro.state;

class PageLoadingState extends StateBase {

    @Override
    public State change(Event e) {
        switch(e) {
        case TAP_HW_MENU:
            return goTo(PAGE_OPTIONS, e);
        case PAGE_FINISHED_LOADING:
            return goTo(PAGE_LOADED, e);
        case TAP_LINK:
            return goTo(PAGE_LOADING, e);
        case TAP_JS_TOGGLE:
            return this;
        case BACK:
            return null;
        }
        return this;
    }

}
