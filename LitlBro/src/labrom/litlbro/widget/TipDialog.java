package labrom.litlbro.widget;

import android.app.AlertDialog;
import android.content.Context;

public class TipDialog extends AlertDialog {

    public TipDialog(Context context, int textResId) {
        super(context, true, null);
        setMessage(context.getText(textResId));
    }
    
    

}
