package com.example.untitled2;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MyActivity extends Activity {

    SQLiteOpenHelper openHelper;
    MessageDialogView2 contentView;

    /**
     * Called when the activity is first created.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView = new MessageDialogView2(this);
        setContentView(contentView);

        String dbName = "myDB";
        openHelper = new SQLiteOpenHelper(this, dbName, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("create table messageHistory (messageOriginator integer, messageBody text, firstMessageInSeccion bit)");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        for (ToBeSavedMessageFormat entry : contentView.mData) {
            contentValues.put("messageBody", entry.messageBody);
            contentValues.put("messageOriginator", entry.originator);
            contentValues.put("firstMessageInSeccion", entry.firstMessageInSeccion);

            db.insert("messageHistory", null, contentValues);
            contentValues.clear();
        }

        ArrayList<ToBeSavedMessageFormat> list = loadPreviousEntryes(db, 2);

        openHelper.close();

    }

    ArrayList<ToBeSavedMessageFormat> loadPreviousEntryes(SQLiteDatabase db, int seccionNumber) {
        // делаем запрос всех данных из таблицы mytable, получаем Cursor
        Cursor c = db.query("messageHistory", null, null, null, null, null, null);
        ToBeSavedMessageFormat entry = null;
        ArrayList<ToBeSavedMessageFormat> list = new ArrayList<ToBeSavedMessageFormat>();

        // ставим позицию курсора на первую строку выборки
        // если в выборке нет строк, вернется false
        if (c.moveToLast()) {

            // определяем номера столбцов по имени в выборке
            int messageOriginatorColumnIdx = c.getColumnIndex("messageOriginator");
            int messageBodyColumnIndx = c.getColumnIndex("messageBody");
            int firstMessageInSeccionIdx = c.getColumnIndex("firstMessageInSeccion");


            do {
                entry = new ToBeSavedMessageFormat();
                entry.originator = c.getInt(messageOriginatorColumnIdx);
                entry.messageBody = c.getString(messageBodyColumnIndx);
                entry.firstMessageInSeccion = c.getInt(firstMessageInSeccionIdx);
                if (entry.firstMessageInSeccion == 1) {
                    --seccionNumber;
                }


                list.add(entry);

            } while (c.moveToPrevious() && seccionNumber != 0);
        } else
            c.close();

        return list;
    }

    public ArrayList<LinearLayout> restoreLastSeccion() {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        ArrayList<ToBeSavedMessageFormat> list = loadPreviousEntryes(db, 1);
        ArrayList<LinearLayout> restoredViews = new ArrayList<LinearLayout>();

        for (ToBeSavedMessageFormat entry : list) {
            LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(entry.originator, null);
            TextView imageView = (TextView) linearLayout.findViewById(com.example.untitled2.R.id.messageContainer);
            imageView.setText(entry.messageBody);

            restoredViews.add(linearLayout);

        }

        return restoredViews;
    }

}