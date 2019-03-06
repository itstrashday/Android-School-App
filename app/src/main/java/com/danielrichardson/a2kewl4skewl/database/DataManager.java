package com.danielrichardson.a2kewl4skewl.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.danielrichardson.a2kewl4skewl.model.Assessment;
import com.danielrichardson.a2kewl4skewl.model.AssessmentNote;
import com.danielrichardson.a2kewl4skewl.model.Course;
import com.danielrichardson.a2kewl4skewl.model.CourseNote;
import com.danielrichardson.a2kewl4skewl.model.CourseStatus;
import com.danielrichardson.a2kewl4skewl.model.Term;

public class DataManager {

    // Terms
    public static Uri insertTerm(Context context, String termName, String termStart, String termEnd, int termStatus) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TERM_NAME, termName);
        values.put(DBOpenHelper.TERM_START, termStart);
        values.put(DBOpenHelper.TERM_END, termEnd);
        values.put(DBOpenHelper.TERM_STATUS, termStatus);
        Uri termUri = context.getContentResolver().insert(DataProvider.TERMS_URI, values);
        return termUri;
    }

    public static Term getTerm(Context context, long termId) {
        Cursor cursor = context.getContentResolver().query(DataProvider.TERMS_URI, DBOpenHelper.TERMS_COLUMNS,
                DBOpenHelper.TERMS_TABLE_ID + " = " + termId, null, null);
        cursor.moveToFirst();
        String termName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_NAME));
        String termStartDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_START));
        String termEndDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_END));
        int termStatus = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.TERM_STATUS));

        Term term = new Term();
        term.termId = termId;
        term.name = termName;
        term.termStart = termStartDate;
        term.termEnd = termEndDate;
        term.status = termStatus;
        return term;
    }

    // Courses
    public static Uri insertCourse(Context context, long termId, String courseName, String courseStart, String courseEnd,
                                   String courseMentorName, String courseMentorPhone, String courseMentorEmail, CourseStatus status) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_TERM_ID, termId);
        values.put(DBOpenHelper.COURSE_NAME, courseName);
        values.put(DBOpenHelper.COURSE_START, courseStart);
        values.put(DBOpenHelper.COURSE_END, courseEnd);
        values.put(DBOpenHelper.COURSE_MENTOR_NAME, courseMentorName);
        values.put(DBOpenHelper.COURSE_MENTOR_PHONE, courseMentorPhone);
        values.put(DBOpenHelper.COURSE_MENTOR_EMAIL, courseMentorEmail);
        values.put(DBOpenHelper.COURSE_STATUS, status.toString());
        Uri courseUri = context.getContentResolver().insert(DataProvider.COURSES_URI, values);
        return courseUri;
    }

    public static Course getCourse(Context context, long courseId) {
        Cursor cursor = context.getContentResolver().query(DataProvider.COURSES_URI, DBOpenHelper.COURSES_COLUMNS,
                DBOpenHelper.COURSES_TABLE_ID + " = " + courseId, null, null);
        cursor.moveToFirst();
        Long termId = cursor.getLong(cursor.getColumnIndex(DBOpenHelper.COURSE_TERM_ID));
        String courseName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_NAME));
        String courseStart = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_START));
        String courseEnd = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_END));
        CourseStatus courseStatus = CourseStatus.valueOf(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_STATUS)));
        String courseMentorName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_MENTOR_NAME));
        String courseMentorPhone = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_MENTOR_PHONE));
        String courseMentorEmail = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_MENTOR_EMAIL));
        int courseNotifications = (cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COURSE_NOTIFICATIONS)));

        Course course = new Course();
        course.courseId = courseId;
        course.termId = termId;
        course.name = courseName;
        course.courseStart = courseStart;
        course.courseEnd = courseEnd;
        course.status = courseStatus;
        course.mentorName = courseMentorName;
        course.mentorPhone = courseMentorPhone;
        course.mentorEmail = courseMentorEmail;
        course.notifications = courseNotifications;
        return course;
    }

    public static boolean deleteCourse(Context context, long courseId) {
        Cursor notesCursor = context.getContentResolver().query(DataProvider.COURSE_NOTES_URI,
                DBOpenHelper.COURSE_NOTES_COLUMNS, DBOpenHelper.COURSE_NOTE_COURSE_ID + " = " + courseId,
                null, null);
        while (notesCursor.moveToNext()) {
            deleteCourseNote(context, notesCursor.getLong(notesCursor.getColumnIndex(DBOpenHelper.COURSE_NOTES_TABLE_ID)));
        }
        context.getContentResolver().delete(DataProvider.COURSES_URI, DBOpenHelper.COURSES_TABLE_ID + " = "
                + courseId, null);
        return true;
    }

    // Course Notes
    public static Uri insertCourseNote(Context context, long courseId, String text) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_NOTE_COURSE_ID, courseId);
        values.put(DBOpenHelper.COURSE_NOTE_TEXT, text);
        Uri courseNoteUri = context.getContentResolver().insert(DataProvider.COURSE_NOTES_URI, values);
        return courseNoteUri;
    }

    public static CourseNote getCourseNote(Context context, long courseNoteId) {
        Cursor cursor = context.getContentResolver().query(DataProvider.COURSE_NOTES_URI, DBOpenHelper.COURSE_NOTES_COLUMNS,
                DBOpenHelper.COURSE_NOTES_TABLE_ID + " = " + courseNoteId, null, null);
        cursor.moveToFirst();
        long courseId = cursor.getLong(cursor.getColumnIndex(DBOpenHelper.COURSE_NOTE_COURSE_ID));
        String text = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_NOTE_TEXT));

        CourseNote courseNote = new CourseNote();
        courseNote.courseNoteId = courseNoteId;
        courseNote.courseId = courseId;
        courseNote.text = text;
        return courseNote;
    }

    public static boolean deleteCourseNote(Context context, long courseNoteId) {
        context.getContentResolver().delete(DataProvider.COURSE_NOTES_URI, DBOpenHelper.COURSE_NOTES_TABLE_ID + " = " + courseNoteId, null);
        return true;
    }

    // Assessments
    public static Uri insertAssessment(Context context, long courseId, String code, String name, String description, String datetime) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.ASSESSMENT_COURSE_ID, courseId);
        values.put(DBOpenHelper.ASSESSMENT_CODE, code);
        values.put(DBOpenHelper.ASSESSMENT_NAME, name);
        values.put(DBOpenHelper.ASSESSMENT_DESCRIPTION, description);
        values.put(DBOpenHelper.ASSESSMENT_DATETIME, datetime);
        Uri assessmentUri = context.getContentResolver().insert(DataProvider.ASSESSMENTS_URI, values);
        return assessmentUri;
    }

    public static Assessment getAssessment(Context context, long assessmentId) {
        Cursor cursor = context.getContentResolver().query(DataProvider.ASSESSMENTS_URI, DBOpenHelper.ASSESSMENTS_COLUMNS,
                DBOpenHelper.ASSESSMENTS_TABLE_ID + " = " + assessmentId, null, null);
        cursor.moveToFirst();
        long courseId = cursor.getLong(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_COURSE_ID));
        String name = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_NAME));
        String description = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_DESCRIPTION));
        String code = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_CODE));
        String datetime = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_DATETIME));
        int notifications = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_NOTIFICATIONS));

        Assessment assessment = new Assessment();
        assessment.assessmentId = assessmentId;
        assessment.courseId = courseId;
        assessment.name = name;
        assessment.description = description;
        assessment.code = code;
        assessment.datetime = datetime;
        assessment.notifications = notifications;
        return assessment;
    }

    public static boolean deleteAssessment(Context context, long assessmentId) {
        Cursor notesCursor = context.getContentResolver().query(DataProvider.ASSESSMENT_NOTES_URI,
                DBOpenHelper.ASSESSMENT_NOTES_COLUMNS, DBOpenHelper.ASSESSMENT_NOTE_ASSESSMENT_ID + " = " +
                        assessmentId, null, null);
        while (notesCursor.moveToNext()) {
            deleteAssessmentNote(context, notesCursor.getLong(notesCursor.getColumnIndex(DBOpenHelper.ASSESSMENT_NOTES_TABLE_ID)));
        }
        context.getContentResolver().delete(DataProvider.ASSESSMENTS_URI, DBOpenHelper.ASSESSMENTS_TABLE_ID
                + " = " + assessmentId, null);
        return true;
    }

    // Assessment Notes
    public static Uri insertAssessmentNote(Context context, long assessmentId, String text) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.ASSESSMENT_NOTE_ASSESSMENT_ID, assessmentId);
        values.put(DBOpenHelper.ASSESSMENT_NOTE_TEXT, text);
        Uri assessmentNoteUri = context.getContentResolver().insert(DataProvider.ASSESSMENT_NOTES_URI, values);
        return assessmentNoteUri;
    }

    public static AssessmentNote getAssessmentNote(Context context, long assessmentNoteId) {
        Cursor cursor = context.getContentResolver().query(DataProvider.ASSESSMENT_NOTES_URI,
                DBOpenHelper.ASSESSMENT_NOTES_COLUMNS, DBOpenHelper.ASSESSMENT_NOTES_TABLE_ID + " = "
                        + assessmentNoteId, null, null);
        cursor.moveToFirst();
        long assessmentId = cursor.getLong(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_NOTE_ASSESSMENT_ID));
        String text = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_NOTE_TEXT));

        AssessmentNote assessmentNote = new AssessmentNote();
        assessmentNote.assessmentNoteId = assessmentNoteId;
        assessmentNote.assessmentId = assessmentId;
        assessmentNote.text = text;
        return assessmentNote;
    }

    public static boolean deleteAssessmentNote(Context context, long assessmentNoteId) {
        context.getContentResolver().delete(DataProvider.ASSESSMENT_NOTES_URI, DBOpenHelper.ASSESSMENT_NOTES_TABLE_ID
                + " = " + assessmentNoteId, null);
        return true;
    }
}
