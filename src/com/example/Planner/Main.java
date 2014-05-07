package com.example.Planner;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.SparseArray;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Main extends FragmentActivity {
    final int PAGE_COUNT = 7;

    ViewPager weekTasks;
    MyFragmentPagerAdapter pagerAdapter;
    HashMap<Integer, ArrayList<TaskInfo>> tasks;

    DBHelper dbHelper;

    Dialog prioritySettingDialog;

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

        weekTasks.setCurrentItem(map.get(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)));

        tasks = new HashMap<Integer, ArrayList<TaskInfo>>();

        String dbName = "myDB";
        dbHelper = new DBHelper(this, dbName);
        SQLiteDatabase database = dbHelper.getWritableDatabase();


        Cursor cursor = database.query("mytable", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int taskDay = cursor.getColumnIndex("taskDay");
            int taskDescription = cursor.getColumnIndex("taskDescription");
            int taskCompleteness = cursor.getColumnIndex("taskCompleteness");

            do {
                TaskInfo taskInfo = new TaskInfo();
                taskDay = cursor.getInt(taskDay);
                taskInfo.taskDescription = cursor.getString(taskDescription);
                taskInfo.taskCompleteness = cursor.getInt(taskCompleteness);

                if (!tasks.containsKey(taskDay)) {
                    tasks.put(taskDay, new ArrayList<TaskInfo>());
                }

                tasks.get(taskDay).add(taskInfo);


            } while (cursor.moveToNext());


            database.delete("mytable", null, null);
        }

        prioritySettingDialog = new Dialog(this);
        prioritySettingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        prioritySettingDialog.setContentView(getLayoutInflater().inflate(R.layout.priority_setter_view, null));
        Button okButton = (Button) prioritySettingDialog.findViewById(R.id.button_set);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prioritySettingDialog.dismiss();
            }
        });

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


    HashMap<Integer, ArrayList<TaskInfo>> getTasks() {
        return tasks;
    }


    void initEntry(LinearLayout newEnry) {
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

        CheckBox taskCompleteness = (CheckBox) newEnry.findViewById(R.id.taskCompleteness);
        taskCompleteness.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                taskDescription.setEnabled(!isChecked);
            }
        });

        registerForContextMenu(newEnry);
    }


    private View ctxSelectedView;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.ctx_menu, menu);

        ctxSelectedView = v;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Fragment fragment = pagerAdapter.getRegisteredFragment(weekTasks.getCurrentItem());
        LinearLayout base = (LinearLayout) fragment.getView().findViewById(R.id.base);
        switch (item.getItemId()) {
            case R.id.deleteTask:

                base.removeView(ctxSelectedView);
                return true;
            case R.id.setPriority:
                prioritySettingDialog.show();
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
            case R.id.addTaskMenuItem:
                LinearLayout newTask = (LinearLayout) getLayoutInflater().inflate(R.layout.entry, null);
                initEntry(newTask);
                base.addView(newTask);
                return true;

            case R.id.clearTaskListMenuItem:
                base.removeAllViews();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onDestroy() {

        pagerAdapter.getRegisteredFragment(weekTasks.getCurrentItem()).onDestroyView();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        for (Integer key : tasks.keySet()) {
            ArrayList<TaskInfo> dayTasks = tasks.get(key);
            for (TaskInfo dayTask : dayTasks) {
                contentValues.put("taskDay", key);
                contentValues.put("taskDescription",  dayTask.taskDescription);
                contentValues.put("taskCompleteness", dayTask.taskCompleteness);

                db.insert("mytable", null, contentValues);
                contentValues.clear();
            }
        }

        dbHelper.close();
        super.onDestroy();
    }




}