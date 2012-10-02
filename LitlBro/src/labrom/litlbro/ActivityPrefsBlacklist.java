package labrom.litlbro;

import labrom.data.ActiveRecordList;
import labrom.litlbro.data.DBHistoryManager;
import labrom.litlbro.data.Database;
import labrom.litlbro.data.HistoryBlacklist;
import labrom.litlbro.data.HistoryManager;
import labrom.litlbro.widget.HistoryBlacklistAdapter;
import labrom.litlbro.widget.HistoryBlacklistAdapter.OnReinstateListener;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;

public class ActivityPrefsBlacklist extends ListActivity implements OnReinstateListener {
    
    private Database db;
    private HistoryManager history;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pref_history_blacklist);
        HistoryBlacklistAdapter adaptr = new HistoryBlacklistAdapter(this, null);
        adaptr.setOnReinstateListener(this);
        setListAdapter(adaptr);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        this.db = Database.create(getApplicationContext());
        this.history = new DBHistoryManager(this.db);
        ActiveRecordList<HistoryBlacklist> lst = this.db.query(new HistoryBlacklist(), null, "reinstated=0 OR hide_shortcut=1 OR hide_suggestion=1", null, null);
        Cursor c = lst.getCursor();
        startManagingCursor(c);
        ((CursorAdapter)getListAdapter()).swapCursor(c);
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        this.db.close();
        this.db = null;
    }

    @Override
    public void onReinstate(HistoryBlacklist bl) {
        history.reinstate(bl.host);
    }

}
