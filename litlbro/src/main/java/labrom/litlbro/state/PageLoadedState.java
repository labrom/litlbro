package labrom.litlbro.state;

class PageLoadedState extends StateBase {

    @Override
    public State change(Event e) {
        switch(e) {
        case TAP_HW_MENU:
            return goTo(PAGE_OPTIONS, e);
        case TAP_LINK:
            return goTo(PAGE_LOADING, e);
        case RELOAD:
            return goTo(PAGE_LOADING, e);
        case BACK:
            return null;
        }
        return this;
    }


}
