package com.color.tools.views;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

import com.hzpd.hflt.R;

public class DotView extends View {
    private Paint p;
    private int paintColor;
    private int lineWidth;
    private int lineHeight;
    private int dashed;//

    public DotView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.StarRatingbar);
        paintColor = typeArray.getColor(R.styleable.DotView_linecolor, Color.parseColor("#dadada"));
        float scale = context.getResources().getDisplayMetrics().density;
        dashed = typeArray.getInt(R.styleable.DotView_dashed, (int) (6 * scale + 0.5f));
        typeArray.recycle();

        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Style.FILL);
        p.setColor(paintColor);

    }

    public DotView(Context context) {
        super(context);

        paintColor = Color.parseColor("#eaeaea");
        float scale = context.getResources().getDisplayMetrics().density;
        dashed = (int) (6 * scale + 0.5f);
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Style.FILL);
        p.setColor(paintColor);

    }


    public int getPaintColor() {
        return paintColor;
    }

    public void setPaintColor(int paintColor) {
        this.paintColor = paintColor;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    public int getLineHeight() {
        return lineHeight;
    }

    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
    }

    public int getDashed() {
        return dashed;
    }

    public void setDashed(int dashed) {
        this.dashed = dashed;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (lineWidth > 10) {
            for (int i = 0; i < lineWidth; i += dashed) {
                i += dashed;
                if (i < lineWidth) {
                    canvas.drawLine(i - dashed, 0, i, 0, p);
                }
            }
        }
        super.onDraw(canvas);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        lineWidth = w;
        lineHeight = h;
        p.setStrokeWidth(lineHeight);
        this.postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        lineWidth = widthMeasureSpec;
        lineHeight = heightMeasureSpec;
    }

}
