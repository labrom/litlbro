package labrom.litlbro.state;

public class NavigateIntentState extends StateBase {

    @Override
    public State change(Event e) {
        if(e == Event.IMPLICIT_NEXT)
            return PAGE_LOADING;
        if(e == Event.BACK)
            return previous; // Could be either SUGGESTIONS or HOME
        return this;
    }

}
