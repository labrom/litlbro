package labrom.litlbro.browser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import labrom.litlbro.R;
import labrom.litlbro.util.UrlUtil;

public class ShareScreenshotTask extends AsyncTask<String, Void, Uri> {

    private static final int MAX_DIM = 1500;

    private Activity activity;
    private BroWebView webView;
    private ProgressDialog progressDialog;

    public ShareScreenshotTask(Activity activity, BroWebView webView) {
        this.activity = activity;
        this.webView = webView;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(activity, null, activity.getString(R.string.progressShareScreenshot), true);
    }

    @Override
    protected Uri doInBackground(String... params) {
        Bitmap screenshot = webView.takeScreenshot(MAX_DIM);
        String url = params[0];
        String imageUri = MediaStore.Images.Media.insertImage(activity.getContentResolver(), screenshot,
                UrlUtil.getDomain(url), // Title
                activity.getString(R.string.shareScreenShotImageDescriptionFormat, url, MAX_DIM, MAX_DIM) // Description
        );
        return Uri.parse(imageUri);
    }

    protected void onPostExecute(Uri imageUri) {
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
        if(imageUri == null)
            return;
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.shareScreenShotMessageBodyFormat, webView.getUrl()));
        i.putExtra(Intent.EXTRA_STREAM, imageUri);
        i.setType("image/png");
        activity.startActivity(Intent.createChooser(i, activity.getString(R.string.shareScreenshotDialogTitle)));
    }
}
