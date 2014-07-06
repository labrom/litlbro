package labrom.litlbro;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.webkit.WebIconDatabase;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;

import labrom.litlbro.browser.BrowserClient;
import labrom.litlbro.browser.BrowserSettings;
import labrom.litlbro.browser.ChromeClient;
import labrom.litlbro.browser.ChromeClient.PagePublisher;
import labrom.litlbro.browser.NavFlags;
import labrom.litlbro.browser.PageLoadController;
import labrom.litlbro.browser.ShareScreenshotTask;
import labrom.litlbro.data.DBHistoryManager;
import labrom.litlbro.data.DBSitePreferencesManager;
import labrom.litlbro.data.Database;
import labrom.litlbro.data.HistoryManager;
import labrom.litlbro.icon.IconCache;
import labrom.litlbro.state.Event;
import labrom.litlbro.state.State;
import labrom.litlbro.state.StateBase;
import labrom.litlbro.util.ShakeManager;
import labrom.litlbro.util.ShakeManager.ShakeListener;
import labrom.litlbro.util.UrlUtil;
import labrom.litlbro.widget.ControlBar;
import labrom.litlbro.widget.ShakeDialog;

/**
 * @author Romain Laboisse labrom@gmail.com
 */
public class ActivityBrowser extends Activity implements BrowserClient.Listener, PageLoadController, OnClickListener, OnCheckedChangeListener, BrowserClient.IntentHandler, ShakeListener, ShakeDialog.Listener {


    private static final int SHAKE_MIN_ACCEL = 3;

    private final class PagePublisherWrapper implements PagePublisher {

        private PagePublisher wrapped;
        private IconCache iconCache;
        private String currentUrl;

        public PagePublisherWrapper(PagePublisher wrapped, IconCache iconCache) {
            this.wrapped = wrapped;
            this.iconCache = iconCache;
        }

        public void setCurrentUrl(String url) {
            this.currentUrl = url;
        }

        @Override
        public void setProgress(int progress) {
            wrapped.setProgress(progress);
        }

        @Override
        public void setTitle(String title) {
            wrapped.setTitle(title);
        }

        @Override
        public void setIcon(Bitmap icon) {
            wrapped.setIcon(icon);
            if (this.currentUrl != null) {
                String host = UrlUtil.getHost(currentUrl);
                this.iconCache.cache(icon, host);
            }
        }
    }

    public static final int DIALOG_PROGRESS_SHARE_SCREENSHOT = 1;

    BroWebView browser;
    ControlBar controlBar;
    View optionsPane;
    CompoundButton optionsStarToggle;
    CompoundButton optionsJsToggle;
    BrowserClient viewClient;
    ProgressBar progress;
    private Animation pushOptions;
    private Animation pullOptions;

    Database db;
    DBSitePreferencesManager sitePrefs;
    HistoryManager history;
    IconCache iconCache;

    private State state;
    private SharedPreferences prefs;
    private ShakeManager shaker;
    private ShakeDialog shakeDialog;
    private AlertDialog currentlyShowingShakeDialog;


