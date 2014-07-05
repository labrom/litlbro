package labrom.litlbro.icon;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

/**
 * Taken from http://stackoverflow.com/questions/1705239/how-should-i-give-images-rounded-corners-in-android
 */
public class IconUtil {
    
    public static final int TOP_LEFT = 1;
    public static final int TOP_RIGHT = 2;
    public static final int BOTTOM_LEFT = 4;
    public static final int BOTTOM_RIGHT = 8;
    
    public static Bitmap makeRoundCorners(Bitmap src, float roundCorner, RectF clip) {
        int w = src.getWidth();
        int h = src.getHeight();
        
        // We have to make sure our rounded corners have an alpha channel in most cases
        Bitmap rounder = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Bitmap output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(rounder);    

        // We're going to apply this paint eventually using a porter-duff xfer mode.
        // This will allow us to only overwrite certain pixels. RED is arbitrary. This
        // could be any color that was fully opaque (alpha = 255)
        Paint xferPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xferPaint.setColor(Color.RED);

//        RectF clip2 = new RectF(0, 0, Math.min(w, clip.width()), Math.min(h, clip.height()));
        // We're just reusing xferPaint to paint a normal looking rounded box, the 20.f
        // is the amount we're rounding by.
        canvas.drawRoundRect(clip, roundCorner, roundCorner, xferPaint);     

        // Now we apply the 'magic sauce' to the paint  
        xferPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas = new Canvas(output);
        canvas.drawBitmap(src, 0, 0, null);
        canvas.drawBitmap(rounder, 0, 0, xferPaint);
        
        return output;
    }
    
    
    public static Bitmap makeRoundCorners(Bitmap src, float roundCorner, int corners) {
        int w = src.getWidth();
        int h = src.getHeight();
        float dx = 0;
        float dy = 0;
        
        if(corners > 0) {
            if((corners & TOP_LEFT) == 0) {
                if((corners & TOP_RIGHT) == 0) {
                    h += roundCorner;
                    dy -= roundCorner;
                } else if((corners & BOTTOM_LEFT) == 0) {
                    w += roundCorner;
                    dx -= roundCorner;
                }
            }
            if((corners & TOP_RIGHT) == 0) {
                if((corners & BOTTOM_RIGHT) == 0) {
                    w += roundCorner;
                }
            }
            if((corners & BOTTOM_RIGHT) == 0) {
                if((corners & BOTTOM_LEFT) == 0) {
                    h += roundCorner;
                }
            }
                
        }

        return makeRoundCorners(src, roundCorner, new RectF(dx, dy, w, h));
    }

}
