package com.example.studita.presentation.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.FrameLayout;

import com.example.studita.R;
import com.google.android.material.card.MaterialCardView;

public class AlphaGradientView extends FrameLayout {

    private static final int DEFAULT_GRADIENT_SIZE_DP = 80;

    public static final int FADE_EDGE_TOP = 1;
    private static final int DIRTY_FLAG_TOP = 1;

    private static final int[] FADE_COLORS = new int[]{Color.BLACK, Color.TRANSPARENT};

    private boolean fadeTop;
    private int gradientSizeTop;
    private Paint gradientPaintTop;
    private Rect gradientRectTop;
    private int gradientDirtyFlags;

    public AlphaGradientView(Context context) {
        super(context);
        init(null);
    }

    public AlphaGradientView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public AlphaGradientView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        int defaultSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_GRADIENT_SIZE_DP,
                getResources().getDisplayMetrics());

        if (attrs != null) {
            TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.AlphaGradientView, 0, 0);
            int flags = arr.getInt(R.styleable.AlphaGradientView_fel_edge, 0);

            fadeTop = (flags & FADE_EDGE_TOP) == FADE_EDGE_TOP;

            gradientSizeTop = arr.getDimensionPixelSize(R.styleable.AlphaGradientView_fel_size_top, defaultSize);

            if (fadeTop && gradientSizeTop > 0) {
                gradientDirtyFlags |= DIRTY_FLAG_TOP;
            }

            arr.recycle();
        } else {
            gradientSizeTop = defaultSize;
        }

        PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        gradientPaintTop = new Paint(Paint.ANTI_ALIAS_FLAG);
        gradientPaintTop.setXfermode(mode);

        gradientRectTop = new Rect();
    }

    public void setGradientSizeTop(int gradientSizeTop) {
        this.gradientSizeTop = gradientSizeTop;
    }

    public void setFadeTop(boolean fadeTop) {
        this.fadeTop = fadeTop;
        this.gradientDirtyFlags = DIRTY_FLAG_TOP;
    }


    public boolean getFadeTop() {
        return this.fadeTop;
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        if (getPaddingTop() != top) {
            gradientDirtyFlags |= DIRTY_FLAG_TOP;
        }
        super.setPadding(left, top, right, bottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (h != oldh) {
            gradientDirtyFlags |= DIRTY_FLAG_TOP;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int newWidth = getWidth(), newHeight = getHeight();
        boolean fadeAnyEdge = fadeTop;
        if (getVisibility() == GONE || newWidth == 0 || newHeight == 0 || !fadeAnyEdge) {
            super.dispatchDraw(canvas);
            return;
        }

        if ((gradientDirtyFlags & DIRTY_FLAG_TOP) == DIRTY_FLAG_TOP) {
            gradientDirtyFlags &= ~DIRTY_FLAG_TOP;

            int actualHeight = getHeight() - getPaddingTop() - getPaddingBottom();
            if(gradientSizeTop == 0)
                gradientSizeTop = actualHeight;
            int size = Math.min(gradientSizeTop, actualHeight);
            int l = getPaddingLeft();
            int t = getPaddingTop();
            int r = getWidth() - getPaddingRight();
            int b = t + size;
            gradientRectTop.set(l, t, r, b);
            LinearGradient gradient = new LinearGradient(l, t, l, b, FADE_COLORS, null, Shader.TileMode.CLAMP);
            gradientPaintTop.setShader(gradient);
        }

        int count = canvas.saveLayer(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), null, Canvas.ALL_SAVE_FLAG);
        super.dispatchDraw(canvas);
        if (fadeTop && gradientSizeTop > 0) {
            canvas.drawRect(gradientRectTop, gradientPaintTop);
        }
        canvas.restoreToCount(count);
    }
}