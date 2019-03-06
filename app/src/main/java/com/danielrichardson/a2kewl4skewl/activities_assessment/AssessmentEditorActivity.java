package com.danielrichardson.a2kewl4skewl.activities_assessment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.danielrichardson.a2kewl4skewl.R;
import com.danielrichardson.a2kewl4skewl.database.DataManager;
import com.danielrichardson.a2kewl4skewl.database.DataProvider;
import com.danielrichardson.a2kewl4skewl.model.Assessment;
import com.danielrichardson.a2kewl4skewl.utilities.DateUtil;

import java.util.Calendar;
import java.util.TimeZone;

public class AssessmentEditorActivity extends AppCompatActivity implements View.OnClickListener {

    private Assessment assessment;
    private long courseId;
    private EditText assessmentCode, assessmentName, assessmentDescription, assessmentDateTime;
    private DatePickerDialog assessmentDateDialog;
    private TimePickerDialog assessmentTimeDialog;
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        assessmentCode = findViewById(R.id.assessmentCode);
        assessmentName = findViewById(R.id.assessmentName);
        assessmentDescription = findViewById(R.id.assessmentDescription);
        assessmentDateTime = findViewById(R.id.assessmentDatetime);

        Uri assessmentUri = getIntent().getParcelableExtra(DataProvider.ASSESSMENT_CONTENT_TYPE);
        if (assessmentUri == null) {
            setTitle(getString(R.string.new_assessment));
            action = Intent.ACTION_INSERT;
            Uri courseUri = getIntent().getParcelableExtra(DataProvider.COURSE_CONTENT_TYPE);
            courseId = Long.parseLong(courseUri.getLastPathSegment());
            assessment = new Assessment();
        }
        else {
            action = Intent.ACTION_EDIT;
            long assessmentId = Long.parseLong(assessmentUri.getLastPathSegment());
            assessment = DataManager.getAssessment(this, assessmentId);
            courseId = assessment.courseId;
            setTitle(getString(R.string.edit) + " " + assessment.name);
            fillAssessmentForm();
        }
        setupDateAndTimePickers();
    }

    private void fillAssessmentForm() {
        if (assessment != null) {
            assessmentCode.setText(assessment.code);
            assessmentName.setText(assessment.name);
            assessmentDescription.setText(assessment.description);
            assessmentDateTime.setText(assessment.datetime);
        }
    }

    private void setupDateAndTimePickers() {
        assessmentDateTime.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        assessmentDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar2 = Calendar.getInstance();
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                assessmentDateTime.setText(DateUtil.dateFormat.format(newDate.getTime()));
                assessmentTimeDialog = new TimePickerDialog(AssessmentEditorActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String AM_PM;
                        if (hourOfDay < 12) {
                            AM_PM = "AM";
                        }
                        else {
                            AM_PM = "PM";
                        }
                        if (hourOfDay > 12) {
                            hourOfDay = hourOfDay - 12;
                        }
                        if (hourOfDay == 0) {
                            hourOfDay = 12;
                        }
                        String minuteString = Integer.toString(minute);
                        if (minute < 10) {
                            minuteString = "0" + minuteString;
                        }
                        String datetime = assessmentDateTime.getText().toString() + " " + hourOfDay + ":" + minuteString
                                + " " + AM_PM + " " + TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT);
                        assessmentDateTime.setText(datetime);
                    }
                }, calendar2.get(Calendar.HOUR_OF_DAY), calendar2.get(Calendar.MINUTE), false);
                assessmentTimeDialog.show();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        assessmentDateTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    assessmentDateDialog.show();
                }
            }
        });
    }


    public void saveAssessmentChanges(View view) {
        getValuesFromFields();
        switch (action) {
            case Intent.ACTION_INSERT:
                DataManager.insertAssessment(this, courseId, assessment.code, assessment.name, assessment.description,
                        assessment.datetime);
                setResult(RESULT_OK);
                finish();
                break;
            case Intent.ACTION_EDIT:
                assessment.saveChanges(this);
                setResult(RESULT_OK);
                finish();
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private void getValuesFromFields() {
        assessment.code = assessmentCode.getText().toString().trim();
        assessment.name = assessmentName.getText().toString().trim();
        assessment.description = assessmentDescription.getText().toString().trim();
        assessment.datetime = assessmentDateTime.getText().toString().trim();
    }

    @Override
    public void onClick(View view) {
        if (view == assessmentDateTime) {
            assessmentDateDialog.show();
        }
    }
}
