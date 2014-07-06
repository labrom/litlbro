package labrom.litlbro.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author Romain Laboisse labrom@gmail.com
 */
public class PageLoadingView extends TextView {

    private ProgressLabelDrawable progressLabelDrawable;
    private int originalPaddingLeft, originalPaddingRight;

    public PageLoadingView(Context context) {
        super(context);
        setup();
    }

    public PageLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public PageLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {
        progressLabelDrawable = new ProgressLabelDrawable();
        progressLabelDrawable.setBackgroundColor(ColorDrawable.class.cast(getBackground()).getColor());
        progressLabelDrawable.setProgressColor(getCurrentTextColor());
        setBackground(progressLabelDrawable);
        originalPaddingLeft = getPaddingLeft();
        originalPaddingRight = getPaddingRight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        progressLabelDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        int newPaddingLeft = originalPaddingLeft + getMeasuredHeight() / 2;
        int newPaddingRight = originalPaddingRight + getMeasuredHeight();
        if (newPaddingLeft != getPaddingLeft() || newPaddingRight != getPaddingRight()) {
            setPadding(newPaddingLeft, getPaddingTop(), newPaddingRight, getPaddingBottom());
            invalidate();
        }
    }

    public void setProgress(int progress) {
        progressLabelDrawable.setLevel(progress * 100);
    }

    public int getProgress() {
        return progressLabelDrawable.getLevel();
    }
}
