# HVScrollView
仿同花顺自选股、持仓股多列可横向滚动的ListView,可支持松开手指后自动滑动到临界点。
# 效果图
![图片名称](https://github.com/haipinghuang/HVScrollView/blob/master/ext/1.gif)
![图片名称](https://github.com/haipinghuang/HVScrollView/blob/master/ext/2.gif)
# 功能
* 支持设定每列的列头内容
* 支持设定第一列（即固定列）的宽度
* 支持设定默认显示可以滚动的列数
* 支持设定是否松开手指后自动滑动到临界点
* 支持设定自定义ListView adapter
* 支持放在ViewPage内使用，效果不是很理想
# 使用
下载源码，导入hvlistview库  
```java
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
```
# TODO
* 支持瞬滑
* 完美支持和Viewpage结合事件不冲突
