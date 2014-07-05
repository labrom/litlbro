package labrom.litlbro.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import labrom.litlbro.R;
import labrom.litlbro.icon.IconUtil;

public class IconView extends View {

    private Bitmap icon;
    private Bitmap roundedIcon;

    public IconView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public IconView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IconView(Context context) {
        super(context);
    }
    
    public void setIcon(Bitmap icon) {
        if(roundedIcon != null && !roundedIcon.isRecycled()) {
            roundedIcon.recycle();
        }
        roundedIcon = null;
        this.icon = icon;
        invalidate();
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(roundedIcon == null || roundedIcon.isRecycled()) {
            if(icon == null || icon.isRecycled())
                return;
            roundedIcon = IconUtil.makeRoundCorners(icon, getResources().getDimension(R.dimen.shortcutRoundCorner), new RectF(1, 1, getMeasuredWidth() - 1, getMeasuredHeight() - 1));
        }
        canvas.drawBitmap(roundedIcon, 0, 0, null);
    }
    
}
