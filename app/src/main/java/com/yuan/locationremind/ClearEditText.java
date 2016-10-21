package com.yuan.locationremind;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

public class ClearEditText extends EditText {

    /**
     * 清楚按钮的图标
     */
    private Drawable mClearDrawable;

    public ClearEditText(Context context) {
        super(context);
        init(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClearEditText);
        int iconClear = typedArray.getResourceId(R.styleable.ClearEditText_iconClear, -1);
        mClearDrawable = getResources().getDrawable(iconClear);
        updateIconClear();
        typedArray.recycle();

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateIconClear();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int xDown = (int) event.getX();
            if (xDown >= (getWidth() - getCompoundPaddingRight()) && xDown < getWidth()) {
                setText("");
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 更新清除按钮图标显示
     */
    public void updateIconClear() {
        Drawable[] drawables = getCompoundDrawables();
        if (length() > 0) {
            setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], mClearDrawable,
                    drawables[3]);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], null,
                    drawables[3]);
        }
    }

    /**
     * 设置清除按钮图标样式
     *
     * @param resId 图标资源id
     */
    public void setIconClear(@DrawableRes int resId) {
        mClearDrawable = getResources().getDrawable(resId);
        updateIconClear();
    }
}