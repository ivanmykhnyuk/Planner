package com.example.Planner;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

public class PageFragment extends Fragment {
    ArrayAdapter<String> adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.entry, R.id.taskDesctiption, new ArrayList<String>());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, null);

       final LinearLayout base = (LinearLayout) view.findViewById(R.id.base);


        View v = inflater.inflate(R.layout.entry, null);
        base.addView(v);


        final EditText taskDesctiption = (EditText) v.findViewById(R.id.taskDesctiption);
        taskDesctiption.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    // hide virtual keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(taskDesctiption.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        CheckBox taskCompleteness = (CheckBox) v.findViewById(R.id.taskCompleteness);
        taskCompleteness.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                taskDesctiption.setEnabled(!isChecked);
            }
        });

        final Spinner taskPrioritySetter = (Spinner) v.findViewById(R.id.taskPriority);
        List<String> list = new ArrayList<String>();
        for (int i = 1; i <= 10; ++i) {
            list.add("" + i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, list);
        taskPrioritySetter.setAdapter(adapter);


        taskPrioritySetter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pullMorePriorityTasksToTop(base);
            }
        });


        return view;
    }

    private void pullMorePriorityTasksToTop(LinearLayout base) {
        boolean flag = true;   // set flag to true to begin first pass


        while ( flag )
        {
            flag= false;    //set flag to false awaiting a possible swap

            for (int i = 0; i < base.getChildCount() - 1; ++i) {
                LinearLayout entry = (LinearLayout) base.getChildAt(i).findViewById(R.id.entry);
                int pr1 = ((Spinner) entry.findViewById(R.id.taskPriority)).getSelectedItemPosition() + 1;

                LinearLayout entryNext = (LinearLayout) base.getChildAt(i).findViewById(R.id.entry);
                int pr2 = ((Spinner) entry.findViewById(R.id.taskPriority)).getSelectedItemPosition() + 1;
                if (pr1 < pr2) {
                    base.removeViewAt(i);
                    base.removeViewAt(i + 1);

                    base.addView(entryNext, i);
                    base.addView(entry, i+ 1);
                    flag = true;
                }
            }
        }
    }
}