package tw.com.voarlese.centeraligntitleactionbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;


public class KeTextView extends android.support.v7.widget.AppCompatTextView {
    public KeTextView(Context context) {
        super(context);
        setAttributes(context, null, 0);
    }
    
    public KeTextView(
            Context context,
            @Nullable
                    AttributeSet attrs) {
        super(context, attrs);
        setAttributes(context, attrs, 0);
    }
    
    public KeTextView(
            Context context,
            @Nullable
                    AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttributes(context,attrs, defStyleAttr);
    }
    
    public void setAttributes(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.KeTextView);
        try {
            String fontName = a.getString(R.styleable.KeTextView_KeTextView_font);
            if (fontName != null) {setFont(fontName);}
        } finally {
            a.recycle();
        }
    }
    
    private void setFont(String fontName){
//        Typeface typeface = FontManager.NotoFont.getFont(FontManager.FontType.valueOf(fontName));
//        if (typeface != null) setTypeface(typeface);
    }
}
