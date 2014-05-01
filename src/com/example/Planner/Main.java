package com.example.Planner;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Calendar;
import java.util.HashMap;

public class Main extends FragmentActivity {
    static final int PAGE_COUNT = 7;

    ViewPager pager;
    MyFragmentPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button addTaskButton = (Button) findViewById(R.id.addTaskButton);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = pagerAdapter.getRegisteredFragment(pager.getCurrentItem());
                LinearLayout base = (LinearLayout) fragment.getView().findViewById(R.id.base);

                LinearLayout newEnry = (LinearLayout) getLayoutInflater().inflate(R.layout.entry, null);

                base.addView(newEnry);
            }
        });




        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), this);
        pager.setAdapter(pagerAdapter);

        pager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>(7);

        map.put(Calendar.MONDAY, 0);
        map.put(Calendar.TUESDAY, 1);
        map.put(Calendar.WEDNESDAY, 2);
        map.put(Calendar.THURSDAY, 3);
        map.put(Calendar.FRIDAY, 4);
        map.put(Calendar.SATURDAY, 5);
        map.put(Calendar.SUNDAY, 6);

        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        pager.setCurrentItem(map.get(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)));
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private Context context;
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        public MyFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            return new PageFragment();
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = "";
            switch (position) {
                case 0:
                    title = context.getString(R.string.monday);
                    break;
                case 1:
                    title = context.getString(R.string.tuesday);
                    break;
                case 2:
                    title = context.getString(R.string.wednesday);
                    break;
                case 3:
                    title = context.getString(R.string.thursday);
                    break;
                case 4:
                    title = context.getString(R.string.friday);
                    break;
                case 5:
                    title = context.getString(R.string.saturday);
                    break;
                case 6:
                    title = context.getString(R.string.sunday);
                    break;
            }
            return title;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PageFragment fragment = (PageFragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

    }

}