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

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

/**
 * 此类未实现功能，还有错误，连基本的滚动手势都实现有误
 * Created by huanghp on 2018/12/19.
 * Email h1132760021@sina.com
 */
public class HVScrollView2 extends LinearLayout {
    private static final String TAG = "HVScrollView";
    private float mStartX = 0;
    private int mMoveOffsetX = 0;
    private int mFixX = 0;
    private int mEndX = 0;
    private int mHeaderHeight = 75;
    private int mMovableTotalWidth = 0;
    private boolean isAnimate2Int = true;

    private int mTouchSlop = 0;//move事件最小阈值
    private int flingVelocity;

    private volatile boolean isAnimate;
    private ValueAnimator animator;

    private String[] mScrollHeaderTitle = new String[]{};
    private String mFixedHeaderTitle;
    private ScrollConfig mScrollConfig;
    private LinearLayout mScrollHeaderContainer;
    private Set<View> mScrollContainerViews = new ArraySet<>();
    private ListView mStockListView;
    private BaseAdapter mAdapter;

    private GestureDetector gestureDetector;

    public HVScrollView2(Context context) {
        this(context, null);
    }

    public HVScrollView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HVScrollView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        flingVelocity = configuration.getScaledMinimumFlingVelocity();

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                //此处第一次滚动的distanceX和手势抬起后第二次滚动后的distanceX值相差巨大
                //此处的实现逻辑有误
                mMoveOffsetX = (int) (distanceX + mFixX);
                if (0 > mMoveOffsetX) {
                    mMoveOffsetX = 0;
                } else {
                    if ((mScrollHeaderContainer.getWidth() + mMoveOffsetX) > MovableTotalWidth()) {
                        Log.e(TAG, "onScroll: (mScrollHeaderContainer.getWidth() + mMoveOffsetX) > MovableTotalWidth()");
                        mMoveOffsetX = MovableTotalWidth() - mScrollHeaderContainer.getWidth();
                    }
                }
                Log.e(TAG, "onScroll:distanceX=" + distanceX + "  |mFixX=" + mFixX + "  |mMoveOffsetX=" + mMoveOffsetX);
                mScrollHeaderContainer.scrollTo(mMoveOffsetX, 0);
                scrollTo(mMoveOffsetX);
                mFixX = mMoveOffsetX;
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
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
                if (isAnimate && animator != null) animator.cancel();
                mStartX = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                int offsetX = (int) Math.abs(ev.getX() - mStartX);
                if (offsetX > mTouchSlop) {
                    return true;
                } else {
                    return false;
                }
            case MotionEvent.ACTION_UP:
//                actionUP();
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private void actionUP() {
        if (mFixX < 0) {//右滑越界
            mFixX = 0;
            mScrollHeaderContainer.scrollTo(0, 0);
            scrollTo(mFixX);
        } else if (mScrollHeaderContainer.getWidth() + Math.abs(mFixX) > MovableTotalWidth()) {
            //左滑越界
            int pointX = MovableTotalWidth() - mScrollHeaderContainer.getWidth();
            mScrollHeaderContainer.scrollTo(pointX, 0);
            scrollTo(pointX);
        } else {
            if (!isAnimate2Int) return;
            int avgWidth = ((getMeasuredWidth() - mScrollConfig.getFixedColWidth()) / mScrollConfig.getShowCols());
            if (mFixX % avgWidth >= (avgWidth / 2)) {
                mEndX = ((mFixX / avgWidth + 1) * avgWidth);
            } else {
                mEndX = ((mFixX / avgWidth) * avgWidth);
            }
            if (mFixX == mEndX) return;
            animator = ValueAnimator.ofInt(mFixX, mEndX);
            animator.setInterpolator(new DecelerateInterpolator());
//            animator.setDuration(4000);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    Log.e(TAG, "onAnimationUpdate: value=" + value);
                    mScrollHeaderContainer.scrollTo(value, 0);
                    scrollTo(value);
                    mFixX = value;
                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    isAnimate = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isAnimate = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    isAnimate = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.start();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean flag = gestureDetector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            mFixX = mMoveOffsetX;
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

    /**
     * 是否滚动到显示一整列
     *
     * @param animate2Int
     */
    public void setAnimate2Int(boolean animate2Int) {
        isAnimate2Int = animate2Int;
    }

    public void setScrollConfig(ScrollConfig mScrollConfig) {
        this.mScrollConfig = mScrollConfig;
    }
}

