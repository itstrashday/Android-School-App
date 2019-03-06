package com.danielrichardson.a2kewl4skewl.activities_course;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.danielrichardson.a2kewl4skewl.R;
import com.danielrichardson.a2kewl4skewl.activities_assessment.AssessmentListActivity;
import com.danielrichardson.a2kewl4skewl.database.DataManager;
import com.danielrichardson.a2kewl4skewl.database.DataProvider;
import com.danielrichardson.a2kewl4skewl.model.Course;
import com.danielrichardson.a2kewl4skewl.model.CourseStatus;
import com.danielrichardson.a2kewl4skewl.utilities.AlarmHandler;
import com.danielrichardson.a2kewl4skewl.utilities.DateUtil;

public class CourseViewerActivity extends AppCompatActivity {

    private static final int COURSE_NOTE_LIST_ACTIVITY_CODE = 111;
    private static final int ASSESSMENT_LIST_ACTIVITY_CODE = 222;
    private static final int COURSE_EDITOR_ACTIVITY_CODE = 333;

    private Menu menu;
    private Uri courseUri;
    private long courseId;
    private Course course;
    private TextView tvCourseName, tvStartDate, tvEndDate, tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_viewer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        courseUri = intent.getParcelableExtra(DataProvider.COURSE_CONTENT_TYPE);
        courseId = Long.parseLong(courseUri.getLastPathSegment());
        course = DataManager.getCourse(this, courseId);

