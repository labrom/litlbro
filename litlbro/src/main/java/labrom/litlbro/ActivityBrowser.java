package labrom.litlbro;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
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
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebIconDatabase;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import labrom.litlbro.browser.BroWebView;
import labrom.litlbro.browser.BrowserClient;
import labrom.litlbro.browser.BrowserSettings;
import labrom.litlbro.browser.DelegatingChromeClient;
import labrom.litlbro.browser.DownloadFileType;
import labrom.litlbro.browser.NavFlags;
import labrom.litlbro.browser.PageLoadController;
import labrom.litlbro.browser.ShareScreenshotTask;
import labrom.litlbro.data.DBHistoryManager;
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
public class ActivityBrowser extends Activity implements
        BrowserClient.Listener, PageLoadController, OnClickListener,
        OnCheckedChangeListener, BrowserClient.IntentHandler, ShakeListener,
        ShakeDialog.Listener, View.OnLongClickListener, DelegatingChromeClient.Delegate {


    private static final int SHAKE_MIN_ACCEL = 3;

    public static final int DIALOG_PROGRESS_SHARE_SCREENSHOT = 1;

    private BroWebView browser;
    private ControlBar controlBar;
    private View optionsPane;
    private CompoundButton optionsStarToggle;
    private BrowserClient viewClient;
    private Database db;
    private HistoryManager history;
    private State state;
    private SharedPreferences prefs;
    private ShakeManager shaker;
    private ShakeDialog shakeDialog;
    private AlertDialog currentlyShowingShakeDialog;
    private ViewGroup fullScreenVideoContainer;
    private WebChromeClient.CustomViewCallback fullScreenVideoCallback;
    private Animation pushOptions;
    private Animation pullOptions;
    private IconCache iconCache;
    private String currentUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        this.shaker = new ShakeManager(this, this);
        this.shakeDialog = new ShakeDialog(this, prefs, this);
        this.fullScreenVideoContainer = (ViewGroup) findViewById(R.id.video);

        this.browser = (BroWebView) findViewById(R.id.web);
        this.browser.setOnLongClickListener(this);
        this.browser.setShouldHideSystemUi(prefs.getBoolean("hideSystemUI", getResources().getBoolean(R.bool.prefHideSystemUIDefault)));
        this.browser.setWebChromeClient(new DelegatingChromeClient(this));
        BrowserSettings.configure(this.browser.getSettings());

        this.optionsPane = findViewById(R.id.optionsPane);
        this.optionsStarToggle = (CompoundButton) this.optionsPane.findViewById(R.id.star);
        this.optionsStarToggle.setOnCheckedChangeListener(this);

        findViewById(R.id.share).setOnClickListener(this);
        findViewById(R.id.shareScreenshot).setOnClickListener(this);
        findViewById(R.id.prefs).setOnClickListener(this);

        this.controlBar = (ControlBar) findViewById(R.id.controlBar);
        this.controlBar.setPageLoadController(this);

        // Control pad animations
        this.pullOptions = AnimationUtils.loadAnimation(this, R.anim.pull_options_pane);
        this.pushOptions = AnimationUtils.loadAnimation(this, R.anim.push_options_pane);
        this.pushOptions.setAnimationListener(new AnimationListener() {

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

        WebIconDatabase.getInstance().open(getCacheDir().getAbsolutePath());
        this.iconCache = new IconCache(getCacheDir());

        this.state = StateBase.NAVIGATE_INTENT;
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Initialize DB-based services
        this.db = Database.create(getApplicationContext());
        this.history = new DBHistoryManager(this.db);
        this.viewClient = new BrowserClient(this.controlBar, this.history, this);
        this.viewClient.setListener(this);
        this.browser.setWebViewClient(viewClient);

        if (this.state == StateBase.NAVIGATE_INTENT) {
            navigate(getIntent());
        } else if (this.state == StateBase.PAGE_OPTIONS) {
            changeState(Event.BACK);
            setUI();
        }
        this.shaker.register();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        navigate(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        browser.resume();
    }

    @Override
    protected void onPause() {
        hideFullScreenVideo(true);
        browser.pause();
        super.onPause();
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
        ViewParent browserParent = browser.getParent();
        if (browserParent instanceof ViewGroup) ViewGroup.class.cast(browserParent).removeView(browser);
        browser.removeAllViews();
        browser.destroy();

        // TODO Approach below shouldn't be needed as zoom controls aren't displayed
/*
        // Workaround for crasher with zoom controls, see http://stackoverflow.com/questions/5267639/how-to-safely-turn-webview-zooming-on-and-off-as-needed
        browser.postDelayed(new Runnable() {
            @Override
            public void run() {
                browser.destroy();
            }
        }, ViewConfiguration.getZoomControlsTimeout());
*/

        super.onDestroy();
    }

    public BroWebView getWebView() {
        return browser;
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
            showOptionsPane();
        }
    }

    private void showOptionsPane() {
        if (!optionsPane.isShown()) {
            browser.showSystemUi(false);
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

    void navigate(String url, boolean noHistory) {
        NavFlags flags = new NavFlags();
        flags.noHistory = noHistory;
        flags.explicitNav = true;
        this.browser.setTag(R.id.tag_nav_flags, flags);
        this.browser.loadUrl(url);
    }

    void navigate(Intent intent) {
        if (intent != null) {
            DownloadFileType downloadFileType = DownloadFileType.fromPath(intent.getData().getLastPathSegment());
            if (downloadFileType != null) {
                handleIntent(intent, downloadFileType);
            }
            state = StateBase.NAVIGATE_INTENT;
            browser.clearHistory();
            String url = intent.getDataString();
            if (url != null) {
                boolean noHistory = isSearchNavigateIntent(intent);
                changeState(Event.IMPLICIT_NEXT);
                setUI();
                navigate(url, noHistory);
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
    public void restart() {
        changeState(Event.RELOAD);
        setUI();

        String url = this.browser.getUrl();
        this.browser.stopLoading();
        navigate(url, true); // Should already be in history right?
    }

    @Override
    public boolean isRestarting() {
        return state != null && state.getLastEvent() == Event.RELOAD;
    }


    @Override
    public void onPageFinished() {
        changeState(Event.PAGE_FINISHED_LOADING);
        setUI();
    }

    @Override
    public void onPageStarted(String url) {
        this.currentUrl = url;
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
        hideFullScreenVideo(true);

        changeState(Event.BACK);
        if (state == null) {
            if (this.browser.canGoBack()) {
                state = StateBase.PAGE_LOADING;
                NavFlags flags = new NavFlags();
                flags.isBack = true;
                this.browser.setTag(R.id.tag_nav_flags, flags);
                this.browser.goBack();
                this.browser.showSystemUi();
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
    public boolean onLongClick(View v) {
        if (v.getId() == R.id.web) {
            changeState(Event.TAP_HW_MENU);
            setUI();
        }
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton v, boolean checked) {
        switch (v.getId()) {
            case R.id.star:
                toggleStarred(checked);
                return;
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
    public void handleIntent(Intent i, DownloadFileType fileType) {
        if (fileType != null) {
            Log.d(L.TAG, "Downloading file " + i.getData());
            DownloadManager.Request downloadRequest = new DownloadManager.Request(i.getData());
            downloadRequest.setVisibleInDownloadsUi(true);
            downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            DownloadManager.class.cast(getSystemService(DOWNLOAD_SERVICE)).enqueue(downloadRequest);
            Toast.makeText(this, getString(R.string.startingDownload, getString(fileType.titleResId)), Toast.LENGTH_LONG).show();
        } else {
            Log.d(L.TAG, "Sending intent " + i);
            startActivity(i);
        }
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

    @Override
    public void onPageProgressChanged(int progress) {
        this.controlBar.onPageProgressChanged(progress);
    }

    @Override
    public void onReceivedTitle(String title) {
        this.controlBar.onReceivedTitle(title);
    }

    @Override
    public void onReceivedIcon(Bitmap icon) {
        this.controlBar.onReceivedIcon(icon);
        if (this.currentUrl != null) {
            String host = UrlUtil.getHost(currentUrl);
            this.iconCache.cache(icon, host);
        }
    }

    @Override
    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        this.controlBar.onShowCustomView(view, callback);
        if (isShowingFullScreenVideo()) {
            hideFullScreenVideo(false);
        }
        fullScreenVideoCallback = callback;
        showFullScreenVideo(view);
    }

    @Override
    public void onHideCustomView() {
        this.controlBar.onHideCustomView();
        hideFullScreenVideo(false);
    }


    private boolean isShowingFullScreenVideo() {
        return fullScreenVideoContainer.getVisibility() == View.VISIBLE;
    }

    private void hideFullScreenVideo(boolean notify) {
        if (notify && fullScreenVideoCallback != null) {
            fullScreenVideoCallback.onCustomViewHidden();
        }
        fullScreenVideoCallback = null;
        fullScreenVideoContainer.removeAllViews();
        fullScreenVideoContainer.setVisibility(View.GONE);
    }

    private void showFullScreenVideo(View v) {
        browser.hideSystemUi();
        fullScreenVideoContainer.addView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        fullScreenVideoContainer.setVisibility(View.VISIBLE);
    }
}
