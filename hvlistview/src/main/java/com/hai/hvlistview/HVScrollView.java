/**
 * Copyright (c) 2016, andjdk@163.com All Rights Reserved.
 * #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG            #
 * #                                                   #
 */
package com.hai.hvlistview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.ArraySet;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;


/**
 * Created by andjdk on 2015/11/3.
 */
public class HVScrollView extends LinearLayout {

    private float mStartX = 0;
    private int mMoveOffsetX = 0;
    private int mFixX = 0;

    private String[] mScrollHeaderTitle = new String[]{};
    private String mFixedHeaderTitle;
    private ScrollConfig mScrollConfig;
    private int mHeaderHeight = 75;
    private LinearLayout mScrollHeaderContainer;
    private Set<View> mScrollContainerViews = new ArraySet<>();
    private int mMovableTotalWidth = 0;
    private ListView mStockListView;
    private BaseAdapter mAdapter;

    private Context context;

    public HVScrollView(Context context) {
        this(context, null);
    }

    public HVScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HVScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        this.context = context;
    }

    private void initView() {
        removeAllViews();//第二次调用的时候移除之前添加的View
        mMovableTotalWidth = 0;

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(buildHeadLayout(), layoutParams);
        addView(buildMoveableListView(), layoutParams);
    }

    private View buildHeadLayout() {
        LinearLayout headLayout = new LinearLayout(getContext());
        headLayout.setGravity(Gravity.CENTER_VERTICAL);

        addListHeaderTextView(mFixedHeaderTitle, mScrollConfig.getFixedColWidth(), headLayout);

        mScrollHeaderContainer = new LinearLayout(getContext());
        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        //每个滚动header的width
        double width = 1.0 * (widthPixels - mScrollConfig.getFixedColWidth()) / mScrollConfig.getShowCols();
        for (int i = 0; i < mScrollHeaderTitle.length; i++) {
            TextView textView = addListHeaderTextView(mScrollHeaderTitle[i], (int) width, mScrollHeaderContainer);
            /**
             * 这里可以设置列header的点击事件
             */
        }
        headLayout.addView(mScrollHeaderContainer, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.
                WRAP_CONTENT));
        return headLayout;
    }

    private View buildMoveableListView() {
        mStockListView = new ListView(getContext());
        mStockListView.setDividerHeight(1);
        mStockListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mAdapter.getCount();
            }

            @Override
            public Object getItem(int position) {
                return mAdapter.getItem(position);
            }

            @Override
            public long getItemId(int position) {
                return mAdapter.getItemId(position);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View adapterView = mAdapter.getView(position, convertView, parent);
                View scrollView = adapterView.findViewById(mScrollConfig.getScrollParentViewId());
                mScrollContainerViews.add(scrollView);

                if (mScrollConfig != null) {
                    int rightWidth = getMeasuredWidth() - mScrollConfig.getFixedColWidth();
                    int avgWidth = rightWidth / mScrollConfig.getShowCols();
                    if (mScrollConfig.getShowViewIds() != null) {
                        for (int j = 0; j < mScrollConfig.getShowViewIds().length; j++) {
                            ViewGroup.LayoutParams layoutParams = adapterView.findViewById(mScrollConfig.getShowViewIds()[j]).getLayoutParams();
                            layoutParams.width = avgWidth;
                            adapterView.findViewById(mScrollConfig.getShowViewIds()[j]).setLayoutParams(layoutParams);
                        }
                    }
                }
                return adapterView;
            }
        });
        return mStockListView;
    }

    private TextView addListHeaderTextView(String headerName, int headerWidth, LinearLayout fixHeadLayout) {
        TextView textView = new TextView(getContext());
        textView.setText(headerName);
        textView.setGravity(Gravity.CENTER);
        fixHeadLayout.addView(textView, headerWidth, mHeaderHeight);
        return textView;
    }

    public void setAdapter(@NonNull BaseAdapter adapter) {
        this.mAdapter = adapter;
        initView();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                int offsetX = (int) Math.abs(ev.getX() - mStartX);
                if (offsetX > 30) {
                    return true;
                } else {
                    return false;
                }
            case MotionEvent.ACTION_UP:
                actionUP();
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private void actionUP() {
        if (mFixX < 0) {
            mFixX = 0;
            mScrollHeaderContainer.scrollTo(0, 0);
            scrollTo(mFixX);
        } else {
            if (mScrollHeaderContainer.getWidth() + Math.abs(mFixX) > MovableTotalWidth()) {
                int pointX = MovableTotalWidth() - mScrollHeaderContainer.getWidth();
                mScrollHeaderContainer.scrollTo(pointX, 0);
                scrollTo(pointX);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                return true;
            case MotionEvent.ACTION_MOVE:
                int offsetX = (int) Math.abs(event.getX() - mStartX);
                if (offsetX > 30) {
                    mMoveOffsetX = (int) (mStartX - event.getX() + mFixX);
                    if (0 > mMoveOffsetX) {
                        mMoveOffsetX = 0;
                    } else {
                        if ((mScrollHeaderContainer.getWidth() + mMoveOffsetX) > MovableTotalWidth()) {
                            mMoveOffsetX = MovableTotalWidth() - mScrollHeaderContainer.getWidth();
                        }
                    }
                    mScrollHeaderContainer.scrollTo(mMoveOffsetX, 0);

                    scrollTo(mMoveOffsetX);
                }
                break;
            case MotionEvent.ACTION_UP:
                mFixX = mMoveOffsetX; // mFixX + (int) ((int) ev.getX() - mStartX)
                actionUP();
                break;
        }

        return super.onTouchEvent(event);
    }

    private void scrollTo(int x) {
        if (mScrollContainerViews != null) {
            for (View view : mScrollContainerViews) {
                view.scrollTo(x, 0);
            }
        }
    }

    private int MovableTotalWidth() {
        if (0 == mMovableTotalWidth) {
            int rightWidth = getMeasuredWidth() - mScrollConfig.getFixedColWidth();
            mMovableTotalWidth += (rightWidth / mScrollConfig.getShowCols() * mScrollConfig.getShowViewIds().length);
        }
        return mMovableTotalWidth;
    }


    public void setHeaderListData(String fixedHeaderTitle, String[] scrollHeaderTitle) {
        mFixedHeaderTitle = fixedHeaderTitle;
        this.mScrollHeaderTitle = scrollHeaderTitle;

        if (mScrollConfig == null) throw new RuntimeException("please init mScrollConfig first");

    }

    public void setScrollConfig(ScrollConfig mScrollConfig) {
        this.mScrollConfig = mScrollConfig;
    }
}

