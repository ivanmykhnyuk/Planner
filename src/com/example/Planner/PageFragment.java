package com.example.Planner;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PageFragment extends Fragment {

    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

    int pageNumber;
    int backColor;

    static PageFragment newInstance(int page) {
        PageFragment pageFragment = new PageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);

        Random rnd = new Random();
        backColor = Color.argb(40, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, null);

        ListView lvMain = (ListView) view.findViewById(R.id.lvMain);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),
                R.layout.message_history_entry, R.id.task, new ArrayList<String>());


        // присваиваем адаптер списку
        lvMain.setAdapter(adapter);

        adapter.add("dkdfkdfkdf");
        adapter.add("dkdfkdfkdf");
        adapter.add("dkdfkdfkdf");
        adapter.add("dkdfkdfkdf");
        adapter.add("dkdfkdfkdf");
        adapter.add("dkdfkdfkdf");

        return view;
    }
}