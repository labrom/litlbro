package labrom.litlbro.icon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import labrom.litlbro.L;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

public class IconCache {
    
    private File cacheDir;
    
    public IconCache(File cacheDir) {
        this.cacheDir = cacheDir;
    }

    public void cache(Bitmap icon, String host) {
        File iconFile = new File(cacheDir, host + ".png");
        if(iconFile.exists())
            iconFile.delete();
        OutputStream out;
        try {
            out = new FileOutputStream(iconFile);
            icon.compress(CompressFormat.PNG, 0, out);
            out.close();
        } catch (FileNotFoundException e) {
            Log.e(L.TAG, "Invalid path: " + iconFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e(L.TAG, "Error on icon write: " + iconFile.getAbsolutePath() + " - " + e.getMessage());
        }
    }
    
    public Bitmap readBitmap(String host) {
        File iconFile = new File(cacheDir, host + ".png");
        if(iconFile.exists())
            try {
                return BitmapFactory.decodeFile(iconFile.getCanonicalPath());
            } catch (IOException e) {
                Log.e(L.TAG, "Error on icon read: " + iconFile.getAbsolutePath() + " - " + e.getMessage());
            }
        return null;
    }

}
