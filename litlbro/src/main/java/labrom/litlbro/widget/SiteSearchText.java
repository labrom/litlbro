package labrom.litlbro.widget;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class SiteSearchText extends EditText {
	
	OnDoneHandler onDoneHandler;
	
	public SiteSearchText(Context context) {
		super(context);
		init();
	}

	private void init() {
        setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_FILTER);
		setImeOptions(EditorInfo.IME_ACTION_DONE);
		setOnEditorActionListener(new TextView.OnEditorActionListener() {
	        @Override
	        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	            if(/*actionId == EditorInfo.IME_ACTION_DONE && */onDoneHandler != null) { // IME_ACTION_DONE doesn't work on HTC Eris
					onDoneHandler.onDone(SiteSearchText.this);
					return false;
	            }
	            return false;
	        }
	    });
	}

	public SiteSearchText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public SiteSearchText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	
	public OnDoneHandler getOnDoneHandler() {
		return onDoneHandler;
	}

	public void setOnDoneHandler(OnDoneHandler doneHandler) {
		this.onDoneHandler = doneHandler;
	}


	public interface OnDoneHandler {
		void onDone(SiteSearchText e);
	}
	

}
