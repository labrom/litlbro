package labrom.litlbro;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

/**
 * Created by labrom on 3/6/14.
 */
public class BroWebView extends WebView {

    interface Listener {
        void onShowChrome();
        void onHideChrome();
    }

    private Listener listener;
    private final Runnable showRunnable = new Runnable() {
        @Override
        public void run() {
            if(listener != null) {
                listener.onShowChrome();
            }
        }
    };

    public BroWebView(Context context) {
        super(context);
    }

    public BroWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BroWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        removeCallbacks(showRunnable);
        if(event.getAction() == MotionEvent.ACTION_UP) {
            postDelayed(showRunnable, 200);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        removeCallbacks(showRunnable);
        super.onScrollChanged(l, t, oldl, oldt);
        if(listener != null)
            listener.onHideChrome();
    }
}
