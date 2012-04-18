package labrom.litlbro.state;

public class SearchIntentState extends StateBase {

    @Override
    public State change(Event e) {
        if(e == Event.IMPLICIT_NEXT)
            return PAGE_LOADING;
        if(e == Event.BACK)
            return goTo(SUGGESTIONS, e);
        return this;
    }

}
