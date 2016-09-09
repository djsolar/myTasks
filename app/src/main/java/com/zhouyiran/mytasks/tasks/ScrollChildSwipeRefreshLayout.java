package com.zhouyiran.mytasks.tasks;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zhouyiran on 2016/8/30.
 */
public class ScrollChildSwipeRefreshLayout extends SwipeRefreshLayout {

    private View mScrollChildUp;

    public ScrollChildSwipeRefreshLayout(Context context) {
        super(context);
    }

    public ScrollChildSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canChildScrollUp() {
        if(mScrollChildUp != null) {
            return ViewCompat.canScrollVertically(mScrollChildUp, -1);
        }
        return super.canChildScrollUp();
    }

    public void setScrollChildUp(View view) {
        this.mScrollChildUp = mScrollChildUp;
    }
}
