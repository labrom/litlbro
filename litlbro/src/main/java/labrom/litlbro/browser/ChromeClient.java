package labrom.litlbro.browser;

import android.graphics.Bitmap;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public final class ChromeClient extends WebChromeClient {

    private final PagePublisher publisher;
    
    public interface PagePublisher {
        /**
         * Values range from 0 to 100.
         * @param progress
         */
        void setProgress(int progress);
        
        void setTitle(String title);
        
        void setIcon(Bitmap icon);
    }

    public ChromeClient(PagePublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        this.publisher.setTitle(title);
    }
    
    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        this.publisher.setIcon(icon);
    }
    
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        this.publisher.setProgress(newProgress);
    }
    
}