package xmu.dayandnight.smallraccoon.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * 自定义的ScrollView，主要用来拦截ScrollView的滑动，触摸事件，可以根据实际需要来改造onInterceptTouchEvent()方法
 */
public class CustomScrollView extends ScrollView {

    float touchDistanceX, touchDistanceY;
    float touchStartX = 0, touchStartY = 0;

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 1.拦截左右滑动事件; 2.拦截在顶部的事件; 3.拦截在底部的事件
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        float endX = 0, endY = 0;
        touchDistanceX = touchDistanceY = 0f;
        if (action == MotionEvent.ACTION_DOWN) {
            touchStartX = ev.getX();
            touchStartY = ev.getY();
        }
        if (action == MotionEvent.ACTION_MOVE) {
            endX = ev.getX();
            endY = ev.getY();
            touchDistanceX += Math.abs(endX - touchStartX);
            touchDistanceY += Math.abs(endY - touchStartY);
            if (touchDistanceX >= touchDistanceY) {// 水平滑动
                return false;
            } else {// 上下滑动
                float directY = endY - touchStartY;// 判断滑动方向
                if (directY < 0) {
                    int diffAtBottom = getChildAt(getChildCount() - 1)
                            .getBottom() - (getHeight() + getScrollY());// 到底部的距离
                    if (diffAtBottom <= 10) {// 到底部了
                        return false;
                    }
                } else {
                    if (getScrollY() <= 50) {// 在顶部范围内
                        return false;
                    }
                }
            }
        }
        return super.onInterceptTouchEvent(ev);
    }
}
