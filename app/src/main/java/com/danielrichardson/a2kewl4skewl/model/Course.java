package com.danielrichardson.a2kewl4skewl.model;

import android.content.ContentValues;
import android.content.Context;

import com.danielrichardson.a2kewl4skewl.database.DBOpenHelper;
import com.danielrichardson.a2kewl4skewl.database.DataProvider;

public class Course {
    public long courseId;
    public long termId;
    public String name;
    public String courseStart;
    public String courseEnd;
    public CourseStatus status;
    public String mentorName;
    public String mentorPhone;
    public String mentorEmail;
    public int notifications;

    public void saveChanges(Context context) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_TERM_ID, termId);
        values.put(DBOpenHelper.COURSE_NAME, name);
        values.put(DBOpenHelper.COURSE_START, courseStart);
        values.put(DBOpenHelper.COURSE_END, courseEnd);
        values.put(DBOpenHelper.COURSE_STATUS, status.toString());
        values.put(DBOpenHelper.COURSE_MENTOR_NAME, mentorName);
        values.put(DBOpenHelper.COURSE_MENTOR_PHONE, mentorPhone);
        values.put(DBOpenHelper.COURSE_MENTOR_EMAIL, mentorEmail);
        values.put(DBOpenHelper.COURSE_NOTIFICATIONS, notifications);
        context.getContentResolver().update(DataProvider.COURSES_URI, values, DBOpenHelper.COURSES_TABLE_ID
                + " = " + courseId, null);
    }
}
