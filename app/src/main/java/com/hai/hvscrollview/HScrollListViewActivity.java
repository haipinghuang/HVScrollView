package com.hai.hvscrollview;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hai.hvlistview.HVScrollView;
import com.hai.hvlistview.ScrollConfig;
import com.hai.hvscrollview.adapter.MyBaseAdapter;
import com.hai.hvscrollview.bean.LvBean;

import java.util.ArrayList;
import java.util.List;

public class HScrollListViewActivity extends AppCompatActivity {
    HVScrollView listView;
    List<LvBean> list = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hscroll_list_view);

        initData();

        listView = findViewById(R.id.listView);
        listView.setScrollConfig(new ScrollConfig() {
            @Override
            public int getFixedColWidth() {
                float value = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
                return (int) value;
            }

            @Override
            public int getShowCols() {
                return 3;
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
        listView.setHeaderListData("header", new String[]{"列表1", "列表2", "列表3", "列表4", "列表5", "列表6", "列表7"});
        listView.setAnimate2Int(true);
        listView.setAdapter(new MyBaseAdapter<LvBean>(this, list) {
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
    }

    void initData() {
        for (int i = 0; i < 1500; i++) {
            list.add(new LvBean(" header" + i, "mov1-" + i, "mov2-" + i, "mov3-" + i
                    , "mov4-" + i, "mov5-" + i, "mov6-" + i, "mov7-" + i));
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


}
