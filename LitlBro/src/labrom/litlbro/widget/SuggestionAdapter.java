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
    
    
    
    public SuggestionAdapter(Context ctx, List<Suggestion> suggs) {
        this.inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        // FIXME
        /*
        03-20 08:52:12.390: V/LITLBRO(1844): Content changed, notifying
        03-20 08:52:12.390: W/dalvikvm(1844): threadid=19: thread exiting with uncaught exception (group=0x4001e578)
        03-20 08:52:12.400: E/AndroidRuntime(1844): FATAL EXCEPTION: AsyncTask #5
        03-20 08:52:12.400: E/AndroidRuntime(1844): java.lang.RuntimeException: An error occured while executing doInBackground()
        03-20 08:52:12.400: E/AndroidRuntime(1844):     at android.os.AsyncTask$3.done(AsyncTask.java:200)
        03-20 08:52:12.400: E/AndroidRuntime(1844):     at java.util.concurrent.FutureTask$Sync.innerSetException(FutureTask.java:274)
        03-20 08:52:12.400: E/AndroidRuntime(1844):     at java.util.concurrent.FutureTask.setException(FutureTask.java:125)
        03-20 08:52:12.400: E/AndroidRuntime(1844):     at java.util.concurrent.FutureTask$Sync.innerRun(FutureTask.java:308)
        03-20 08:52:12.400: E/AndroidRuntime(1844):     at java.util.concurrent.FutureTask.run(FutureTask.java:138)
        03-20 08:52:12.400: E/AndroidRuntime(1844):     at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1088)
        03-20 08:52:12.400: E/AndroidRuntime(1844):     at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:581)
        03-20 08:52:12.400: E/AndroidRuntime(1844):     at java.lang.Thread.run(Thread.java:1019)
        03-20 08:52:12.400: E/AndroidRuntime(1844): Caused by: android.view.ViewRoot$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.
        03-20 08:52:12.400: E/AndroidRuntime(1844):     at android.view.ViewRoot.checkThread(ViewRoot.java:3088)
        03-20 08:52:12.400: E/AndroidRuntime(1844):     at android.view.ViewRoot.requestLayout(ViewRoot.java:669)
        03-20 08:52:12.400: E/AndroidRuntime(1844):     at android.view.View.requestLayout(View.java:8406)
        03-20 08:52:12.400: E/AndroidRuntime(1844):     at android.view.View.requestLayout(View.java:8406)
        03-20 08:52:12.400: E/AndroidRuntime(1844):     at android.view.View.requestLayout(View.java:8406)
        03-20 08:52:12.400: E/AndroidRuntime(1844):     at android.view.View.requestLayout(View.java:8406)
        03-20 08:52:12.400: E/AndroidRuntime(1844):     at android.widget.AbsListView.requestLayout(AbsListView.java:1202)
        03-20 08:52:12.400: E/AndroidRuntime(1844):     at android.widget.AdapterView$AdapterDataSetObserver.onChanged(AdapterView.java:790)
        03-20 08:52:12.400: E/AndroidRuntime(1844):     at android.database.DataSetObservable.notifyChanged(DataSetObservable.java:31)
        03-20 08:52:12.400: E/AndroidRuntime(1844):     at android.widget.BaseAdapter.notifyDataSetChanged(BaseAdapter.java:50)
        03-20 08:52:12.400: E/AndroidRuntime(1844):     at labrom.litlbro.suggestion.SuggestionAdapter.update(SuggestionAdapter.java:52)
        03-20 08:52:12.400: E/AndroidRuntime(1844):     at java.util.Observable.notifyObservers(Observable.java:139)
        03-20 08:52:12.400: E/AndroidRuntime(1844):     at java.util.Observable.notifyObservers(Observable.java:114)
        03-20 08:52:12.400: E/AndroidRuntime(1844):     at labrom.litlbro.util.ObservableListProxy.notifyObservers(ObservableListProxy.java:37)
        03-20 08:52:12.400: E/AndroidRuntime(1844):     at labrom.litlbro.util.ObservableListProxy.forceNotify(ObservableListProxy.java:31)
        03-20 08:52:12.400: E/AndroidRuntime(1844):     at labrom.litlbro.suggestion.SuggestionList.merge(SuggestionList.java:77)
        03-20 08:52:12.400: E/AndroidRuntime(1844):     at labrom.litlbro.suggestion.GetSuggestionsTask.doInBackground(GetSuggestionsTask.java:59)
        03-20 08:52:12.400: E/AndroidRuntime(1844):     at labrom.litlbro.suggestion.GetSuggestionsTask.doInBackground(GetSuggestionsTask.java:1)
        03-20 08:52:12.400: E/AndroidRuntime(1844):     at android.os.AsyncTask$2.call(AsyncTask.java:185)
        03-20 08:52:12.400: E/AndroidRuntime(1844):     at java.util.concurrent.FutureTask$Sync.innerRun(FutureTask.java:306)
        03-20 08:52:12.400: E/AndroidRuntime(1844):     ... 4 more         
        */
        notifyDataSetChanged();
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
