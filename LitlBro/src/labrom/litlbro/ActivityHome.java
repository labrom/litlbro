package labrom.litlbro;


import java.util.List;

import labrom.litlbro.data.DBHistoryManager;
import labrom.litlbro.data.Database;
import labrom.litlbro.data.History;
import labrom.litlbro.data.HistoryManager;
import labrom.litlbro.duckduckgo.DuckyManager;
import labrom.litlbro.gossip.GossipManager;
import labrom.litlbro.icon.IconCache;
import labrom.litlbro.shortcut.DisplayShortcutsTask;
import labrom.litlbro.state.Event;
import labrom.litlbro.state.State;
import labrom.litlbro.state.StateBase;
import labrom.litlbro.suggestion.GetSuggestionsTask;
import labrom.litlbro.suggestion.Suggestion;
import labrom.litlbro.widget.ShortcutView;
import labrom.litlbro.widget.ShortcutView.OnShortcutActionListener;
import labrom.litlbro.widget.ShortcutsNavigatorView;
import labrom.litlbro.widget.ShortcutsPage.OnEditModeListener;
import labrom.litlbro.widget.SiteSearchText;
import labrom.litlbro.widget.SiteSearchText.OnDoneHandler;
import labrom.litlbro.widget.SuggestionAdapter;
import labrom.litlbro.widget.TipDialog;
import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * 
 * @author Romain Laboisse labrom@gmail.com
 * 
 */
public class ActivityHome extends Activity implements OnDoneHandler, TextWatcher, OnClickListener, OnLongClickListener, OnItemClickListener, OnShortcutActionListener, OnEditModeListener {


    Database db;
    HistoryManager history;
    GossipManager gossipMgr;
    DuckyManager duckyMgr;
    IconCache iconCache;
    
    State state;

    private ListView suggestionList;
    private SiteSearchText siteText;
    ShortcutsNavigatorView shortcutsPane;
    private DisplayShortcutsTask displayShortcutsTask;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        this.suggestionList = (ListView)findViewById(R.id.suggestions);
        this.suggestionList.setOnItemClickListener(this);
        this.suggestionList.setAdapter(new SuggestionAdapter(this));
        this.siteText = (SiteSearchText)findViewById(R.id.siteTextBox);
        this.siteText.setOnDoneHandler(this);
        this.siteText.addTextChangedListener(this);
        View icon = findViewById(R.id.icon);
        icon.setOnClickListener(this);
        icon.setOnLongClickListener(this);
        
        this.gossipMgr = new GossipManager(getResources());
        this.duckyMgr = new DuckyManager();
        this.iconCache = new IconCache(getCacheDir());
        
