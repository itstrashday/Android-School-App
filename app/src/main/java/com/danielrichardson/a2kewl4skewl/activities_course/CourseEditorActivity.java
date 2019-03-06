package com.danielrichardson.a2kewl4skewl.activities_course;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.danielrichardson.a2kewl4skewl.R;
import com.danielrichardson.a2kewl4skewl.database.DataManager;
import com.danielrichardson.a2kewl4skewl.database.DataProvider;
import com.danielrichardson.a2kewl4skewl.model.Course;
import com.danielrichardson.a2kewl4skewl.model.CourseStatus;
import com.danielrichardson.a2kewl4skewl.utilities.DateUtil;

import java.util.Calendar;

public class CourseEditorActivity extends AppCompatActivity implements View.OnClickListener {

    private String action;
    private Uri courseUri, termUri;
    private Course course;
    private EditText courseName, courseStart, courseEnd, courseMentorName, courseMentorPhone, courseMentorEmail;
    private DatePickerDialog courseStartDateDialog, courseEndDateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViews();
        Intent intent = getIntent();
        courseUri = intent.getParcelableExtra(DataProvider.COURSE_CONTENT_TYPE);
        termUri = intent.getParcelableExtra(DataProvider.TERM_CONTENT_TYPE);

        if (courseUri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.add_new_course));
        }
        else {
            action = Intent.ACTION_EDIT;
            long classId = Long.parseLong(courseUri.getLastPathSegment());
            course = DataManager.getCourse(this, classId);
            setTitle(getString(R.string.edit) + " " + course.name);
            populateFields(course);
        }
        setupDatePickers();
    }

    private void findViews() {
        courseName = findViewById(R.id.courseName);
        courseStart = findViewById(R.id.courseStart);
        courseEnd = findViewById(R.id.courseEnd);
        courseMentorName = findViewById(R.id.courseMentor);
        courseMentorPhone = findViewById(R.id.courseMentorPhone);
        courseMentorEmail = findViewById(R.id.courseMentorEmail);
    }

    private void populateFields(Course course) {
        courseName.setText(course.name);
        courseStart.setText(course.courseStart);
        courseEnd.setText(course.courseEnd);
        courseMentorName.setText(course.mentorName);
        courseMentorPhone.setText(course.mentorPhone);
        courseMentorEmail.setText(course.mentorEmail);
    }

    private void setupDatePickers() {
        courseStart.setOnClickListener(this);
        courseEnd.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        courseStartDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                courseStart.setText(DateUtil.dateFormat.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        courseEndDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                courseEnd.setText(DateUtil.dateFormat.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        courseStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    courseStartDateDialog.show();
                }
            }
        });

        courseEnd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    courseEndDateDialog.show();
                }
            }
        });
    }

    public void saveCourseChanges(View view) {
        if (action.equals(Intent.ACTION_INSERT)) {
            long termId = Long.parseLong(termUri.getLastPathSegment());
            DataManager.insertCourse(this, termId,
                    courseName.getText().toString().trim(),
                    courseStart.getText().toString().trim(),
                    courseEnd.getText().toString().trim(),
                    courseMentorName.getText().toString().trim(),
                    courseMentorPhone.getText().toString().trim(),
                    courseMentorEmail.getText().toString().trim(),
                    CourseStatus.PLANNED);
        }
        else if (action.equals(Intent.ACTION_EDIT)) {
            course.name = courseName.getText().toString().trim();
            course.courseStart = courseStart.getText().toString().trim();
            course.courseEnd = courseEnd.getText().toString().trim();
            course.mentorName = courseMentorName.getText().toString().trim();
            course.mentorPhone = courseMentorPhone.getText().toString().trim();
            course.mentorEmail = courseMentorEmail.getText().toString().trim();
            course.saveChanges(this);
            setResult(RESULT_OK);
        }
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view == courseStart) {
            courseStartDateDialog.show();
        }
        if (view == courseEnd) {
            courseEndDateDialog.show();
        }
    }
}
