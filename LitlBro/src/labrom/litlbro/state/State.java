package labrom.litlbro.state;

public interface State {
    
    State change(Event e);
    
    Event getLastEvent();
    
}
