package labrom.litlbro.widget;

import labrom.litlbro.R;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;


public class TipDialog extends Dialog {
    
    /* Static */

    public static final int DIALOG_ID = 598324;

    private static TipMeta[] tips = {
        new TipMeta(0, "tipFirst"), // fake tip, we won't display any tip at first
        new TipMeta(R.string.tipSearch, "tipSearch"),
        new TipMeta(R.string.tipShortcuts, "tipShortcuts"),
        new TipMeta(R.string.tipSettings, "tipSettings"),
        };

    public static Dialog createNextTip(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
        for(TipMeta tip : tips) {
            if(!wasShown(prefs, tip)) {
                if(tip.isVoid()) {
                    savePref(prefs, tip);
                    return null;
                }
                return new TipDialog(ctx, tip);
            }
        }
        return null;
    }
    
    public static void resetAll(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
        Editor editor = prefs.edit();
        for(TipMeta tip : tips) {
            editor.remove(tip.prefKey);
        }
        editor.commit();
    }
    
    private static boolean wasShown(SharedPreferences prefs, TipMeta tip) {
        return prefs.getBoolean(tip.prefKey, false);
    }
    
    private static void savePref(SharedPreferences prefs, TipMeta tip) {
        prefs.edit().putBoolean(tip.prefKey, true).commit();
    }

    private static class TipMeta {
        
        final int tipResId;
        final String prefKey;
        
        /**
         * 
         * @param tipResId use 0 for a void tip (i.e. not displayed but next tip will have to wait for the next time).
         * @param prefKey
         */
        public TipMeta(int tipResId, String prefKey) {
            this.tipResId = tipResId;
            this.prefKey = prefKey;
        }
        
        boolean isVoid() {
            return tipResId == 0;
        }
        
    }
    
    
    
    /* Instance */
    
    private final TipMeta tip;

    private TipDialog(Context context, TipMeta tip) {
        super(context, R.style.tipDialogFrame);
        this.tip = tip;
        setCancelable(false);
//        View layout = getLayoutInflater().inflate(R.layout.tip_dialog, null);
        getWindow().setBackgroundDrawable(null);
        setContentView(R.layout.tip_dialog);
        TextView text = (TextView)findViewById(R.id.text);
        text.setText(tip.tipResId);
        View btn = findViewById(R.id.ok);
        btn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
                savePref(prefs, TipDialog.this.tip);
                dismiss();
            }
        });
    }

}
