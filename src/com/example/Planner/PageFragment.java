package com.example.Planner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, null);

        LinearLayout base = (LinearLayout) view.findViewById(R.id.base);


        Main activity = (Main) getActivity();
        ArrayList<Object[]> dkkdfkdf = activity.getTasks().get(activity.getPager().getCurrentItem());
        if (dkkdfkdf != null) {
            for (Object[] taskInfo : dkkdfkdf) {
                LinearLayout newEnry = (LinearLayout) inflater.inflate(R.layout.entry, null);
                activity.initEntry(newEnry, base);


                Spinner prioritySetter = (Spinner) newEnry.findViewById(R.id.prioritySetter);
                EditText taskDescription = (EditText) newEnry.findViewById(R.id.taskDesctiption);
                CheckBox taskCompleteness = (CheckBox) newEnry.findViewById(R.id.taskCompleteness);

                prioritySetter.setSelection((Integer) taskInfo[0]);
                taskDescription.setText((String) taskInfo[1]);
                taskCompleteness.setChecked((Boolean) taskInfo[2]);

                base.addView(newEnry);
            }
    }

    return view;
}

    public void onDestroyView() {
        super.onDestroyView();

        LinearLayout base = (LinearLayout) getView().findViewById(R.id.base);
        for (int i = 0; i < base.getChildCount(); ++i) {
            LinearLayout child = (LinearLayout) base.getChildAt(i);
            Spinner prioritySetter = (Spinner) child.findViewById(R.id.prioritySetter);
            EditText taskDescription = (EditText) child.findViewById(R.id.taskDesctiption);
            CheckBox taskCompleteness = (CheckBox) child.findViewById(R.id.taskCompleteness);

            Object[] taskInfo = new Object[3];
            taskInfo[0] = prioritySetter.getSelectedItemPosition();
            taskInfo[1] = taskDescription.getText().toString();
            taskInfo[2] = taskCompleteness.isChecked();

            int id = ((Main) getActivity()).getPager().getCurrentItem();

            HashMap<Integer, ArrayList<Object[]>> tasks = ((Main) getActivity()).getTasks();
            if (tasks.containsKey(id)) {
                tasks.get(id).add(taskInfo);
            } else {
                ArrayList<Object[]> list = new ArrayList<Object[]>();
                list.add(taskInfo);

                tasks.put(id, list);
            }
        }

    }
}