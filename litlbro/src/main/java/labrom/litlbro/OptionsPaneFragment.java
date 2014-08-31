package labrom.litlbro;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import labrom.litlbro.browser.ShareScreenshotTask;
import labrom.litlbro.data.DBHistoryManager;
import labrom.litlbro.data.Database;
import labrom.litlbro.data.HistoryManager;

public class OptionsPaneFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    public static final int RESULT_CODE_SHARE_SCREENSHOT = 10;

    public static OptionsPaneFragment newInstance(String url) {
        OptionsPaneFragment frag = new OptionsPaneFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        frag.setArguments(args);
        return frag;
    }

    private String url;
    private CompoundButton optionsStarToggle;
    private Database db;
    private HistoryManager history;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.url = getArguments().getString("url");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.options_pane, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.optionsStarToggle = (CompoundButton) view.findViewById(R.id.star);
        this.optionsStarToggle.setOnCheckedChangeListener(this);

        view.findViewById(R.id.share).setOnClickListener(this);
        view.findViewById(R.id.shareScreenshot).setOnClickListener(this);
        view.findViewById(R.id.prefs).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.db = Database.create(getActivity().getApplicationContext());
        this.history = new DBHistoryManager(this.db);
        this.optionsStarToggle.setChecked(history.isStarred(url));
    }

    @Override
    public void onStop() {
        this.db.close();
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share:
                share();
                return;
            case R.id.shareScreenshot:
                shareScreenshot();
                return;
            case R.id.prefs:
                ActivityPrefs.startBy(getActivity());
                return;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton v, boolean checked) {
        switch (v.getId()) {
            case R.id.star:
                toggleStarred(checked);
                return;
        }
    }

    private void share() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_TEXT, this.url);
        i.setType("text/plain");
        startActivity(Intent.createChooser(i, getString(R.string.shareDialogTitle)));
    }

    private void toggleStarred(boolean star) {
        if (this.history == null) return;
        if (star) {
            this.history.starUrl(this.url);
        } else {
            this.history.unstarUrl(this.url);
        }
    }

    private void shareScreenshot() {
        final Activity activity = getActivity();
        if (activity instanceof ActivityBrowser) {
            new ShareScreenshotTask(activity, ActivityBrowser.class.cast(activity).getWebView())
                    .execute(this.url);
        }
        else {
            Intent urlData = new Intent();
            urlData.setData(Uri.parse(this.url));
            activity.setResult(RESULT_CODE_SHARE_SCREENSHOT, urlData);
            getActivity().finish();
        }
    }


}
