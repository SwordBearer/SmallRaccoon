package xmu.dayandnight.smallraccoon.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author SwordBearer  e-mail :ranxiedao@163.com
 *         Created by SwordBearer on 08/04/2014
 */

/**
 * 自适应的ViewGroup控件，可以根据内容自动换行
 */
public class AdaptiveViewGroup extends ViewGroup {
    private int childMarginLeft = 0, childMarginTop = 0, childMarginRight = 0,
            childMarginBottom = 0;

    public interface OnChildViewClickListener {
        public void onClicked(int i);
    }

    public AdaptiveViewGroup(Context context) {
        super(context);
    }

    public AdaptiveViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdaptiveViewGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        for (int index = 0; index < getChildCount(); index++) {
            final View child = getChildAt(index);
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        int lengthX = 0;
        int lengthY = 0;
        int maxRowHeight = 0;
        int halfMarginX = childMarginLeft / 2 + childMarginRight / 2;
        int halfMarginY = childMarginTop / 2 + childMarginBottom / 2;
        int row = 1;
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final View child = this.getChildAt(i);
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                int childX = width + halfMarginX;// 控件占据空间的宽度
                int childY = height + halfMarginY;// 控件占据空间的高度
                // 上一行的最大高度
                lengthX += childX;
                if (lengthX > r) {// 宽度超出，则换行
                    lengthX = childX;// 改变行宽
                    lengthY += childY;
                    row++;
                } else {
                    if (childY > maxRowHeight) {
                        lengthY += childY - maxRowHeight;
                    }
                    maxRowHeight = Math.max(maxRowHeight, childY);
                }
                child.layout(lengthX - childX, lengthY - childY, lengthX,
                        lengthY);
            }
        }
        // 重新设置ViewGroup的高度，因为自定义的ViewGroup高度自动会变成MATCH_PARENT
        LayoutParams lp = getLayoutParams();
        lp.height = lengthY;
        setLayoutParams(lp);

        //刷新界面，必须使用异步方式，直接 requestLayout不起作用
        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }

    /**
     * 设置子元素之间的间隔
     *
     * @param left   left-margin
     * @param top    top-margin
     * @param right  right-margin
     * @param bottom bottom-margin
     */
    public void setChildMargin(int left, int top, int right, int bottom) {
        this.childMarginLeft = left;
        this.childMarginTop = top;
        this.childMarginRight = right;
        this.childMarginBottom = bottom;
    }

    public void setChildMargin(int margin) {
        this.setChildMargin(margin, margin, margin, margin);
    }
}
