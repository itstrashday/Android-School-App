package com.danielrichardson.a2kewl4skewl.activities_assessment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.danielrichardson.a2kewl4skewl.R;
import com.danielrichardson.a2kewl4skewl.activities_course.CourseEditorActivity;
import com.danielrichardson.a2kewl4skewl.database.DataManager;
import com.danielrichardson.a2kewl4skewl.database.DataProvider;
import com.danielrichardson.a2kewl4skewl.model.Assessment;
import com.danielrichardson.a2kewl4skewl.utilities.AlarmHandler;
import com.danielrichardson.a2kewl4skewl.utilities.DateUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AssessmentViewerActivity extends AppCompatActivity {

    private static final int ASSESSMENT_EDITOR_ACTIVITY_CODE = 111;
    private static final int ASSESSMENT_NOTE_LIST_ACTIVITY_CODE = 222;

    private long assessmentId;
    private Assessment assessment;
    private TextView tvAssessmentTitle, tvAssessmentDescription, tvAssessmentDatetime;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_viewer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadAssessment();
    }

    private void loadAssessment() {
        Uri assessmentUri = getIntent().getParcelableExtra(DataProvider.ASSESSMENT_CONTENT_TYPE);
        assessmentId = Long.parseLong(assessmentUri.getLastPathSegment());
        assessment = DataManager.getAssessment(this, assessmentId);
        tvAssessmentTitle = findViewById(R.id.tvAssessmentTitle);
        tvAssessmentDescription = findViewById(R.id.tvAssessmentDescription);
        tvAssessmentDatetime = findViewById(R.id.tvAssessmentDatetime);
        tvAssessmentTitle.setText(assessment.code + ": " + assessment.name);
        tvAssessmentDescription.setText(assessment.description);
        tvAssessmentDatetime.setText(assessment.datetime);
        setTitle(assessment.name + " Details");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadAssessment();
        }
    }

    public void openAssessmentNotesList(View view) {
        Intent intent = new Intent(AssessmentViewerActivity.this, AssessmentNoteListActivity.class);
        Uri uri = Uri.parse(DataProvider.ASSESSMENTS_URI + "/" + assessmentId);
        intent.putExtra(DataProvider.ASSESSMENT_CONTENT_TYPE, uri);
        startActivityForResult(intent, ASSESSMENT_NOTE_LIST_ACTIVITY_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_assessment_viewer, menu);
        this.menu = menu;
        showAppropriateMenuOptions();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_delete_assessment:
                return deleteAssessment();
            case R.id.action_enable_notifications:
                return enableNotifications();
            case R.id.action_disable_notifications:
                return disableNotifications();
            case R.id.action_edit_assessment:
                return editCourse();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean editCourse() {
        Intent intent = new Intent(this, AssessmentEditorActivity.class);
        Uri uri = Uri.parse(DataProvider.ASSESSMENTS_URI + "/" + assessment.courseId);
        intent.putExtra(DataProvider.ASSESSMENT_CONTENT_TYPE, uri);
        startActivityForResult(intent, ASSESSMENT_EDITOR_ACTIVITY_CODE);
        return true;
    }

    private boolean deleteAssessment() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int button) {
                if (button == DialogInterface.BUTTON_POSITIVE) {
                    DataManager.deleteAssessment(AssessmentViewerActivity.this, assessmentId);
                    setResult(RESULT_OK);
                    finish();
                    Toast.makeText(AssessmentViewerActivity.this, getString(R.string.delete_successful), Toast.LENGTH_LONG).show();
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_delete_assessment)
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
        return true;
    }

    private boolean enableNotifications() {
        long now = DateUtil.todayLong();

        AlarmHandler.scheduleAssessmentAlarm(getApplicationContext(), (int) assessmentId, System.currentTimeMillis()
                + 1000, "Assessment is today!", assessment.name + " takes place on " + assessment.datetime);
        if (now <= DateUtil.getDateTimestamp(assessment.datetime)) {
            AlarmHandler.scheduleAssessmentAlarm(getApplicationContext(), (int) assessmentId, DateUtil.getDateTimestamp(assessment.datetime), "Assessment is today!", assessment.name + " takes place on " + assessment.datetime);
        }
        if (now <= DateUtil.getDateTimestamp(assessment.datetime) - 3 * 24 * 60 * 60 * 1000) {
            AlarmHandler.scheduleAssessmentAlarm(getApplicationContext(), (int) assessmentId, DateUtil.getDateTimestamp(assessment.datetime) - 3 * 24 * 60 * 60 * 1000, "Assessment is in three days!", assessment.name + " takes place on " + assessment.datetime);
        }
        if (now <= DateUtil.getDateTimestamp(assessment.datetime) - 21 * 24 * 60 * 60 * 1000) {
            AlarmHandler.scheduleAssessmentAlarm(getApplicationContext(), (int) assessmentId, DateUtil.getDateTimestamp(assessment.datetime) - 21 * 24 * 60 * 60 * 1000, "Assessment is in three weeks!", assessment.name + " takes place on " + assessment.datetime);
        }

        assessment.notifications = 1;
        assessment.saveChanges(this);
        showAppropriateMenuOptions();
        return true;
    }

    private boolean disableNotifications() {
        assessment.notifications = 0;
        assessment.saveChanges(this);
        showAppropriateMenuOptions();
        return true;
    }

    private void showAppropriateMenuOptions() {
        menu.findItem(R.id.action_enable_notifications).setVisible(true);
        menu.findItem(R.id.action_disable_notifications).setVisible(true);

        if (assessment.notifications == 1) {
            menu.findItem(R.id.action_enable_notifications).setVisible(false);
        }
        else {
            menu.findItem(R.id.action_disable_notifications).setVisible(false);
        }
    }
}
