package com.danielrichardson.a2kewl4skewl.activities_course;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.danielrichardson.a2kewl4skewl.R;
import com.danielrichardson.a2kewl4skewl.model.CourseNote;
import com.danielrichardson.a2kewl4skewl.database.DataManager;
import com.danielrichardson.a2kewl4skewl.database.DataProvider;

public class CourseNoteEditorActivity extends AppCompatActivity {

    private long courseId, courseNoteId;
    private Uri courseUri, courseNoteUri;
    private CourseNote courseNote;
    private EditText noteTextField;
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_note_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        noteTextField = findViewById(R.id.etCourseNoteText);
        courseNoteUri = getIntent().getParcelableExtra(DataProvider.COURSE_NOTE_CONTENT_TYPE);

        if (courseNoteUri == null) {
            setTitle(getString(R.string.enter_new_note));
            courseUri = getIntent().getParcelableExtra(DataProvider.COURSE_CONTENT_TYPE);
            courseId = Long.parseLong(courseUri.getLastPathSegment());
            action = Intent.ACTION_INSERT;
        }
        else {
            setTitle(getString(R.string.edit_note));
            courseNoteId = Long.parseLong(courseNoteUri.getLastPathSegment());
            courseNote = DataManager.getCourseNote(this, courseNoteId);
            courseId = courseNote.courseId;
            noteTextField.setText(courseNote.text);
            action = Intent.ACTION_EDIT;
        }
    }

    public void saveCourseNote(View view) {
        if (action.equals(Intent.ACTION_INSERT)) {
            DataManager.insertCourseNote(this, courseId, noteTextField.getText().toString().trim());
            setResult(RESULT_OK);
            finish();
        }
        if (action.equals(Intent.ACTION_EDIT)) {
            courseNote.text = noteTextField.getText().toString().trim();
            courseNote.saveChanges(this);
            setResult(RESULT_OK);
            finish();
        }
    }
}
