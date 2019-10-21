package com.yordan.finance.view.ui.customViews;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;


public class PrefixEditText extends AppCompatEditText {

    float originalLeftPadding = -1;
    float suffixWidth = 0;

    public PrefixEditText(Context context) {
        super(context);
    }

    public PrefixEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PrefixEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        calculatePrefix();
    }

    private void calculatePrefix() {
        if (originalLeftPadding == 1) {
            String prefix = (String) getTag();
            if(prefix == null){
                prefix = "";
            }
            float[] widths = new float[prefix.length()];
            getPaint().getTextWidths(prefix, widths);
            for (float w : widths) {
                suffixWidth += w;
            }
            originalLeftPadding = getCompoundPaddingLeft();
            setPadding((int) (suffixWidth + originalLeftPadding),
                    getPaddingRight(), getPaddingTop(),
                    getPaddingBottom());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String prefix = (String) getTag();
        if(prefix == null){
            prefix = "";
        }
        canvas.drawText(prefix, getWidth() - 80 - suffixWidth, getLineBounds(0, null), getPaint());
    }

}
