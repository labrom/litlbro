package labrom.litlbro.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ShakeDialog {
    
    private static final String PREF_SHAKE_NEEDS_CONFIRM = "shakeNeedsConfirm";
    private static final String PREF_SHAKED_ONCE = "shakedOnce";


    public interface Listener {
        void onGoHome();
    }
    
    private final Context ctx;
    private final SharedPreferences prefs;
    private final Listener listener;
    
    public ShakeDialog(Context ctx, SharedPreferences prefs, Listener l) {
        this.ctx = ctx;
        this.prefs = prefs;
        this.listener = l;
    }
    
    
    public AlertDialog create() {
        boolean needsConfirm = prefs.getBoolean(PREF_SHAKE_NEEDS_CONFIRM, true);
        if(!needsConfirm)
            return null; // Dialog not needed
        
        final boolean firstTime = !prefs.getBoolean(PREF_SHAKED_ONCE, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        
        CharSequence[] items = {
                firstTime ? "OK, that's cool" : "Yes",
                firstTime ? "OK, but always confirm before" : "Yes, always do that",
                firstTime ? "Cancel" : "No"};
        builder.setCancelable(false);
        builder.setTitle(firstTime ? "You've discovered the shake! It takes you to home screen." : "Go to home screen?");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Editor editor = prefs.edit();
                editor.putBoolean(PREF_SHAKED_ONCE, true);
                switch(item) {
                case 1:
                case 0:
                    editor.putBoolean(PREF_SHAKE_NEEDS_CONFIRM, item == (firstTime ? 1 : 0));
                    listener.onGoHome();
                    break;
                }
                editor.commit();
                dialog.dismiss();
            }
        });
        
        return builder.create();
    }

}
