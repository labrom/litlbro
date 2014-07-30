package labrom.litlbro.widget;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import labrom.litlbro.R;

public class SearchText extends EditText {

    private static final int[] STATE_SUGGESTIONS_SHOWN = {R.attr.state_suggestions_shown};
	
	private OnDoneHandler onDoneHandler;
    private boolean suggestionsShown;
	
	public SearchText(Context context) {
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
					onDoneHandler.onDone(SearchText.this);
					return false;
	            }
	            return false;
	        }
	    });
	}

	public SearchText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public SearchText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

    @Override
    protected int[] onCreateDrawableState(int l) {
        if (suggestionsShown) {
            final int[] drawableState = super.onCreateDrawableState(l + 1);
            mergeDrawableStates(drawableState, STATE_SUGGESTIONS_SHOWN);
            return drawableState;
        }
        return super.onCreateDrawableState(l);
    }

    public void setSuggestionsShown(boolean suggestionsShown) {
        this.suggestionsShown = suggestionsShown;
        refreshDrawableState();
    }

    public boolean isSuggestionsShown() {
        return suggestionsShown;
    }

    public OnDoneHandler getOnDoneHandler() {
		return onDoneHandler;
	}

	public void setOnDoneHandler(OnDoneHandler doneHandler) {
		this.onDoneHandler = doneHandler;
	}


	public interface OnDoneHandler {
		void onDone(SearchText e);
	}
	

}
