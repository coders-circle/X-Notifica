package com.fabb.notifica;


import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {
//    private ListView mListView;

    public CustomSwipeRefreshLayout(Context context) {
        super(context);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    public void setListView(ListView view) {
//        mListView = view;
//    }

//    @Override
//    public boolean canChildScrollUp() {
//        return mListView != null && mListView.getAdapter().getCount() > 0 &&
//                (mListView.getFirstVisiblePosition() > 0
//                        || mListView.getChildAt(0) == null
//                        || mListView.getChildAt(0).getTop() < 0);
//    }
}
