package com.example.Planner;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.SparseArray;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Main extends FragmentActivity {
    static final int PAGE_COUNT = 7;

    ViewPager weekTasks;
    MyFragmentPagerAdapter pagerAdapter;
    HashMap<Integer, ArrayList<Object[]>> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        weekTasks = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), this);
        weekTasks.setAdapter(pagerAdapter);

        weekTasks.setOnPageChangeListener(new OnPageChangeListener() {

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
        weekTasks.setCurrentItem(map.get(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)));

        tasks = new HashMap<Integer, ArrayList<Object[]>>();
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
            PageFragment pageFragment = new PageFragment();
            pageFragment.setPos(position);
            return pageFragment;
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



    HashMap<Integer, ArrayList<Object[]>> getTasks() {
        return tasks;
    }


    private void pullMorePriorityTasksToTop(LinearLayout base) {
        boolean flag = true;   // set flag to true to begin first pass


        while (flag) {
            flag = false;    //set flag to false awaiting a possible swap

            for (int i = 0; i < base.getChildCount() - 1; ++i) {
                LinearLayout entry = (LinearLayout) base.getChildAt(i).findViewById(R.id.entry);
                int pr1 = ((Spinner) entry.findViewById(R.id.prioritySetter)).getSelectedItemPosition() + 1;

                LinearLayout entryNext = (LinearLayout) base.getChildAt(i + 1).findViewById(R.id.entry);
                int pr2 = ((Spinner) entryNext.findViewById(R.id.prioritySetter)).getSelectedItemPosition() + 1;
                if (pr1 < pr2) {
                    base.removeView(entry);
                    base.addView(entry, i - 1);

                    flag = true;
                }
            }
        }
    }

    void initEntry(LinearLayout newEnry, final LinearLayout base) {
        final EditText taskDescription = (EditText) newEnry.findViewById(R.id.taskDesctiption);
        taskDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    // hide virtual keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(taskDescription.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });


        final Spinner taskPrioritySetter = (Spinner) newEnry.findViewById(R.id.prioritySetter);
        taskPrioritySetter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pullMorePriorityTasksToTop(base);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        CheckBox taskCompleteness = (CheckBox) newEnry.findViewById(R.id.taskCompleteness);
        taskCompleteness.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                taskDescription.setEnabled(!isChecked);
                taskPrioritySetter.setEnabled(!isChecked);
            }
        });

        registerForContextMenu(newEnry);
    }


    private View ctxSelectedView;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Share Menu.");
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.ctx_menu, menu);

        ctxSelectedView = v;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteTask:
                Fragment fragment = pagerAdapter.getRegisteredFragment(weekTasks.getCurrentItem());
                LinearLayout base = (LinearLayout) fragment.getView().findViewById(R.id.base);
                base.removeView(ctxSelectedView);
                return true;
            default:
                return super.onContextItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment = pagerAdapter.getRegisteredFragment(weekTasks.getCurrentItem());
        final LinearLayout base = (LinearLayout) fragment.getView().findViewById(R.id.base);
        switch (item.getItemId()) {
            case R.id.addTask:
                LinearLayout newTask = (LinearLayout) getLayoutInflater().inflate(R.layout.entry, null);
                initEntry(newTask, base);
                base.addView(newTask);
                return true;

            case R.id.clearTaskList:
                base.removeAllViews();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}