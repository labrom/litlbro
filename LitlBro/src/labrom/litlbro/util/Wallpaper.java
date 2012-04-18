package labrom.litlbro.util;

import labrom.litlbro.L;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class Wallpaper {
    
    
    public static Drawable get(Context ctx) {
        try {
            Class<?> wallpaperManagerClass = Class.forName("android.app.WallpaperManager");
            Object mgr = wallpaperManagerClass.getDeclaredMethod("getInstance", Context.class).invoke(null, ctx);
            return (Drawable)wallpaperManagerClass.getDeclaredMethod("getFastDrawable").invoke(mgr);
        } catch(Exception e) {
            Log.w(L.TAG, "WallpaperManager not available: " + e.getMessage());
        }
        return null;
    }

}
