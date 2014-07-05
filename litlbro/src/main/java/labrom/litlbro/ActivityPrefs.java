package labrom.litlbro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ActivityPrefs extends PreferenceActivity {
    
    public static void startBy(Context ctx) {
        Intent prefs = new Intent(ctx, ActivityPrefs.class);
        ctx.startActivity(prefs);
    }
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
    
    public static boolean useWindowAnimations(SharedPreferences prefs, Resources res) {
        return prefs.getBoolean("useAnimations", res.getBoolean(R.bool.prefUseAnimationsDefault));
    }
    
}
