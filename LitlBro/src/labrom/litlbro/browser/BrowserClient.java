package labrom.litlbro.browser;

import labrom.litlbro.R;
import labrom.litlbro.data.HistoryManager;
import labrom.litlbro.data.SitePreferencesManager;
import labrom.litlbro.widget.ControlBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public final class BrowserClient extends WebViewClient {
    
    public interface Listener {
        void onPageStarted(String url);
        void onPageFinished();
    }
    
    public interface IntentHandler {
        void handleIntent(Intent i);
    }
    
    final ControlBar controlBar;
    final SitePreferencesManager prefs;
    final HistoryManager history;
    private String lastStartedUrl;
    private NavFlags lastNavFlags;
    private Listener listener;
    private final IntentHandler intentHandler;
    
    /*
     * Should be consistent with what's in the intent filter in the manifest.
     */
    private static final String[] SCHEMES = {"http", "https", "javascript", "about", "inline"};
    private static final String[] EXCLUDED_EXTENSIONS = {".pdf"};

    
    public BrowserClient(ControlBar controlBar, SitePreferencesManager prefs, HistoryManager history, IntentHandler intentHandler) {
        this.controlBar = controlBar;
        this.prefs = prefs;
        this.history = history;
        this.intentHandler = intentHandler;
    }
    
    
    Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    String getLastStartedUrl() {
        return this.lastStartedUrl;
    }
    
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Uri uri = Uri.parse(url);
        
        String lastPathSegment = uri.getLastPathSegment();
        String scheme = uri.getScheme();
        for(String sch : SCHEMES) {
            if(sch.equals(scheme)) { // Supported scheme
                if(lastPathSegment != null) {
                    for(String ext : EXCLUDED_EXTENSIONS) {
                        if(lastPathSegment.endsWith(ext)) {
                            intentHandler.handleIntent(new Intent(Intent.ACTION_VIEW, uri));
                            return true;
                        }
                    }
                }
                return false;
            }
        }

        intentHandler.handleIntent(new Intent(Intent.ACTION_VIEW, uri));
        return true;
    }
    

    @Override
    public void onPageStarted(final WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        
        if(listener != null)
            listener.onPageStarted(url);
        
        NavFlags flags = (NavFlags)view.getTag(R.id.tag_nav_flags);

        if(url.equals(this.lastStartedUrl) && this.lastNavFlags != null)
            flags = this.lastNavFlags;
        this.lastStartedUrl = url;
        this.lastNavFlags = flags;

        boolean forceJs = flags != null && flags.forceJs;
        boolean forceNoJs = flags != null && flags.forceNoJs;
//        boolean explicitNav = flags != null && flags.explicitNav;

        PageLoadController loadCtrl = this.controlBar.getPageLoadController();
        if(loadCtrl != null) {
            if(forceJs) {
                controlBar.setJavascriptEnabled(true);
            } else if(forceNoJs) {
                controlBar.setJavascriptEnabled(false);
            } else /*if(!explicitNav)*/ {
                controlBar.disableJavascriptToggle();
                this.prefs.askWhetherJavascriptEnabled(url, new SitePreferencesManager.Delegate() {
        
                    @Override
                    public void notifyJavascriptEnabled(boolean enabled) {
                        if(view.getSettings().getJavaScriptEnabled() != enabled)
                            view.getSettings().setJavaScriptEnabled(enabled);
                        controlBar.setJavascriptEnabled(enabled);
                    }
                    
                });
            }
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        
        if(listener != null)
            listener.onPageFinished();
        
        this.prefs.recordPagePref(url, view.getSettings().getJavaScriptEnabled());

        NavFlags flags = (NavFlags)view.getTag(R.id.tag_nav_flags);
        boolean noHistory = flags != null && flags.noHistory;
        boolean back = flags != null && flags.isBack;
        view.setTag(R.id.tag_nav_flags, null);
        if(!noHistory) {
            if(back)
                this.history.recordUrlIfPresent(url);
            else
                this.history.recordUrl(url, view.getTitle());
        }
    }
    
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        view.setTag(R.id.tag_nav_flags, null);
    }
    

}