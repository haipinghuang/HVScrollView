package com.hai.hvscrollview;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    String[] title = new String[]{"西瓜", "苹果", "水梨", "脐橙"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.viewPage);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                ItemFragment itemFragment = ItemFragment.newInstance();
                itemFragment.setViewPage(viewPager);
                itemFragment.getArguments().putString("title", title[position]);
                return itemFragment;
            }

            @Override
            public int getCount() {
                return title.length;
            }
        });
    }
}
