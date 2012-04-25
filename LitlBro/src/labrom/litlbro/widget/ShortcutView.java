package labrom.litlbro.widget;

import labrom.litlbro.R;
import labrom.litlbro.icon.IconMap;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class ShortcutView extends FrameLayout implements OnClickListener {
    
    public interface OnRemoveShortcutListener {
        void onRemoveShortcut(ViewGroup shortcutView, View removeButton);
    }
    public interface OnStarShortcutListener {
        void onStarShortcut(String host, boolean star);
    }
    
    private TextView caption;
    private ImageView favicon;
    private IconView bigIcon;
    private Button remove;
    private Button starToggle;
    private View editBar;
    private OnRemoveShortcutListener onRemoveShortcutListener;
    private OnStarShortcutListener onStarShortcutListener;
    private Bitmap bigIconBmp;
    private Bitmap faviconBmp;
    private View star;
    private boolean editMode;

    public ShortcutView(Context context) {
        super(context);
    }

    public ShortcutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ShortcutView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        caption = (TextView)findViewById(R.id.caption);
        favicon = (ImageView)findViewById(R.id.favicon);
        bigIcon = (IconView)findViewById(R.id.big_icon);
        editBar = findViewById(R.id.editBar);
        remove = (Button)findViewById(R.id.remove);
        remove.setOnClickListener(this);
        starToggle = (Button)findViewById(R.id.starToggle);
        starToggle.setOnClickListener(this);
        star = findViewById(R.id.starred);
    }
    
    public OnStarShortcutListener getOnStarShortcutListener() {
        return onStarShortcutListener;
    }

    public void setOnStarShortcutListener(OnStarShortcutListener onStarShortcutListener) {
        this.onStarShortcutListener = onStarShortcutListener;
    }

    public OnRemoveShortcutListener getOnRemoveShortcutListener() {
        return onRemoveShortcutListener;
    }

    public void setOnRemoveShortcutListener(OnRemoveShortcutListener onRemoveShortcutListener) {
        this.onRemoveShortcutListener = onRemoveShortcutListener;
    }

    public void setText(CharSequence text) {
        caption.setText(text);
    }
    
    public void setSmallIcon(Bitmap icon) {
        faviconBmp = icon;
    }
    
    public void setStarred(boolean starred) {
        star.setVisibility(starred ? View.VISIBLE : View.INVISIBLE);
    }
    
    private void onToggleStar(View toggleButton) {
        boolean currentlyStarred = star.getVisibility() == View.VISIBLE;
        setStarred(!currentlyStarred);
        if(onStarShortcutListener != null)
            onStarShortcutListener.onStarShortcut(caption.getText().toString(), !currentlyStarred);
    }

    public void update() {
        boolean enabled = isEnabled();
        caption.setVisibility(enabled ? View.VISIBLE : View.INVISIBLE);
        setEditMode(editMode);
        if(enabled) {
            String text = caption.getText().toString();
            if(text.length() > 0) {
                int iconId = IconMap.getIconResourceId(text.toString());
                if(iconId > 0) {
                    bigIconBmp = ((BitmapDrawable)getResources().getDrawable(iconId)).getBitmap();
                } else {
                    bigIconBmp = null;
                }
            }
            if(bigIconBmp != null) {
                bigIcon.setIcon(bigIconBmp);
                favicon.setVisibility(View.INVISIBLE);
            } else {
                bigIcon.setIcon(null);
                if(faviconBmp != null) {
                    favicon.setImageBitmap(faviconBmp);
                    favicon.setVisibility(View.VISIBLE);
                } else {
                    favicon.setVisibility(View.INVISIBLE);
                }
            }
        } else {
            caption.setText(null);
            bigIcon.setIcon(null);
            favicon.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if(v == remove && onRemoveShortcutListener != null)
            onRemoveShortcutListener.onRemoveShortcut(this, remove);
        else if(v == starToggle && onStarShortcutListener != null)
            onToggleStar(v);
    }
    
    public void setAlpha(int alpha) {
        Drawable bg = getBackground();
        if(bg != null)
            bg.setAlpha(alpha);
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        this.editBar.setVisibility(editMode && isEnabled() ? View.VISIBLE : View.INVISIBLE);
    }

}