        setTitle(course.name + " Details");
        setStatus();
        findElements();
    }

    private void setStatus() {
        tvStatus = findViewById(R.id.tvStatus);
        String status = "";
        switch (course.status.toString()) {
            case "PLANNED":
                status = "Planned to Take";
                break;
            case "IN_PROGRESS":
                status = "In Progress";
                break;
            case "COMPLETED":
                status = "Completed";
                break;
            case "DROPPED":
                status = "Dropped";
                break;
        }
        tvStatus.setText(getString(R.string.status) + status);
    }

    private void findElements() {
        tvCourseName = findViewById(R.id.tvCourseName);
        tvCourseName.setText(course.name);
        tvStartDate = findViewById(R.id.tvCourseStart);
        tvStartDate.setText(course.courseStart);
        tvEndDate = findViewById(R.id.tvCourseEnd);
        tvEndDate.setText(course.courseEnd);
    }

    private void updateElements() {
        course = DataManager.getCourse(this, courseId);
        tvCourseName.setText(course.name);
        tvStartDate.setText(course.courseStart);
        tvEndDate.setText(course.courseEnd);
    }

    public void openClassNotesList(View view) {
        Intent intent = new Intent(CourseViewerActivity.this, CourseNoteListActivity.class);
        Uri uri = Uri.parse(DataProvider.COURSES_URI + "/" + courseId);
        intent.putExtra(DataProvider.COURSE_CONTENT_TYPE, uri);
        startActivityForResult(intent, COURSE_NOTE_LIST_ACTIVITY_CODE);
    }

    public void openAssessments(View view) {
        Intent intent = new Intent(CourseViewerActivity.this, AssessmentListActivity.class);
        Uri uri = Uri.parse(DataProvider.COURSES_URI + "/" + courseId);
        intent.putExtra(DataProvider.COURSE_CONTENT_TYPE, uri);
        startActivityForResult(intent, ASSESSMENT_LIST_ACTIVITY_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course_viewer, menu);
        this.menu = menu;
        showAppropriateMenuOptions();
        return true;
    }

    private void showAppropriateMenuOptions() {
        SharedPreferences sp = getSharedPreferences(AlarmHandler.courseAlarmFile, Context.MODE_PRIVATE);
        menu.findItem(R.id.action_enable_notifications).setVisible(true);
        menu.findItem(R.id.action_disable_notifications).setVisible(true);

        if (course.notifications == 1) {
            menu.findItem(R.id.action_enable_notifications).setVisible(false);
        }
        else {
            menu.findItem(R.id.action_disable_notifications).setVisible(false);
        }

        if (course.status == null) {
            course.status = CourseStatus.PLANNED;
            course.saveChanges(this);
        }

        switch (course.status.toString()) {
            case "PLANNED":
                menu.findItem(R.id.action_drop_course).setVisible(false);
                menu.findItem(R.id.action_start_course).setVisible(true);
                menu.findItem(R.id.action_mark_course_completed).setVisible(false);
                break;
            case "IN_PROGRESS":
                menu.findItem(R.id.action_drop_course).setVisible(true);
                menu.findItem(R.id.action_start_course).setVisible(false);
                menu.findItem(R.id.action_mark_course_completed).setVisible(true);
                break;
            case "COMPLETED":
                menu.findItem(R.id.action_drop_course).setVisible(false);
                menu.findItem(R.id.action_start_course).setVisible(false);
                menu.findItem(R.id.action_mark_course_completed).setVisible(false);
                break;
            case "DROPPED":
                menu.findItem(R.id.action_drop_course).setVisible(false);
                menu.findItem(R.id.action_start_course).setVisible(false);
                menu.findItem(R.id.action_mark_course_completed).setVisible(false);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_edit_course:
                return editCourse();
            case R.id.action_delete_course:
                return deleteCourse();
            case R.id.action_enable_notifications:
                return enableNotifications();
            case R.id.action_disable_notifications:
                return disableNotifications();
            case R.id.action_drop_course:
                return dropCourse();
            case R.id.action_start_course:
                return startCourse();
            case R.id.action_mark_course_completed:
                return markCourseCompleted();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean editCourse() {
        Intent intent = new Intent(this, CourseEditorActivity.class);
        Uri uri = Uri.parse(DataProvider.COURSES_URI + "/" + course.courseId);
        intent.putExtra(DataProvider.COURSE_CONTENT_TYPE, uri);
        startActivityForResult(intent, COURSE_EDITOR_ACTIVITY_CODE);
        return true;
    }

    private boolean deleteCourse() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int button) {
                if (button == DialogInterface.BUTTON_POSITIVE) {
                    DataManager.deleteCourse(CourseViewerActivity.this, courseId);
                    setResult(RESULT_OK);
                    finish();
                    Toast.makeText(CourseViewerActivity.this, getString(R.string.delete_successful), Toast.LENGTH_SHORT).show();
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_delete_course)
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
        return true;
    }

    private boolean enableNotifications() {
        long now = DateUtil.todayLong();

        if (now <= DateUtil.getDateTimestamp(course.courseStart)) {
            AlarmHandler.scheduleCourseAlarm(getApplicationContext(), courseId, DateUtil.getDateTimestamp(course.courseStart),
                    "Course starts today!", course.name + " begins on " + course.courseStart);
        }
        if (now <= DateUtil.getDateTimestamp(course.courseStart) - 3 * 24 * 60 * 60 * 1000) {
            AlarmHandler.scheduleCourseAlarm(getApplicationContext(), courseId, DateUtil.getDateTimestamp(course.courseStart),
                    "Course starts in three days!", course.name + " begins on " + course.courseStart);
        }
        if (now <= DateUtil.getDateTimestamp(course.courseStart) - 21 * 24 * 60 * 60 * 1000) {
            AlarmHandler.scheduleCourseAlarm(getApplicationContext(), courseId, DateUtil.getDateTimestamp(course.courseStart),
                    "Course starts in three weeks!", course.name + " begins on " + course.courseStart);
        }

        if (now <= DateUtil.getDateTimestamp(course.courseEnd)) {
            AlarmHandler.scheduleCourseAlarm(getApplicationContext(), courseId, DateUtil.getDateTimestamp(course.courseEnd),
                    "Course ends today!", course.name + " ends on " + course.courseStart);
        }
        if (now <= DateUtil.getDateTimestamp(course.courseEnd) - 3 * 24 * 60 * 60 * 1000) {
            AlarmHandler.scheduleCourseAlarm(getApplicationContext(), courseId, DateUtil.getDateTimestamp(course.courseEnd),
                    "Course ends in three days!", course.name + " ends on " + course.courseStart);
        }
        if (now <= DateUtil.getDateTimestamp(course.courseEnd) - 21 * 24 * 60 * 60 * 1000) {
            AlarmHandler.scheduleCourseAlarm(getApplicationContext(), courseId, DateUtil.getDateTimestamp(course.courseEnd),
                    "Course ends in three weeks!", course.name + " ends on " + course.courseStart);
        }
        course.notifications = 1;
        course.saveChanges(this);
        showAppropriateMenuOptions();
        return true;
    }

    private boolean disableNotifications() {
        course.notifications = 0;
        course.saveChanges(this);
        showAppropriateMenuOptions();
        return true;
    }

    private boolean dropCourse() {
        course.status = CourseStatus.DROPPED;
        course.saveChanges(this);
        setStatus();
        showAppropriateMenuOptions();
        return true;
    }

    private boolean startCourse() {
        course.status = CourseStatus.IN_PROGRESS;
        course.saveChanges(this);
        setStatus();
        showAppropriateMenuOptions();
        return true;
    }

    private boolean markCourseCompleted() {
        course.status = CourseStatus.COMPLETED;
        course.saveChanges(this);
        setStatus();
        showAppropriateMenuOptions();
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            updateElements();
        }
    }
}
