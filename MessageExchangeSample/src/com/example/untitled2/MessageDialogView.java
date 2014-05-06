package com.example.untitled2;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 12.03.14
 * Time: 10:05
 * To change this template use File | Settings | File Templates.
 */
public class MessageDialogView extends RelativeLayout {


    private Context context;

    private ListView listView;
    private EditText editText;
    private Button button;

    private InterlocutorInfo currentSenderInfo;
    private InterlocutorInfo currentReceiverInfo;

    public static class InterlocutorInfo {
        public int photo;
        public String name;
    }

    public void setInterlocutorsInfo(InterlocutorInfo sender, InterlocutorInfo receiver) {
        currentReceiverInfo = sender;
        currentReceiverInfo = receiver;
    }


    public MessageDialogView(Context context) {
        super(context);
        init(context);
    }

    public MessageDialogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MessageDialogView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void showInputMessage(String message) {

    }

    private int mMaxYOverscrollDistance;

    private void init(Context ctx) {
        this.context = ctx;


        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        final float density = metrics.density;

        mMaxYOverscrollDistance = (int) (density * 100);

        listView = new ListView(context) {
            @Override
            protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
                //This is where the magic happens, we have replaced the incoming maxOverScrollY with our own custom variable mMaxYOverscrollDistance;
                return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxYOverscrollDistance, isTouchEvent);
            }
        };


        listView.setDivider(null);
        listView.setSelector(R.color.transparent);
        listView.setId(1);

        editText = new EditText(context);
        editText.setId(2);

        button = new Button(context);
        button.setText("Send");
        button.setOnClickListener(onClickListener);
        button.setId(3);


        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        relativeParams.addRule(RelativeLayout.ABOVE, 3);
        addView(listView, relativeParams);

        relativeParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        addView(button, relativeParams);

        relativeParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        relativeParams.addRule(RelativeLayout.ALIGN_BASELINE, 3);
        relativeParams.addRule(RelativeLayout.LEFT_OF, 3);
        addView(editText, relativeParams);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                com.example.untitled2.R.layout.message_history_entry, com.example.untitled2.R.id.messageContainer);

        listView.setAdapter(adapter);


    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

            ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
            String message = editText.getText().toString();
            message = message.trim();

            if (message.length() > 0) {
                adapter.add(message);
                listView.setSelection(listView.getAdapter().getCount() - 1);
            }

            editText.setText("");
        }
    };
}
