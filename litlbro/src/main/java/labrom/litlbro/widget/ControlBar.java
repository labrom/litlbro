package labrom.litlbro.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import labrom.litlbro.L;
import labrom.litlbro.R;
import labrom.litlbro.browser.ChromeClient.PagePublisher;
import labrom.litlbro.browser.PageLoadController;

public class ControlBar extends RelativeLayout implements OnCheckedChangeListener, PagePublisher {
    
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
    
    private TextView title;
    private ViewGroup controlPad;
    private ProgressBar progress;
    private CompoundButton jsToggle;
    private PageLoadController pageLoadController;
    private Animation pushPad;
    private Animation pullPad;
    private long lastControlPadShowTime;
    private final Handler handler = new Handler();

    private final Runnable runPushPad = new Runnable() {
        @Override
        public void run() {
            controlPad.startAnimation(pushPad);
        }
    };
    private final Runnable runHidePad = new Runnable() {
        @Override
        public void run() {
            controlPad.setVisibility(View.INVISIBLE);
            setVisibility(View.INVISIBLE);
            setTitle(null);
        }
    };
    private class RunTimedHide implements Runnable {
        private boolean useAnimation;
        public RunTimedHide(boolean useAnimation) {
            this.useAnimation = useAnimation;
        }
        
        @Override
        public void run() {
            if(progress.getProgress() >= HIDE_ON_PROGRESS)
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
        title = (TextView)findViewById(R.id.title);
        controlPad = (ViewGroup)findViewById(R.id.controlPad);
        progress = (ProgressBar)findViewById(R.id.progress);
        jsToggle = (CompoundButton)this.controlPad.findViewById(R.id.jsToggle);
        jsToggle.setOnCheckedChangeListener(this);
        
        // Control pad animations
        pullPad = AnimationUtils.loadAnimation(getContext(), R.anim.pull_control_pad);
        pushPad = AnimationUtils.loadAnimation(getContext(), R.anim.push_control_pad);
        pushPad.setAnimationListener(new AnimationListener() {
            
            @Override
            public void onAnimationStart(Animation animation) {
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(View.INVISIBLE);
                setTitle(null);
            }
        });

    }
    
    public void disableJavascriptToggle() {
        jsToggle.setEnabled(false);
    }
    
    public void setJavascriptEnabled(boolean jsEnabled) {
        Log.d(L.TAG, "Control bar: Javascript " + (jsEnabled ? "enabled" : "disabled"));
        if(jsToggle.isChecked() != jsEnabled) {
            jsToggle.setOnCheckedChangeListener(null);
            jsToggle.setChecked(jsEnabled);
            jsToggle.setOnCheckedChangeListener(this);
        }
        jsToggle.setEnabled(true);
    }
    
    public boolean isJavascriptEnabled() {
        return jsToggle.isChecked();
    }

    public PageLoadController getPageLoadController() {
        return pageLoadController;
    }

    public void setPageLoadController(PageLoadController pageLoader) {
        pageLoadController = pageLoader;
    }

    @Override
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        if(view == this.jsToggle) {
            
            if(this.pageLoadController == null)
                return;

            jsToggle.setEnabled(false);
            pageLoadController.restart(isChecked);
            
        }
    }
    
    
    public void show(boolean useAnimation) {
        handler.removeCallbacks(runPushPad);
        handler.removeCallbacks(runHidePad);
        
        setVisibility(View.VISIBLE);
        lastControlPadShowTime = System.currentTimeMillis() + (useAnimation ? pullPad.getDuration() : 0);
        if(useAnimation)
            controlPad.startAnimation(pullPad);
        setTitle(null);
        controlPad.setVisibility(View.VISIBLE);
        
        runTimedHide = new RunTimedHide(useAnimation);
        handler.postDelayed(runTimedHide, CONTROL_BAR_SHOW_TIME);
    }
    
    public void hide(boolean useAnimation) {
        if(runTimedHide != null)
            handler.removeCallbacks(runTimedHide);
        if(!isShown())
            return;
        
        Runnable hide = useAnimation ? runPushPad : runHidePad;
        int durationShown = (int)(System.currentTimeMillis() - lastControlPadShowTime);
        int delay = CONTROL_PAD_MIN_SHOW_TIME - durationShown;
        if(delay > 0) {
            handler.postDelayed(hide, delay);
        } else {
            hide.run();
        }
    }

    @Override
    public void setTitle(String title) {
        this.title.setText(title == null ? "" : title);
    }

    @Override
    public void setProgress(int progress) {
        this.progress.setProgress(progress);
    }
    
    @Override
    public void setIcon(Bitmap icon) {
        // Does nothing
    }
    
}
