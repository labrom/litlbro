package labrom.litlbro.widget;

import labrom.litlbro.util.Wallpaper;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class NavViewPager extends ViewPager {
    
    private int pageCount;
    private NavListener navListener;
    
    public interface NavListener {
        void setMaskLevels(int levelLeft, int levelRight);
    }
    
    public NavViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NavViewPager(Context context) {
        super(context);
    }
    
    public void hintPageCount(int count) {
        pageCount = count;
        setIndicator(0, 0);
    }
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        
        // Available after 1.6, sets the shortcut pages background with the device's wallpaper.
        Drawable wallpaper = Wallpaper.get(getContext());
        if(wallpaper != null)
            setBackgroundDrawable(wallpaper);
    }
    
    @Override
    protected void onPageScrolled(int position, float offset, int offsetPixels) {
        super.onPageScrolled(position, offset, offsetPixels);
        setIndicator(position, offset);
    }

    private void setIndicator(int position, float offset) {
        int pages = Math.max(getChildCount(), pageCount);
        if(pages <= 1) {
            navListener.setMaskLevels(0, 0);
            return;
        }
        int levelLeft = (int)(10000 * (position + offset) / pages);
        int levelRight = (int)(10000 * (pages - 1 - position - offset) / pages);
        if(navListener != null) {
            navListener.setMaskLevels(levelLeft, levelRight);
        }
    }

    public NavListener getNavListener() {
        return navListener;
    }

    public void setNavListener(NavListener navListener) {
        this.navListener = navListener;
    }
    
}
