package com.projecty.sleepgroundbox.util;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.projecty.sleepgroundbox.R;
import com.squareup.picasso.Callback;

/**
 * Created by byungwoo on 15. 4. 5..
 */
public class HeadAdView extends RelativeLayout implements Callback{

    RelativeLayout top;
    TextView dynamicTitle;
    FrameLayout ad;
    TextView ad_text;

    public HeadAdView(Context context) {
        super(context);
        init();
        onFinishInflate();
    }

    public HeadAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.list_view_header, this);
        top = (RelativeLayout) findViewById(R.id.top_layout);
        ad = (FrameLayout) findViewById(R.id.header_ad);
        ad_text = (TextView) findViewById(R.id.ad_text);
        dynamicTitle = new TextView(getContext());
    }

    @Override
    public void onSuccess() {
    }

    @Override
    public void onError() {

    }

    public void setHeaderParallax(int parallaxMultiplier) {
        if(top.getTop() >100) {
//            top.setTop(-getTop() / parallaxMultiplier);
        }
    }

    public void configureStickyTitle(int height) {
        dynamicTitle.setLayoutParams(
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ad.getHeight()));

        dynamicTitle.setText("AD");
        dynamicTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        dynamicTitle.setTextColor(Color.parseColor("#FFFFFFFF"));
        ViewGroup parent = (ViewGroup) top.getParent().getParent();

        if (parent != null) {
            parent.addView(dynamicTitle);
        }

        dynamicTitle.setY(height);
    }

    public int getTitleHeight() {
        return ad.getHeight();
    }

    public void removeTitle() {
        ((ViewGroup) getParent().getParent()).removeView(dynamicTitle);
    }
}