    private PagePublisherWrapper pagePublisher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chrome);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        shaker = new ShakeManager(this, this);
        shakeDialog = new ShakeDialog(this, prefs, this);

        this.browser = (BroWebView) findViewById(R.id.web);
        this.optionsPane = findViewById(R.id.optionsPane);
        this.optionsStarToggle = (CompoundButton) this.optionsPane.findViewById(R.id.star);
        this.optionsStarToggle.setOnCheckedChangeListener(this);
        this.optionsJsToggle = (CompoundButton) this.optionsPane.findViewById(R.id.optionsJsToggle);
        findViewById(R.id.share).setOnClickListener(this);
        findViewById(R.id.shareScreenshot).setOnClickListener(this);
        findViewById(R.id.prefs).setOnClickListener(this);
        this.controlBar = (ControlBar) findViewById(R.id.controlBar);
        this.controlBar.setPageLoadController(this);

        // Control pad animations
        pullOptions = AnimationUtils.loadAnimation(this, R.anim.pull_options_pane);
        pushOptions = AnimationUtils.loadAnimation(this, R.anim.push_options_pane);
        pushOptions.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                optionsPane.setVisibility(View.INVISIBLE);
            }
        });


        configureBrowser();

        WebIconDatabase.getInstance().open(getCacheDir().getAbsolutePath());
        this.iconCache = new IconCache(getCacheDir());
        this.pagePublisher = new PagePublisherWrapper(this.controlBar, this.iconCache);
        this.browser.setWebChromeClient(new ChromeClient(pagePublisher));

        this.state = StateBase.NAVIGATE_INTENT;
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Initialize DB-based services
        this.db = Database.create(getApplicationContext());
        this.sitePrefs = new DBSitePreferencesManager(this.db);
        this.history = new DBHistoryManager(this.db);
        this.viewClient = new BrowserClient(this.controlBar, this.sitePrefs, this.history, this);
        this.viewClient.setListener(this);
        this.browser.setWebViewClient(viewClient);

        if (state == StateBase.NAVIGATE_INTENT) {
            navigate(getIntent());
        } else if (state == StateBase.PAGE_OPTIONS) {
            changeState(Event.BACK);
            setUI();
        }
        shaker.register();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        navigate(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.shaker.unregister();
        this.browser.stopLoading();
        this.db.close();
        this.db = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Workaround for crasher with zoom controls, see http://stackoverflow.com/questions/5267639/how-to-safely-turn-webview-zooming-on-and-off-as-needed
        this.browser.postDelayed(new Runnable() {
            @Override
            public void run() {
                browser.destroy();
            }
        }, ViewConfiguration.getZoomControlsTimeout());
    }


    private void configureBrowser() {
        WebSettings settings = this.browser.getSettings();
        settings.setJavaScriptEnabled(false); // Javascript disabled by default

        BrowserSettings.configure(settings);
    }


    private void setUI() {
        boolean useAnimations = ActivityPrefs.useWindowAnimations(prefs, getResources());
        if (state == StateBase.PAGE_LOADING) {
            browser.setVisibility(View.VISIBLE);
            controlBar.show(useAnimations);
            hideOptionsPane();
        } else if (state == StateBase.PAGE_LOADED) {
            browser.setVisibility(View.VISIBLE);
            if (controlBar.isShown())
                this.controlBar.hide(useAnimations);
            hideOptionsPane();
        } else if (state == StateBase.PAGE_OPTIONS) {
            if (controlBar.isShown())
                this.controlBar.hide(useAnimations);
            optionsStarToggle.setChecked(history.isStarred(this.browser.getUrl()));
            optionsJsToggle.setChecked(this.browser.getSettings().getJavaScriptEnabled());
            showOptionsPane();
        }
    }

    private void showOptionsPane() {
        if (!optionsPane.isShown()) {
            if (ActivityPrefs.useWindowAnimations(prefs, getResources())) {
                optionsPane.startAnimation(pullOptions);
            }
            optionsPane.setVisibility(View.VISIBLE);
        }
    }

    private void hideOptionsPane() {
        if (optionsPane.isShown() && ActivityPrefs.useWindowAnimations(prefs, getResources())) {
            optionsPane.startAnimation(pushOptions);
        } else {
            optionsPane.setVisibility(View.INVISIBLE);
        }
    }

    void navigate(String url, boolean forceJs, boolean forceNoJs, boolean noHistory) {
        NavFlags flags = new NavFlags();
        flags.forceJs = forceJs;
        flags.forceNoJs = forceNoJs;
        flags.noHistory = noHistory;
        flags.explicitNav = true;
        this.browser.setTag(R.id.tag_nav_flags, flags);
//        if(!forceJs && !forceNoJs) {
//            this.sitePrefs.askWhetherJavascriptEnabled(url, new Delegate() {
//               @Override
//                public void notifyJavascriptEnabled(boolean enabled) {
//                   browser.getSettings().setJavaScriptEnabled(enabled);
//                } 
//            });
//        }
        this.browser.loadUrl(url);
    }

    void navigate(Intent intent) {

        if (intent != null) {
            state = StateBase.NAVIGATE_INTENT;
            browser.clearHistory();
            String url = intent.getDataString();
            if (url != null) {

                boolean forceJs = isSearchNavigateIntent(intent);
                boolean noHistory = forceJs;
                if (forceJs) {
                    browser.getSettings().setJavaScriptEnabled(true);
                }

                changeState(Event.IMPLICIT_NEXT);
                setUI();
                navigate(url, forceJs, false, noHistory);
                return;
            }
        }
    }

    private boolean isSearchNavigateIntent(Intent intent) {
        String appid = intent.getStringExtra("com.android.browser.application_id");
        Uri u = intent.getData();
        boolean search = "com.android.quicksearchbox".equals(appid) || "com.google.android.googlequicksearchbox".equals(appid) || (u != null && "duckduckgo.com".equals(u.getHost()));
        return search;
    }

    @Override
    public void restart(boolean enableJavascript) {
        changeState(Event.TAP_JS_TOGGLE);
        setUI();

        String url = this.browser.getUrl();
        this.browser.stopLoading();
        this.browser.getSettings().setJavaScriptEnabled(enableJavascript);
        navigate(url, enableJavascript, !enableJavascript, true); // Should already be in history right?
    }

    @Override
    public boolean isRestarting() {
        return state != null && state.getLastEvent() == Event.TAP_JS_TOGGLE;
    }


    @Override
    public void onPageFinished() {
        changeState(Event.PAGE_FINISHED_LOADING);
        setUI();
    }

    @Override
    public void onPageStarted(String url) {
        this.pagePublisher.setCurrentUrl(url);
        /*
         * We had a page loaded, now a (supposedly new) page is loading,
         * so someone must have clicked on a link or something... 
         */
        if (state == StateBase.PAGE_LOADED) {
            changeState(Event.TAP_LINK);
            setUI();
        }
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_MENU) {
            // Display/hide options bar on Menu hardware button
            changeState(Event.TAP_HW_MENU);
            setUI();
            return true; // Means we intercept Menu hardware key when control bar is shown too
        }

        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            onGoHome();
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        changeState(Event.BACK);

        if (state == null) {
            if (this.browser.canGoBack()) {
                state = StateBase.PAGE_LOADING;
                NavFlags flags = new NavFlags();
                flags.isBack = true;
                this.browser.setTag(R.id.tag_nav_flags, flags);
                this.browser.goBack();
                setUI();
            } else /*if(gotViewIntent())*/ {
                super.onBackPressed();
            }
        } else {
            setUI();
        }
    }


    private void changeState(Event e) {
        if (state != null)
            state = state.change(e);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share:
                share();
                return;
            case R.id.shareScreenshot:
                shareScreenshot();
                return;
            case R.id.prefs:
                ActivityPrefs.startBy(this);
                return;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton v, boolean checked) {
        switch (v.getId()) {
            case R.id.star:
                toggleStarred(checked);
                return;
            case R.id.optionsJsToggle:
                restart(checked);
                this.controlBar.setJavascriptEnabled(checked);
        }
    }

    private void share() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_TEXT, this.browser.getUrl());
        i.setType("text/plain");
        startActivity(Intent.createChooser(i, getString(R.string.shareDialogTitle)));
    }

    private void toggleStarred(boolean star) {
        if (star)
            this.history.starUrl(this.browser.getUrl());
        else
            this.history.unstarUrl(this.browser.getUrl());
    }

    private void shareScreenshot() {
        showDialog(DIALOG_PROGRESS_SHARE_SCREENSHOT);
        new ShareScreenshotTask(this).execute(UrlUtil.getDomain(browser.getUrl()));
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_PROGRESS_SHARE_SCREENSHOT) {
            ProgressDialog d = new ProgressDialog(this);
            d.setIndeterminate(true);
            d.setMessage(getString(R.string.progressShareScreenshot));
            return d;
        }
        return super.onCreateDialog(id);
    }

    @Override
    public void handleIntent(Intent i) {
        Log.d(L.TAG, "Sending intent " + i);
        startActivity(i);
    }


    @Override
    public void onShake(float acceleration) {
        if (acceleration > SHAKE_MIN_ACCEL + prefs.getInt("shakeLevel", getResources().getInteger(R.integer.prefShakeLevelDefault))) {
            if (currentlyShowingShakeDialog != null) {
                if (currentlyShowingShakeDialog.isShowing())
                    return;
            }

            currentlyShowingShakeDialog = shakeDialog.create();
            if (currentlyShowingShakeDialog != null)
                currentlyShowingShakeDialog.show();
            else
                onGoHome(); // No confirmation needed
        }
    }

    @Override
    public void onGoHome() {
        startActivity(new Intent(this, ActivityHome.class));
        finish();
    }

    public WebView getWebView() {
        return browser;
    }
}
