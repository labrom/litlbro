package labrom.litlbro.shortcut;

import java.util.List;

import labrom.litlbro.data.History;
import labrom.litlbro.data.HistoryManager;
import android.os.AsyncTask;

public class DisplayShortcutsTask extends AsyncTask<Void, Void, List<History>> {

    private final int maxShortcuts;
    private final HistoryManager history;

    private boolean running;
    
    
    public DisplayShortcutsTask(HistoryManager history, int maxShortcuts) {
        this.history = history;
        this.maxShortcuts = maxShortcuts;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        running = true;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        running = false;
    }

    @Override
    protected List<History> doInBackground(Void... params) {
        ShortcutList shortcuts = new ShortcutList(history.getMostPopularSites(maxShortcuts), history.getStarredSites(maxShortcuts), maxShortcuts);
        return shortcuts.getFinalHistoryList();
    }

    @Override
    protected void onPostExecute(List<History> result) {
        running = false;
    }
}