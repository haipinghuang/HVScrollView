package com.hai.hvscrollview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hai.hvlistview.HVScrollView;
import com.hai.hvlistview.ScrollConfig;
import com.hai.hvscrollview.adapter.MyBaseAdapter;
import com.hai.hvscrollview.bean.LvBean;

import java.util.ArrayList;
import java.util.List;

/**
 * fileDesc
 * Created by huanghp on 2018/12/19.
 * Email h1132760021@sina.com
 */
public class ItemFragment extends Fragment {
    HVScrollView hvScrollView;
    List<LvBean> list = new ArrayList();
    String title;
    ViewPager mViewPage;

    public static ItemFragment newInstance() {
        Bundle args = new Bundle();
        ItemFragment fragment = new ItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments().getString("title");
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        hvScrollView = view.findViewById(R.id.scrollView);
        hvScrollView.setScrollConfig(new ScrollConfig() {
            @Override
            public int getFixedColWidth() {
                float value = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
                return (int) value;
            }

            @Override
            public int getShowCols() {
                return 2;
            }

            @Override
            public int[] getShowViewIds() {
                return new int[]{R.id.tvCol1, R.id.tvCol2, R.id.tvCol3, R.id.tvCol4, R.id.tvCol5, R.id.tvCol6, R.id.tvCol7};
            }

            @Override
            public int getScrollParentViewId() {
                return R.id.llContainer;
            }
        });
        hvScrollView.setHeaderListData(title, new String[]{"列表1", "列表2", "列表3", "列表4", "列表5", "列表6", "列表7"});
        hvScrollView.setAnimate2Int(false);
        hvScrollView.setViewPage(mViewPage);
        hvScrollView.setAdapter(new MyBaseAdapter<LvBean>(getContext(), list) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.item_hscroll_lv, parent, false);
                }
                LvBean item = getItem(position);
                ((TextView) convertView.findViewById(R.id.tvFixCol)).setText(item.fixedCol);
                ((TextView) convertView.findViewById(R.id.tvCol1)).setText(item.movCol1);
                ((TextView) convertView.findViewById(R.id.tvCol2)).setText(item.movCol2);
                ((TextView) convertView.findViewById(R.id.tvCol3)).setText(item.movCol3);
                ((TextView) convertView.findViewById(R.id.tvCol4)).setText(item.movCol4);
                ((TextView) convertView.findViewById(R.id.tvCol5)).setText(item.movCol5);
                ((TextView) convertView.findViewById(R.id.tvCol6)).setText(item.movCol6);
                ((TextView) convertView.findViewById(R.id.tvCol7)).setText(item.movCol7);
                return convertView;
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    void initData() {
        for (int i = 0; i < 1500; i++) {
            list.add(new LvBean(title + i, title + "mov1-" + i, title + "mov2-" + i, title + "mov3-" + i
                    , title + "mov4-" + i, title + "mov5-" + i, title + "mov6-" + i, title + "mov7-" + i));
        }
    }

    public void setViewPage(ViewPager mViewPage) {
        this.mViewPage = mViewPage;
    }
}
