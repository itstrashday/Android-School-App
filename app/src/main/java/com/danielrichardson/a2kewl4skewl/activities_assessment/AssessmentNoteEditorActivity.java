package com.danielrichardson.a2kewl4skewl.activities_assessment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.danielrichardson.a2kewl4skewl.R;
import com.danielrichardson.a2kewl4skewl.model.AssessmentNote;
import com.danielrichardson.a2kewl4skewl.database.DataManager;
import com.danielrichardson.a2kewl4skewl.database.DataProvider;

public class AssessmentNoteEditorActivity extends AppCompatActivity {

    private long assessmentNoteId, assessmentId;
    private Uri assessmentNoteUri, assessmentUri;
    private AssessmentNote assessmentNote;
    private EditText assessmentNoteTextField;
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_note_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        assessmentNoteTextField = findViewById(R.id.etAssessmentNoteText);
        assessmentNoteUri = getIntent().getParcelableExtra(DataProvider.ASSESSMENT_NOTE_CONTENT_TYPE);

        if (assessmentNoteUri == null) {
            setTitle(R.string.enter_new_note);
            assessmentUri = getIntent().getParcelableExtra(DataProvider.ASSESSMENT_CONTENT_TYPE);
            assessmentId = Long.parseLong(assessmentUri.getLastPathSegment());
            action = Intent.ACTION_INSERT;
        }
        else {
            setTitle(R.string.edit_note);
            assessmentNoteId = Long.parseLong(assessmentNoteUri.getLastPathSegment());
            assessmentNote = DataManager.getAssessmentNote(this, assessmentNoteId);
            assessmentId = assessmentNote.assessmentId;
            assessmentNoteTextField.setText(assessmentNote.text);
            action = Intent.ACTION_EDIT;
        }
    }

    public void saveAssessmentNote(View view) {
        if (action.equals(Intent.ACTION_INSERT)) {
            DataManager.insertAssessmentNote(this, assessmentId, assessmentNoteTextField.getText().toString().trim());
            setResult(RESULT_OK);
            finish();
        }
        if (action.equals(Intent.ACTION_EDIT)) {
            assessmentNote.text = assessmentNoteTextField.getText().toString().trim();
            assessmentNote.saveChanges(this);
            setResult(RESULT_OK);
            finish();
        }
    }
}
