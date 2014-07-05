package labrom.litlbro.browser;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.io.FileOutputStream;

import labrom.litlbro.ActivityBrowser;
import labrom.litlbro.R;

/**
* Created by labrom on 7/5/14.
*/
public final class ShareScreenshotTask extends AsyncTask<String, Void, Uri> {

    private ActivityBrowser browserActivity;
    private Picture screenshot;

    public ShareScreenshotTask(ActivityBrowser browserActivity) {
        this.browserActivity = browserActivity;
    }

    @Override
    protected void onPreExecute() {
        screenshot = browserActivity.getWebView().capturePicture();
    }

    @Override
    protected Uri doInBackground(String... params) {
        Bitmap receptacle = Bitmap.createBitmap(Math.min(1000, screenshot.getWidth()), Math.min(800, screenshot.getHeight()), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(receptacle);
        screenshot.draw(c);


        String filename = params[0]+ ".png";
        try {
            FileOutputStream out = browserActivity.openFileOutput(filename, Context.MODE_WORLD_READABLE);
            receptacle.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            String fileStreamPath = browserActivity.getFileStreamPath(filename).getAbsolutePath();
            String uri = MediaStore.Images.Media.insertImage(browserActivity.getContentResolver(), fileStreamPath, params[0], null);
            return Uri.parse(uri);
        } catch(Exception e) {
            return null;
        }


    }

    protected void onPostExecute(Uri imageUri) {
        browserActivity.dismissDialog(ActivityBrowser.DIALOG_PROGRESS_SHARE_SCREENSHOT);
        if(imageUri == null)
            return;
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(Intent.EXTRA_TEXT, "Here is a picture of the page at " + browserActivity.getWebView().getUrl());
        i.putExtra(Intent.EXTRA_STREAM, imageUri);
        i.setType("image/png");
        browserActivity.startActivity(Intent.createChooser(i, browserActivity.getString(R.string.shareScreenshotDialogTitle)));

    }
}
