package labrom.litlbro.widget;

import android.content.Context;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.List;

import labrom.litlbro.R;
import labrom.litlbro.data.History;
import labrom.litlbro.icon.IconCache;
import labrom.litlbro.util.UrlUtil;
import labrom.litlbro.widget.ShortcutView.OnShortcutActionListener;
import labrom.litlbro.widget.ShortcutsPage.OnEditModeListener;

/**
 * Contains a view pager for shortcuts and a page indicator.
 * @author Romain Laboisse labrom@gmail.com
 *
 */
public class ShortcutsNavigatorView extends RelativeLayout implements OnEditModeListener {
    
    private ShortcutsPage[] shortcutsPages;
    private NavViewPager pager;
    private View nav;
    private boolean editMode;
    private IconCache iconCache;
    private OnClickListener onClickListener;
    private OnShortcutActionListener onShortcutActionListener;
    private OnEditModeListener onEditModeListener;
    private int minMaskWidth;


    public ShortcutsNavigatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShortcutsNavigatorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ShortcutsNavigatorView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.shortcuts_navigator_view, this, true);
        onFinishInflate();
    }
    
    @Override
    protected void onFinishInflate() {
        minMaskWidth = getResources().getInteger(R.integer.shortcutPageIndicatorMaskMin);
        pager = (NavViewPager)findViewById(R.id.pager);
    }
    
    public void setup(int pages) {
        if(this.shortcutsPages == null || pages != this.shortcutsPages.length) {
            this.shortcutsPages = new ShortcutsPage[pages];
            for(int i = 0; i < pages; i ++) {
                ShortcutsPage sp =  new ShortcutsPage(getContext(), i, pages);
                sp.setOnEditModeListener(this);
                this.shortcutsPages[i] = sp;
            }
            pager.setAdapter(new ShortcutsPagerAdapter(this.shortcutsPages));
        }
    }

    
    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        if(this.shortcutsPages != null) {
            for(ShortcutsPage p : this.shortcutsPages)
                p.setEditMode(editMode);
        }
        if(editMode && onEditModeListener != null)
            onEditModeListener.onEditMode();
    }
    
    @Override
    public void onEditMode() {
        setEditMode(true);
    }
    
    public int getShortcutsCountPerPage() {
        if(this.shortcutsPages != null && this.shortcutsPages.length > 0)
            return this.shortcutsPages[0].getNbShortcuts();
        return 0;
    }
    
    public int getPagesCount() {
        return this.shortcutsPages != null ? this.shortcutsPages.length : 0;
    }
    
    public int getRequestedShortcutsCount() {
        return getPagesCount() * getShortcutsCountPerPage();
    }

    public void setShortcutsQueryResult(List<History> result) {
        int index = 0;
        for(ShortcutsPage p : shortcutsPages) {
            p.deactivateAllShortcuts();
            p.setEditMode(editMode);
        }
        int max = getRequestedShortcutsCount();
        int nbPerPage = getShortcutsCountPerPage();
        for(final History h : result) {
            if(index == max)
                break;
            h.detach();
            ShortcutsPage page = shortcutsPages[index / nbPerPage];
            page.activateShortcut(index % nbPerPage, UrlUtil.simplifyHost(h.host), h.isStarred, iconCache.readBitmap(h.host), h, onClickListener, onShortcutActionListener);
            index ++;
        }
    }

    public void setIconCache(IconCache iconCache) {
        this.iconCache = iconCache;
    }

    public OnClickListener getOnShortcutClickListener() {
        return onClickListener;
    }

    public void setOnShortcutClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    
    public OnShortcutActionListener getOnShortcutActionListener() {
        return onShortcutActionListener;
    }

    public void setOnShortcutActionListener(OnShortcutActionListener onShortcutActionListener) {
        this.onShortcutActionListener = onShortcutActionListener;
    }

    public IconCache getIconCache() {
        return iconCache;
    }

    public OnEditModeListener getOnEditModeListener() {
        return onEditModeListener;
    }

    public void setOnEditModeListener(OnEditModeListener onEditModeListener) {
        this.onEditModeListener = onEditModeListener;
    }
    


}
