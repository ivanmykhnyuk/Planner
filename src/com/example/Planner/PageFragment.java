package com.example.Planner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PageFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    int mPos;

    void setPos(int pos) {
        mPos = pos;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, null);

        LinearLayout base = (LinearLayout) view.findViewById(R.id.base);


        Main activity = (Main) getActivity();
        ArrayList<TaskInfo> dkkdfkdf = activity.getTasks().get(mPos);
        if (dkkdfkdf != null) {
            for (TaskInfo taskInfo : dkkdfkdf) {
                LinearLayout newEnry = (LinearLayout) inflater.inflate(R.layout.entry, null);
                activity.initEntry(newEnry);

                EditText taskDescription = (EditText) newEnry.findViewById(R.id.taskDesctiption);
                CheckBox taskCompleteness = (CheckBox) newEnry.findViewById(R.id.taskCompleteness);

                taskDescription.setText(taskInfo.taskDescription);
                taskCompleteness.setChecked(taskInfo.taskCompleteness == 1 ? true : false);

                base.addView(newEnry);
            }
        }

        return view;
    }

    public void onDestroyView() {
        super.onDestroyView();

        LinearLayout base = (LinearLayout) getView().findViewById(R.id.base);

        ((Main) getActivity()).getTasks().remove(mPos);

        for (int i = 0; i < base.getChildCount(); ++i) {
            LinearLayout child = (LinearLayout) base.getChildAt(i);
            EditText taskDescription = (EditText) child.findViewById(R.id.taskDesctiption);
            CheckBox taskCompleteness = (CheckBox) child.findViewById(R.id.taskCompleteness);

            TaskInfo taskInfo = new TaskInfo();
            taskInfo.taskDescription = taskDescription.getText().toString();
            taskInfo.taskCompleteness = taskCompleteness.isChecked() ? 1 : 0;


            HashMap<Integer, ArrayList<TaskInfo>> tasks = ((Main) getActivity()).getTasks();
            if (tasks.containsKey(mPos)) {
                tasks.get(mPos).add(taskInfo);
            } else {
                ArrayList<TaskInfo> list = new ArrayList<TaskInfo>();
                list.add(taskInfo);

                tasks.put(mPos, list);
            }
        }

    }
}