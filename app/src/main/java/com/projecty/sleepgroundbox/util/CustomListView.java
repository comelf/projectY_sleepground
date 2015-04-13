package com.projecty.sleepgroundbox.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by byungwoo on 15. 4. 5..
 */
public class CustomListView extends ListView implements AbsListView.OnScrollListener{

    private HeadAdView headerView;
    private boolean isTitleSticking = false;

    public CustomListView(Context context) {
        super(context);
        init();
    }

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public CustomListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        createHeaderView();
        super.addHeaderView(headerView);
        setOnScrollListener(this);
        configureStickyTitle();
    }

    private final void createHeaderView() {
        headerView = new HeadAdView(getContext());
        headerView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        setParallax();
    }

    private void setParallax() {
        headerView.setHeaderParallax(2);
    }

    private void configureStickyTitle() {
        int dynamicTitleHeight =  1283;
        headerView.configureStickyTitle(dynamicTitleHeight);

    }

    private void shouldStickMode(AbsListView view) {
        final int titleHeight = view.getChildAt(1).getTop() - headerView.getTitleHeight();
        final int offset = 1;
        if (offset <= 0) {
            if (!isTitleSticking) {
                isTitleSticking = true;
                configureStickyTitle();

            }
        } else if (offset > 0
                && getChildAt(getFirstVisiblePosition()) == headerView) {
            if (isTitleSticking) {
                headerView.removeTitle();
            }
            isTitleSticking = false;

        }
    }


}
