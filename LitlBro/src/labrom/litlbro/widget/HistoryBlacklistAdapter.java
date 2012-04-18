package labrom.litlbro.widget;

import labrom.litlbro.R;
import labrom.litlbro.data.HistoryBlacklist;
import labrom.litlbro.util.UrlUtil;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class HistoryBlacklistAdapter extends ResourceCursorAdapter implements OnClickListener {
    
    public interface OnReinstateListener {
        void onReinstate(HistoryBlacklist bl);
    }
    
    private OnReinstateListener onReinstateListener;
    
    public HistoryBlacklistAdapter(Context context, Cursor c) {
        super(context, R.layout.blacklist_item, c, 0);
    }
    
    public OnReinstateListener getOnReinstateListener() {
        return onReinstateListener;
    }

    public void setOnReinstateListener(OnReinstateListener onReinstateListener) {
        this.onReinstateListener = onReinstateListener;
    }

    @Override
    public HistoryBlacklist getItem(int position) {
        Cursor c = getCursor();
        if(!c.moveToPosition(position))
            throw new IllegalStateException("Unable to move cursor to position " + position);
        return HistoryBlacklist.getFromCursor(c);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        HistoryBlacklist bl = getItem(cursor.getPosition());
        String url = bl.url;
        if(url == null)
            url = bl.host;
        ((TextView)view.findViewById(R.id.url)).setText(UrlUtil.shortenUrl(url));
        View buttn = view.findViewById(R.id.reinstate);
        buttn.setEnabled(true);
        buttn.setTag(bl);
        buttn.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v) {
        v.setEnabled(false);
        Object tag = v.getTag();
        if(tag instanceof HistoryBlacklist && onReinstateListener != null)
            onReinstateListener.onReinstate((HistoryBlacklist)tag);
    }

}
