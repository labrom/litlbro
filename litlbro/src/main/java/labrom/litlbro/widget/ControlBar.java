package labrom.litlbro.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebChromeClient;
import android.widget.RelativeLayout;

import labrom.litlbro.R;
import labrom.litlbro.browser.DelegatingChromeClient;
import labrom.litlbro.browser.PageLoadController;

public class ControlBar extends RelativeLayout implements DelegatingChromeClient.Delegate {
    
    /**
     * Control pads will always show at least this long.
     */
    private static int CONTROL_PAD_MIN_SHOW_TIME = 3500;
    
    /**
     * Control bar will always show at least this long.
     */
    private static int CONTROL_BAR_SHOW_TIME = 5000;
    
    /**
     * Don't hide control bar and control pad until progress has reached this value.
     */
    private static int HIDE_ON_PROGRESS = 80;

    /**
     * When it's time to hide control bar and control pad but progress hasn't reached {@link #HIDE_ON_PROGRESS},
     * poll for progress following this interval.
     */
    private static int CONTROL_BAR_PROGRESS_POLL_INTERVAL = 1000;
    
    private PageLoadingView pageLoadingView;
    private PageLoadController pageLoadController;
    private long lastControlPadShowTime;
    private final Handler handler = new Handler();

    private final Runnable runHide = new Runnable() {
        @Override
        public void run() {
            setVisibility(View.INVISIBLE);
            onReceivedTitle(null);
        }
    };
    private class RunTimedHide implements Runnable {
        private boolean useAnimation;
        public RunTimedHide(boolean useAnimation) {
            this.useAnimation = useAnimation;
        }
        
        @Override
        public void run() {
            if(pageLoadingView.getProgress() >= HIDE_ON_PROGRESS)
                hide(this.useAnimation);
            else
                handler.postDelayed(this, CONTROL_BAR_PROGRESS_POLL_INTERVAL);
        }
    };
    private RunTimedHide runTimedHide;



    public ControlBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ControlBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlBar(Context context) {
        super(context);
    }
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        pageLoadingView = (PageLoadingView)findViewById(R.id.pageTitleProgressLabel);
    }
    
    public PageLoadController getPageLoadController() {
        return pageLoadController;
    }

    public void setPageLoadController(PageLoadController pageLoader) {
        pageLoadController = pageLoader;
    }

    public void show(boolean useAnimation) {
        handler.removeCallbacks(runHide);
        
        setVisibility(View.VISIBLE);
        lastControlPadShowTime = System.currentTimeMillis();
        onReceivedTitle(null);

        runTimedHide = new RunTimedHide(useAnimation);
        handler.postDelayed(runTimedHide, CONTROL_BAR_SHOW_TIME);
    }
    
    public void hide(boolean useAnimation) {
        if(runTimedHide != null)
            handler.removeCallbacks(runTimedHide);
        if(!isShown())
            return;
        
        int durationShown = (int)(System.currentTimeMillis() - lastControlPadShowTime);
        int delay = CONTROL_PAD_MIN_SHOW_TIME - durationShown;
        if(delay > 0) {
            handler.postDelayed(runHide, delay);
        } else {
            runHide.run();
        }
    }

    @Override
    public void onReceivedTitle(String title) {
        this.pageLoadingView.setText(title);
    }

    @Override
    public void onPageProgressChanged(int progress) {
        this.pageLoadingView.setProgress(progress);
    }
    
    @Override
    public void onReceivedIcon(Bitmap icon) {
        // Does nothing
    }

    @Override
    public void onHideCustomView() {
        // Does nothing
    }

    @Override
    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        // Does nothing
    }
}
