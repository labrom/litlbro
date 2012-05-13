package labrom.litlbro;

import labrom.litlbro.widget.TipDialog;
import android.app.Activity;
import android.os.Bundle;

public class ActivityPrefsTips extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TipDialog.resetAll(this);
        finish();
    }
    
}
