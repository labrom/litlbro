package labrom.litlbro;

import android.app.Activity;
import android.os.Bundle;

public class ActivityPageOptions extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.root, OptionsPaneFragment.newInstance(getIntent().getData().toString()))
                    .commit();
        }
    }
}
