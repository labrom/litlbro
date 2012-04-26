package labrom.litlbro.widget;

import java.util.ArrayList;
import java.util.List;

import labrom.litlbro.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * A single page of shortcuts.
 * @author Romain Laboisse labrom@gmail.com
 *
 */
public class ShortcutsPage extends FrameLayout implements OnLongClickListener {
    
    
    public interface OnEditModeListener {
        void onEditMode();
    }
    
    /**
     * Used to determine to what extent shortcuts become more transparent
     * as they appear late in the pages.
     * Range can be from 0 to 1. 1 means last shortcut is completely transparent,
     * 0 means last shortcut is completely opaque just like the first one.
     */
    private static final float ALPHA_RANGE = 0.5f;
    
    private final List<ShortcutView> shortcuts = new ArrayList<ShortcutView>(12);
    private int pageIndex = 0;
    private int nbPages = 1;
    private boolean editMode;
    private OnEditModeListener onEditModeListener;

    public ShortcutsPage(Context context) {
        super(context);
        inflate();
    }

    public ShortcutsPage(Context context, int pageIndex, int nbPages) {
        super(context);
        
        // Important: fields initialization must happen before layout inflation
        this.pageIndex = pageIndex;
        this.nbPages = nbPages;
        inflate();
    }

    public ShortcutsPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate();
    }
    
    private void inflate() {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.shortcuts_page, this, true);
        initShortcuts();
        setOnLongClickListener(this);
        for(ShortcutView v : shortcuts)
            v.setOnLongClickListener(this);
    }

    private void initShortcuts() {
        traverse(this, 0);
        int nbShortcuts = shortcuts.size();
        double delta = (float)255 * ALPHA_RANGE / (this.nbPages * nbShortcuts);
        int startRank = nbShortcuts * this.pageIndex;
        for (int i = 0; i < nbShortcuts; i++) {
            shortcuts.get(i).setAlpha((int)(255 - (startRank + i) * delta));
        }
    }
    
    private int traverse(ViewGroup grp, int index) {
        int childCount = grp.getChildCount();
        for(int i = 0; i < childCount; i ++) {
            View child = grp.getChildAt(i);
            // Make sure to check on ShortcutView first as ShortcutView is an indirect child class of ViewGroup
            if(child instanceof ShortcutView) {
                shortcuts.add(index ++, (ShortcutView)child);
                child.setEnabled(false);
            }
            else if(child instanceof ViewGroup)
                index = traverse((ViewGroup)child, index);
        }
        return index;
    }
    
    public int getNbShortcuts() {
        return shortcuts.size();
    }

    public void activateShortcut(int index, CharSequence text, boolean starred, Bitmap icon, Object tag, OnClickListener listener, ShortcutView.OnShortcutActionListener actionListener) {
        if(shortcuts == null || index >= shortcuts.size())
            return;
        ShortcutView sh = shortcuts.get(index);
        sh.setTag(tag);
        sh.setText(text);
        sh.setSmallIcon(icon);
        sh.setEnabled(true);
        sh.setStarred(starred);
        sh.update();
        sh.setOnClickListener(listener);
        sh.setOnShortcutActionListener(actionListener);
        sh.setEditMode(editMode);
    }
    
    public void deactivateShortcut(int index) {
        if(shortcuts == null || index >= shortcuts.size())
            return;
        deactivateShortcut(shortcuts.get(index));
    }

    public void deactivateAllShortcuts() {
        if(shortcuts == null)
            return;
        for(ShortcutView sh : shortcuts)
            deactivateShortcut(sh);
    }

    private void deactivateShortcut(ShortcutView sh) {
        sh.setEnabled(false);
        sh.update();
        sh.setTag(null);
        sh.setOnClickListener(null);
        sh.setEditMode(editMode);
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        for(ShortcutView v : shortcuts)
            v.setEditMode(editMode);
    }

    @Override
    public boolean onLongClick(View v) {
        setEditMode(true);
        if(onEditModeListener != null)
            onEditModeListener.onEditMode();
        return true;
    }

    public OnEditModeListener getOnEditModeListener() {
        return onEditModeListener;
    }

    public void setOnEditModeListener(OnEditModeListener onEditModeListener) {
        this.onEditModeListener = onEditModeListener;
    }
    
}
