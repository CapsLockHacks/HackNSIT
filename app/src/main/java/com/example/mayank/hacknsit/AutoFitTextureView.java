package com.example.mayank.hacknsit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * Created by Arpit on 4/9/2016.
 */
public class AutoFitTextureView extends TextureView {
    private int mRatioWidth = 0;
    private int mRatioHeight = 0;
    public AutoFitTextureView(Context context) {
        super(context, null);
    }
    public AutoFitTextureView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }
    public AutoFitTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public void setAspectRatio(int width, int height) {
        if(width < 0 || height < 0)
            throw new IllegalArgumentException("Size cannot be negative");
        mRatioHeight = height;
        mRatioWidth = width;
        requestLayout();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if(0 == mRatioWidth || 0 == mRatioHeight)
            setMeasuredDimension(width, height);
        else
            if(width < height * mRatioWidth / mRatioHeight)
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
            else
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
    }
}
