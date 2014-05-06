package com.example.untitled2;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 12.03.14
 * Time: 10:05
 * To change this template use File | Settings | File Templates.
 */
public class MessageDialogView2 extends RelativeLayout {

    private Animation flipAnimation;

    private final int PULL_TO_REFRESH = 0;
    private final int RELEASE_TO_REFRESH = 1;
    private final int REFRESHING = 2;

    private static String PULL_TO_REFRESH_STRING = "Pull to refresh.";
    private static String RELEASE_TO_REFRESH_STRING = "Release to refresh.";
    private static String REFRESHING_STRING = "Refreshing.";

    private int state = PULL_TO_REFRESH;


    private Context context;

    private ScrollView scrollView;
    private EditText editText;
    private Button button;

    private LinearLayout childrenHolder;

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


    public MessageDialogView2(Context context) {
        super(context);
        init(context);
    }

    public MessageDialogView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MessageDialogView2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private int mMaxYOverscrollDistance;

    private void init(Context ctx) {
        this.context = ctx;

        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mMaxYOverscrollDistance = 200;

        scrollView = new ScrollView(context) {
            @Override
            protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
                //This is where the magic happens, we have replaced the incoming maxOverScrollY with our own custom variable mMaxYOverscrollDistance;
                boolean ret = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxYOverscrollDistance, isTouchEvent);
                Log.d("ololo", "" + ret);
                if (ret && deltaY < 0 && state == PULL_TO_REFRESH) {

                    state = RELEASE_TO_REFRESH;
                    updateHeader();


//                    ArrayList<LinearLayout> linearLayouts = ((MyActivity) context).restoreLastSeccion();
//
//                    for (final LinearLayout linearLayout : linearLayouts) {
//                        ((LinearLayout) scrollView.findViewById(100500)).addView(linearLayout, 0);
//                    }

                }
                return ret;
            }

            @Override
            public boolean onTouchEvent(MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && state == RELEASE_TO_REFRESH) {


                    AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();

                            state = REFRESHING;
                            updateHeader();
                        }

                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                            super.onPostExecute(result);

                            state = PULL_TO_REFRESH;
                            updateHeader();
                        }
                    };

                    task.execute();


//                    childrenHolder.findViewById(R.id.refreshStatusImage).clearAnimation();
//                    childrenHolder.findViewById(R.id.refreshStatusImage).startAnimation(flipAnimation);


                    Log.d("ololo", "state refreshing...");
                }
                return super.onTouchEvent(event);
            }

        };
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        scrollView.setId(1);


        childrenHolder = new LinearLayout(context);
        childrenHolder.setOrientation(LinearLayout.VERTICAL);
        childrenHolder.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        childrenHolder.addView(((Activity) context).getLayoutInflater().inflate(R.layout.header, null));

        scrollView.addView(childrenHolder);

        editText = new EditText(context);
        editText.setId(2);

        button = new Button(context);
        button.setText("Send");
        button.setOnClickListener(onClickListener);
        button.setId(3);


        LayoutParams relativeParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        relativeParams.addRule(RelativeLayout.ABOVE, 3);
        addView(scrollView, relativeParams);

        relativeParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        addView(button, relativeParams);

        relativeParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        relativeParams.addRule(RelativeLayout.ALIGN_BASELINE, 3);
        relativeParams.addRule(RelativeLayout.LEFT_OF, 3);
        addView(editText, relativeParams);

        flipAnimation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        flipAnimation.setInterpolator(new LinearInterpolator());
        flipAnimation.setDuration(250);
        flipAnimation.setFillAfter(true);


    }

    int i = 0;

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

            String message = editText.getText().toString();
            message = message.trim();

//            if (message.length() == 0) {
//                return;
//            }

            int id;
            if (++i % 2 == 0) {
                id = com.example.untitled2.R.layout.message_history_entry;
            } else {
                id = com.example.untitled2.R.layout.message_history_entry_;
            }

            LinearLayout linearLayout = (LinearLayout) ((Activity) context).getLayoutInflater().inflate(id, null);
            linearLayout.addOnLayoutChangeListener(new OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    scrollView.scrollTo(0, bottom);
                }
            });
            TextView imageView = (TextView) linearLayout.findViewById(com.example.untitled2.R.id.messageContainer);
            imageView.setText(message);

            childrenHolder.addView(linearLayout);
            editText.setText("");

            ToBeSavedMessageFormat entry = new ToBeSavedMessageFormat();
            entry.originator = id;
            entry.messageBody = message;
            entry.firstMessageInSeccion = (i == 1 ? 1 : 0);

            mData.add(entry);
        }
    };

    private void updateHeader() {
        int arrowVisibility = View.VISIBLE;
        int progressBarVisibility = View.VISIBLE;
        String text = "";
        switch (state) {
            case PULL_TO_REFRESH:
                text = PULL_TO_REFRESH_STRING;
                progressBarVisibility = View.GONE;
                arrowVisibility = View.VISIBLE;
                break;

            case REFRESHING:
                text = REFRESHING_STRING;
                progressBarVisibility = View.VISIBLE;
                arrowVisibility = View.GONE;
                break;

            case RELEASE_TO_REFRESH:
                text = RELEASE_TO_REFRESH_STRING;
                childrenHolder.findViewById(R.id.refreshStatusImage).clearAnimation();
                childrenHolder.findViewById(R.id.refreshStatusImage).startAnimation(flipAnimation);
                break;
        }

        LinearLayout header = (LinearLayout) childrenHolder.findViewById(R.id.header);

        header.findViewById(R.id.pull_to_refresh_progress).setVisibility(progressBarVisibility);
        header.findViewById(R.id.refreshStatusImage).setVisibility(arrowVisibility);

        TextView textView = (TextView) header.findViewById(R.id.refreshStatusString);
        textView.setText(text);
    }

    public ArrayList<ToBeSavedMessageFormat> mData = new ArrayList<ToBeSavedMessageFormat>(32);
}
