package com.danielrichardson.a2kewl4skewl;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.danielrichardson.a2kewl4skewl.activities_assessment.AssessmentViewerActivity;
import com.danielrichardson.a2kewl4skewl.activities_course.CourseListActivity;
import com.danielrichardson.a2kewl4skewl.activities_course.CourseViewerActivity;
import com.danielrichardson.a2kewl4skewl.database.DBOpenHelper;
import com.danielrichardson.a2kewl4skewl.database.DataProvider;
import com.danielrichardson.a2kewl4skewl.activities_term.TermListActivity;
import com.danielrichardson.a2kewl4skewl.activities_term.TermViewerActivity;

public class MainActivity extends AppCompatActivity {

    private static final int TERM_VIEWER_ACTIVITY_CODE = 111;
    private static final int TERM_LIST_ACTIVITY_CODE = 222;
    private static final int COURSE_LIST_ACTIVITY_CODE = 333;
    private static final int ASSESSMENT_LIST_ACTIVITY_CODE = 444;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void openDegreePlan(View view) {
        Intent intent = new Intent(this, TermListActivity.class);
        startActivityForResult(intent, TERM_LIST_ACTIVITY_CODE);
    }

    public void openCurrentTerm(View view) {
        Cursor cursor = getContentResolver().query(DataProvider.TERMS_URI, null, DBOpenHelper.TERM_STATUS
                + " =1", null, null);
        while (cursor.moveToNext()) {
            Intent intent = new Intent(this, TermViewerActivity.class);
            long id = cursor.getLong(cursor.getColumnIndex(DBOpenHelper.TERMS_TABLE_ID));
            Uri uri = Uri.parse(DataProvider.TERMS_URI + "/" + id);
            intent.putExtra(DataProvider.TERM_CONTENT_TYPE, uri);
            startActivityForResult(intent, TERM_VIEWER_ACTIVITY_CODE);
            return;
        }
        Toast.makeText(this, getString(R.string.no_active_term_set),
                Toast.LENGTH_LONG).show();
    }

    public void openCurrentCourses(View view) {
        Cursor cursor = getContentResolver().query(DataProvider.COURSES_URI, null, DBOpenHelper.COURSE_STATUS
                + " ='IN_PROGRESS'" , null, null);
        while (cursor.moveToNext()) {
            Intent intent = new Intent(this, CourseViewerActivity.class);
            long id = cursor.getLong(cursor.getColumnIndex(DBOpenHelper.COURSES_TABLE_ID));
            Uri uri = Uri.parse(DataProvider.COURSES_URI + "/" + id);
            intent.putExtra(DataProvider.COURSE_CONTENT_TYPE, uri);
            startActivityForResult(intent, COURSE_LIST_ACTIVITY_CODE);
            return;
        }
        Toast.makeText(this, getString(R.string.no_courses_in_progress),
                Toast.LENGTH_LONG).show();
    }

    public void openCurrentAssessments(View view) {
        Cursor cursor = getContentResolver().query(DataProvider.COURSES_URI, null, DBOpenHelper.COURSE_STATUS
                + " ='IN_PROGRESS'" , null, null);
        if (cursor.moveToNext()) {
            Intent intent = new Intent(this, AssessmentViewerActivity.class);
            long id = cursor.getLong(cursor.getColumnIndex(DBOpenHelper.ASSESSMENTS_TABLE_ID));
            Uri uri = Uri.parse(DataProvider.ASSESSMENTS_URI + "/" + id);
            intent.putExtra(DataProvider.ASSESSMENT_CONTENT_TYPE, uri);
            startActivityForResult(intent, ASSESSMENT_LIST_ACTIVITY_CODE);
            return;
        }
        Toast.makeText(this, getString(R.string.no_courses_in_progress),
                Toast.LENGTH_LONG).show();
    }
}
