package labrom.litlbro.widget;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class ShortcutsPagerAdapter extends PagerAdapter {
    
    private View[] pages;
    
    public ShortcutsPagerAdapter(View... pages) {
        this.pages = pages;
    }

    @Override
    public int getCount() {
        return pages.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(pages[position]);
        return pages[position];
    }
    
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if(object instanceof View) {
            container.removeView((View)object);
        }
    }

}
