package labrom.litlbro.widget;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/**
 * @author Romain Laboisse labrom@gmail.com
 */
public class ProgressLabelDrawable extends Drawable {

    private Paint backgroundPaint;
    private Paint progressPaint;
    private int progressPadding;

    private void initBackgroundPaint() {
        if (backgroundPaint == null) {
            backgroundPaint = new Paint();
            backgroundPaint.setAntiAlias(true);
        }
    }

    private void initProgressPaint() {
        if (progressPaint == null) {
            progressPaint = new Paint();
            progressPaint.setAntiAlias(true);
        }
    }

    public void setBackgroundColor(int color) {
        initBackgroundPaint();
        backgroundPaint.setColor(color);
    }

    public void setProgressColor(int color) {
        initProgressPaint();
        progressPaint.setColor(color);
    }

    public void setProgressPadding(int progressPadding) {
        this.progressPadding = progressPadding;
    }

    @Override
    public void setAlpha(int i) {
        initBackgroundPaint();
        backgroundPaint.setAlpha(i);
        initProgressPaint();
        progressPaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        initBackgroundPaint();
        backgroundPaint.setColorFilter(colorFilter);
        initProgressPaint();
        progressPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();

        // Paint background
        canvas.drawRoundRect(new RectF(bounds), bounds.height() / 2, bounds.height() / 2, backgroundPaint);

        // Paint progress on top of background, with a solid color
        Path path = new Path();
        int sweepAngle = getLevel() * 360 / 10000;
        int progressCenterX = bounds.width() - bounds.centerY();
        int progressCenterY = bounds.centerY();
        path.moveTo(progressCenterX, progressCenterY);
        path.lineTo(progressCenterX, progressPadding);
        path.addArc(new RectF(progressCenterX - bounds.centerY() + progressPadding, progressPadding, bounds.width() - progressPadding, bounds.height() - progressPadding), 0, sweepAngle);
        path.lineTo(progressCenterX, progressCenterY);
        canvas.drawPath(path, progressPaint);
    }

    @Override
    protected boolean onLevelChange(int level) {
        invalidateSelf();
        return true;
    }

}
