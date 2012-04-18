package labrom.litlbro.state;

class EditShortcutsState extends StateBase {

    @Override
    public State change(Event e) {
        switch(e) {
        case BACK:
            return previous;
        }
        return this;
    }



}
