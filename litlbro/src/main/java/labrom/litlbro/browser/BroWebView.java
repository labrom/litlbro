package labrom.litlbro.browser;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

/**
 * Created by labrom on 3/6/14.
 */
public final class BroWebView extends WebView {

    private static boolean canPauseResumeTimers = true;
    public static final int HIDE_DELAY = 4000;

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
     * Whether or not the system UI should be automatically hidden after some time.
     */
    private boolean shouldAutoHideSystemUi = true;

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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP) {
            if (!systemUiVisible) showSystemUi();
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        showSystemUi();
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        showSystemUi();
    }

    /**
     * Hide the system UI.
     */
    public void hideSystemUi() {
        removeCallbacks(hideRunnable);
        if (!shouldHideSystemUi || !systemUiVisible) return;
        setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    /**
     * Show the system UI. It will automatically be hidden after some time.
     */
    public void showSystemUi() {
        showSystemUi(true);
    }

    /**
     * Show the system UI.
     * @param autoHide Whether or not the system UI should be automatically hidden after some time.
     */
    public void showSystemUi(boolean autoHide) {
        shouldAutoHideSystemUi = autoHide; // This will be used in #onWindowSystemUiVisibilityChanged
        if (!shouldHideSystemUi) return;
        if (systemUiVisible) { // Already shown, either reschedule or cancel auto-hide
            if (autoHide) {
                delayedHideSystemUi(HIDE_DELAY);
            } else {
                removeCallbacks(hideRunnable);
            }
        } else { // System UI not shown, request visible, see #onWindowSystemUiVisibilityChanged
            setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    @Override
    public void onWindowSystemUiVisibilityChanged(int visibility) {
        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) != 0) { // Hidden
            systemUiVisible = false;
            onSystemUiVisibilityChangeListener.onVisibilityChange(false);
        }
        else { // Shown
            systemUiVisible = true;
            if (shouldHideSystemUi && shouldAutoHideSystemUi) delayedHideSystemUi(HIDE_DELAY); // Automatically hide the system UI after a few seconds
            onSystemUiVisibilityChangeListener.onVisibilityChange(true);
        }
        super.onWindowSystemUiVisibilityChanged(visibility);
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

    private void delayedHideSystemUi(int delayMillis) {
        removeCallbacks(hideRunnable);
        postDelayed(hideRunnable, delayMillis);
    }

    public void pause() {
        onPause();
        if (canPauseResumeTimers) {
            try {
                getClass().getMethod("onPauseTimers", (Class<?>[]) null).invoke(this);
            } catch (Exception e) {
                canPauseResumeTimers = false;
                Log.w("WebView", "Cannot invoke onPauseTimers()");
            }
        }
    }

    public void resume() {
        onResume();
        if (canPauseResumeTimers) {
            try {
                getClass().getMethod("onResumeTimers", (Class<?>[]) null).invoke(this);
            } catch (Exception e) {
                canPauseResumeTimers = false;
                Log.w("WebView", "Cannot invoke onResumeTimers()");
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public Bitmap takeScreenshot(int maxWidth) {
        Bitmap image = Bitmap.createBitmap(getWidth(), getWidth(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        draw(canvas);
        return Bitmap.createScaledBitmap(image, maxWidth, maxWidth, true);
    }
}
