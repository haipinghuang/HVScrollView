package com.hai.hvlistview;

import android.support.annotation.IdRes;

public interface ScrollConfig {
    /**
     * 固定header列宽度
     */
    int getFixedColWidth();

    /**
     * 默认显示多少列
     */
    int getShowCols();

    /**
     * listView adapter布局文件中可滚动view的每列的view ID;
     * {@link HVScrollView} 根据view ID找到view并调整每列的宽度
     */
    @IdRes
    int[] getShowViewIds();

    /**
     * listView adapter布局文件中所有可滚动view的parentView;
     */
    @IdRes
    int getScrollParentViewId();
}