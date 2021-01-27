package com.studita.presentation.views;

import android.content.Context;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;

public class NoEmojiEditText extends androidx.appcompat.widget.AppCompatEditText {
    public NoEmojiEditText(Context context) {
        super(context);
        init();
    }

    public NoEmojiEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoEmojiEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setFilters(new InputFilter[]{new EmojiExcludeFilter()});
    }

    private class EmojiExcludeFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            StringBuilder result = new StringBuilder();
            for (int i = start; i < end; i++) {
                int type = Character.getType(source.charAt(i));
                if (!(type == Character.SURROGATE || type == Character.OTHER_SYMBOL)) {
                    result.append(source.charAt(i));
                }
            }
            if(result.length() != source.length())
                return result.toString().replaceAll("[^\\x00-\\x7F]", "");
            else
            return null;
        }
    }
}
