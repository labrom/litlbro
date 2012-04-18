package labrom.litlbro.state;

public class PageOptionsState extends StateBase {

    @Override
    public State change(Event e) {
        if(e == Event.TAP_HW_MENU || e == Event.BACK)
            return goTo(PAGE_LOADED, e);
        if(e == Event.TAP_JS_TOGGLE)
            return goTo(PAGE_LOADING, e);
        return this;
    }

}
