package labrom.litlbro.widget;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import labrom.litlbro.L;
import labrom.litlbro.R;
import labrom.litlbro.data.HistorySuggestion;
import labrom.litlbro.suggestion.Suggestion;
import labrom.litlbro.util.ObservableListProxy;
import labrom.litlbro.util.UrlUtil;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SuggestionAdapter extends BaseAdapter implements Observer {
    
    private ObservableListProxy<Suggestion> suggestions;
    private LayoutInflater inflater;
    
    private final Handler handler;
    private final Runnable notifier = new Runnable() {
		
		@Override
		public void run() {
			notifyDataSetChanged();
		}
	};
    
    public SuggestionAdapter(Context ctx, List<Suggestion> suggs) {
        this.inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.handler = new Handler(Looper.getMainLooper());
        setSuggestions(suggs);
    }
    
    public SuggestionAdapter(Context ctx) {
        this(ctx, null);
    }

    public List<Suggestion> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<Suggestion> suggestions) {
        if(suggestions == null)
            this.suggestions = new ObservableListProxy<Suggestion>();
        else
            this.suggestions = new ObservableListProxy<Suggestion>(suggestions);
        this.suggestions.addObserver(this);
        notifyDataSetChanged();
    }
    
    @Override
    public void update(Observable o, Object data) {
        Log.v(L.TAG, "Content changed, notifying");
        this.handler.post(this.notifier);
    }


    @Override
    public int getCount() {
        return this.suggestions.size();
    }

    @Override
    public Object getItem(int position) {
        return this.suggestions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.suggestions.get(position).getId();
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder vh = null;
        if(v == null) {
            v = this.inflater.inflate(R.layout.suggestion, null);
            vh = new ViewHolder(v);
        } else {
            vh = (ViewHolder)v.getTag();
        }
        
        Suggestion sugg = this.suggestions.get(position);
        
        int resId = sugg.getIconResouceId();
        if(sugg.isStarred()) {
            vh.icon.setVisibility(View.VISIBLE);
            vh.icon.setImageResource(R.drawable.star_icon);
        } else if(resId > 0) {
            vh.icon.setImageResource(resId);
            vh.icon.setVisibility(View.VISIBLE);
        } else {
            vh.icon.setVisibility(View.GONE);
        }
        // TODO Handle iconUrl
        
        String text = sugg.getTitle();
        String url = UrlUtil.shortenUrl(sugg.getUrl());
        if(text == null)
            text = url;
        vh.caption.setText(text);
        if(sugg instanceof HistorySuggestion && !text.equals(url)) {
            vh.url.setVisibility(View.VISIBLE);
            vh.url.setText(url);
        } else {
            vh.url.setVisibility(View.GONE);
            vh.url.setText("");
        }
        
        return v;
    }
    
    private class ViewHolder {
        TextView caption;
        TextView url;
        ImageView icon;
        ViewHolder(View v) {
            caption = (TextView)v.findViewById(R.id.caption);
            url = (TextView)v.findViewById(R.id.url);
            icon = (ImageView)v.findViewById(R.id.icon);
            v.setTag(this);
        }
    }


}
