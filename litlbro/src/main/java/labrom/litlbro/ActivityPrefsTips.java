package labrom.litlbro;

import android.app.Activity;
import android.os.Bundle;

import labrom.litlbro.widget.TipDialog;

public class ActivityPrefsTips extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TipDialog.resetAll(this);
        finish();
    }
    
}