        this.shortcutsPane = (ShortcutsNavigatorView)findViewById(R.id.shortcutsPager);
        this.shortcutsPane.setIconCache(this.iconCache);
        this.shortcutsPane.setOnEditModeListener(this);
        this.shortcutsPane.setOnShortcutActionListener(this);
        this.shortcutsPane.setOnShortcutClickListener(this);
    }

    
    @Override
    protected void onStart() {
        super.onStart();
        
        // Initialize DB-based services
        this.db = Database.create(getApplicationContext());
        this.history = new DBHistoryManager(this.db);
        
        setupShortcuts();
        
        if(state == null)
            state = StateBase.HOME;
        else if(state != StateBase.HOME)
            state = state.change(Event.BACK); // Since we change state just before exiting this activity, re-entering requires to go back one state
        setUI();
    }


    private void setupShortcuts() {
        // Init shortcut pages (not done in onCreate because we come back from preferences activity with a different number of pages)
        int pages = getResources().getInteger(R.integer.prefNbPagesDefault);
        String pagesPref = prefs.getString("nbPages", null);
        if(pagesPref != null) {
            pages = Integer.parseInt(pagesPref);
        }
        shortcutsPane.setup(pages);
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        this.db.close();
        this.db = null;
    }
    
    void setUI() {
        if(state == StateBase.HOME || state == StateBase.EDIT_SHORTCUTS) {
            suggestionList.setVisibility(View.GONE);
            shortcutsPane.setVisibility(View.VISIBLE);
//                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(this.siteText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
            
            displayShortcuts();
            
            // In HOME state, display next tip if available
            if(state == StateBase.HOME) {
                Dialog tipDlg = TipDialog.createNextTip(this);
                if(tipDlg != null)
                    tipDlg.show(); // Not using Activity.showDialog because tips are showed once only and we don't want to reuse them
            }
        } else if(state == StateBase.SUGGESTIONS) {
            if(!suggestionList.isShown()) {
                this.suggestionList.setVisibility(View.VISIBLE);
            }
        }
    }
    
    void navigate(String url) {
        Intent i = new Intent(this, ActivityBrowser.class);
        i.setAction(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
    
    void search(String query) {
        // Do a search
        Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
        search.putExtra(SearchManager.QUERY, query);
        startActivity(search);
    }
    
    
    
    
    

    private void displayShortcuts() {
        if(displayShortcutsTask != null && displayShortcutsTask.isRunning())
            return;
        displayShortcutsTask = new DisplayShortcutsTask(history, shortcutsPane.getShortcutsCountPerPage() * shortcutsPane.getPagesCount()) {
            @Override
            protected void onPostExecute(List<History> result) {
                super.onPostExecute(result);
                shortcutsPane.setEditMode(state == StateBase.EDIT_SHORTCUTS);
                shortcutsPane.setShortcutsQueryResult(result);
            }
        };
        displayShortcutsTask.execute();
    }
    
    private void displaySuggestions(String query) {
        new GetSuggestionsTask(history, gossipMgr, duckyMgr, (SuggestionAdapter)this.suggestionList.getAdapter()).execute(query);
        if(!this.suggestionList.isShown()) {
            this.shortcutsPane.setVisibility(View.GONE);
            this.suggestionList.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            changeState(Event.BACK);

            /*
             * If state is null, fall back to default BACK behavior.
             */
            
            if(state != null) {
                setUI();
                return true;
            }
        } else if(keyCode == KeyEvent.KEYCODE_SEARCH) {
            // Search hw key has the same behavior as the LitlBro icon
            handleClickOnIcon();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
    

    @Override
    protected Dialog onCreateDialog(int id) {
        if(id == TipDialog.DIALOG_ID) {
            Dialog d = TipDialog.createNextTip(this);
            if(d != null)
                return d;
        }
        return super.onCreateDialog(id);
    }

    

    
    @Override
    public void afterTextChanged(Editable arg0) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(s.length() == 0) {
            changeState(Event.SEARCH_BOX_EMPTY);
            setUI();
            displayShortcuts();
            return;
        }

        changeState(Event.SEARCH_BOX_TEXT);
        setUI();
        displaySuggestions(s.toString());
    }

    @Override
    public void onDone(SiteSearchText e) {
        changeState(Event.SEARCH_BOX_DONE);
        search(e.getText().toString());
    }

    /**
     * Click on suggestions.
     */
    @Override
    public void onItemClick(AdapterView<?> list, View item, int position, long itemId) {
        Suggestion sugg = (Suggestion)list.getItemAtPosition(position);
        if(sugg.isSite()) {
            changeState(Event.TAP_SITE_SUGGESTION);
            setUI();
            navigate(sugg.getUrl());
        } else {
            changeState(Event.TAP_SEARCH_SUGGESTION);
            setUI();
            search(sugg.getTitle());
        }
        
    }
    
    /**
     * Click on shortcuts or app icon.
     */
    @Override
    public void onClick(View v) {
        if(v instanceof ShortcutView) {
            History h = (History)v.getTag();
            if(h != null) {
                changeState(Event.TAP_SHORTCUT);
                setUI();
                navigate("http://" + h.host); // TODO What about https?
            }
        } else if(v.getId() == R.id.icon) {
            handleClickOnIcon();
        }
    }
    
    private void handleClickOnIcon() {
        
        String q = this.siteText.getText().toString();
        if(q.length() == 0) {
            siteText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(siteText, InputMethodManager.SHOW_IMPLICIT);
            return;
        }
        if(state != StateBase.SUGGESTIONS) {
            changeState(Event.SEARCH_BOX_TEXT);
            setUI();
            displaySuggestions(q);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(siteText, InputMethodManager.SHOW_IMPLICIT);
        } else {
            onDone(siteText);
        }
    }


    /**
     * Long click on app icon.
     */
    @Override
    public boolean onLongClick(View v) {
        if(v.getId() == R.id.icon) {
            ActivityPrefs.startBy(this);
            return true;
        }
        return false;
    }
    
    @Override
    public void onRemoveShortcut(ViewGroup shortcutView, View removeButton) {
        History h = (History)shortcutView.getTag();
        if(h != null) {
            history.removeHost(h.host, true, false);
            displayShortcuts();
        }
    }
    
    @Override
    public void onStarShortcut(Object tag, boolean star) {
        String host = ((History)tag).host;
        if(star)
            history.starHost(host);
        else
            history.unstarHost(host);
        displayShortcuts();
    }
    
    @Override
    public void onEditMode() {
        changeState(Event.EDIT_SHORTCUTS);
        // Nothing else to do, the shortcut pages will update their UI
    }
    
    private void changeState(Event e) {
        if(state != null)
            state = state.change(e);
    }


}
