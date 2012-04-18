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
    
    private TextView caption;
    private ImageView favicon;
    private IconView bigIcon;
    private Button remove;
    private OnRemoveShortcutListener onRemoveShortcutListener;
    private Bitmap bigIconBmp;
    private Bitmap faviconBmp;
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
        remove = (Button)findViewById(R.id.remove);
        remove.setOnClickListener(this);
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
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    public void update() {
        boolean enabled = isEnabled();
        caption.setVisibility(enabled ? View.VISIBLE : View.INVISIBLE);
        remove.setVisibility(editMode && enabled ? View.VISIBLE : View.INVISIBLE);
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
        if(v != remove)
            return;
        if(onRemoveShortcutListener != null)
            onRemoveShortcutListener.onRemoveShortcut(this, remove);
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
        this.remove.setVisibility(editMode && isEnabled() ? View.VISIBLE : View.INVISIBLE);
    }

}
