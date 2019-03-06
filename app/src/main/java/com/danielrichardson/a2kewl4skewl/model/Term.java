package com.danielrichardson.a2kewl4skewl.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.danielrichardson.a2kewl4skewl.database.DBOpenHelper;
import com.danielrichardson.a2kewl4skewl.database.DataProvider;

public class Term {
    public long termId;
    public String name;
    public String termStart;
    public String termEnd;
    public int status;

    public void saveChanges(Context context) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TERM_NAME, name);
        values.put(DBOpenHelper.TERM_START, termStart);
        values.put(DBOpenHelper.TERM_END, termEnd);
        values.put(DBOpenHelper.TERM_STATUS, status);
        context.getContentResolver().update(DataProvider.TERMS_URI, values, DBOpenHelper.TERMS_TABLE_ID
                + " = " + termId, null);
    }

    public long getClassCount(Context context) {
        Cursor cursor = context.getContentResolver().query(DataProvider.COURSES_URI, DBOpenHelper.COURSES_COLUMNS,
                DBOpenHelper.COURSE_TERM_ID + " = " + this.termId, null, null);
        int numRows = cursor.getCount();
        return numRows;
    }

    public void makeInactive(Context context) {
        this.status = 0;
        saveChanges(context);
    }

    public void makeCurrent(Context context) {
        this.status = 1;
        saveChanges(context);
    }
}
