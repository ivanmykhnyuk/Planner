package com.example.Planner;

import java.util.ArrayList;

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

        LinearLayout lvMain = (LinearLayout) view.findViewById(R.id.base);


        View v = inflater.inflate(R.layout.entry, null);
        lvMain.addView(v);


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


        return view;
    }
}