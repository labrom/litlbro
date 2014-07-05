package labrom.litlbro;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

/**
 * Created by labrom on 3/6/14.
 */
public final class BroWebView extends WebView {

    /**
     * A callback interface used to listen for system UI visibility changes.
     */
    public interface OnSystemUiVisibilityChangeListener {
        /**
         * Called when the system UI visibility has changed.
         *
         * @param visible True if the system UI is systemUiVisible.
         */
        public void onVisibilityChange(boolean visible);
    }

    /**
     * A dummy no-op callback for use when there is no other listener set.
     */
    private static final OnSystemUiVisibilityChangeListener DUMMY_LISTENER = new OnSystemUiVisibilityChangeListener() {
        @Override
        public void onVisibilityChange(boolean visible) {
        }
    };

    private final Runnable showRunnable = new Runnable() {
        @Override
        public void run() {
            showSystemUi();
        }
    };

    private final Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            hideSystemUi();
        }
    };

    /**
     * Whether or not the system UI should be hidden.
     */
    private boolean shouldHideSystemUi = true;

    /**
     * The current system UI visibility callback.
     */
    private OnSystemUiVisibilityChangeListener onSystemUiVisibilityChangeListener = DUMMY_LISTENER;

    /**
     * Whether or not the system UI is currently systemUiVisible. This is cached from
     * {@link android.view.View.OnSystemUiVisibilityChangeListener}.
     */
    private boolean systemUiVisible = true;


    public BroWebView(Context context) {
        super(context);
    }

    public BroWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BroWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    /**
     * Hide the system UI.
     */
    public void hideSystemUi() {
        if (!shouldHideSystemUi) return;
        setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    /**
     * Show the system UI.
     */
    public void showSystemUi() {
        if (!shouldHideSystemUi) return;
        setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    /**
     * Returns whether or not the system UI is systemUiVisible.
     */
    public boolean isSystemUiVisible() {
        return systemUiVisible;
    }

    /**
     * Returns whether or not the system UI should be hidden.
     * @return
     */
    public boolean shouldHideSystemUi() {
        return shouldHideSystemUi;
    }

    /**
     * Sets whether or not the system UI should be hidden.
     * @param hideSystemUi
     */
    public void setShouldHideSystemUi(boolean hideSystemUi) {
        this.shouldHideSystemUi = hideSystemUi;
        if (!systemUiVisible) showSystemUi();
    }

    /**
     * Sets a listener to system UI visibility changes.
     */
    public void setOnSystemUiVisibilityChangeListener(OnSystemUiVisibilityChangeListener listener) {
        onSystemUiVisibilityChangeListener = listener;
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
        hideSystemUi();
    }

    public void delayedHideSystemUi(int delayMillis) {
        removeCallbacks(hideRunnable);
        postDelayed(hideRunnable, delayMillis);
    }

    @Override
    public void onWindowSystemUiVisibilityChanged(int visibility) {
        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) != 0) { // Hidden
            systemUiVisible = false;
            onSystemUiVisibilityChangeListener.onVisibilityChange(false);
        }
        else { // Shown
            systemUiVisible = true;
            if (shouldHideSystemUi) delayedHideSystemUi(3000); // Automatically hide the system UI after a few seconds
            onSystemUiVisibilityChangeListener.onVisibilityChange(true);
        }
        super.onWindowSystemUiVisibilityChanged(visibility);
    }
}
