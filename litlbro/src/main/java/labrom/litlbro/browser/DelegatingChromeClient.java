package labrom.litlbro.browser;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public final class DelegatingChromeClient extends WebChromeClient {

    private final Delegate delegate;
    
    public interface Delegate {
        /**
         * Values range from 0 to 100.
         * @param progress
         */
        void onPageProgressChanged(int progress);
        
        void onReceivedTitle(String title);
        
        void onReceivedIcon(Bitmap icon);

        void onShowCustomView(View view, CustomViewCallback callback);

        void onHideCustomView();
    }

    public DelegatingChromeClient(Delegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        this.delegate.onReceivedTitle(title);
    }
    
    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        this.delegate.onReceivedIcon(icon);
    }
    
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        this.delegate.onPageProgressChanged(newProgress);
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        this.delegate.onShowCustomView(view, callback);
    }

    @Override
    public void onHideCustomView() {
        this.delegate.onHideCustomView();
    }
}