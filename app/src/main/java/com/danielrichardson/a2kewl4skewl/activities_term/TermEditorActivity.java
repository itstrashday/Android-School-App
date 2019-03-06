package com.danielrichardson.a2kewl4skewl.activities_term;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.danielrichardson.a2kewl4skewl.R;
import com.danielrichardson.a2kewl4skewl.database.DataManager;
import com.danielrichardson.a2kewl4skewl.database.DataProvider;
import com.danielrichardson.a2kewl4skewl.model.Term;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TermEditorActivity extends AppCompatActivity implements View.OnClickListener {

    private String action;
    private Term term;
    private EditText termNameField, termStartDateField, termEndDateField;
    private DatePickerDialog termStartDateDialog, termEndDateDialog;
    private SimpleDateFormat dateFormat;
    private DataProvider database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = new DataProvider();

        termNameField = findViewById(R.id.termNameEditText);
        termStartDateField = findViewById(R.id.termStartDateEditText);
        termStartDateField.setInputType(InputType.TYPE_NULL);
        termEndDateField = findViewById(R.id.termEndDateEditText);
        termEndDateField.setInputType(InputType.TYPE_NULL);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(DataProvider.TERM_CONTENT_TYPE);

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.add_new_term));
        }
        else {
            action = Intent.ACTION_EDIT;
            long termId = Long.parseLong(uri.getLastPathSegment());
            term = DataManager.getTerm(this, termId);
            setTitle(getString(R.string.edit) + " " + term.name);
            populateFields(term);
        }
        setupDatePickers();
    }

    private void populateFields(Term term) {
        termNameField.setText(term.name);
        termStartDateField.setText(term.termStart);
        termEndDateField.setText(term.termEnd);
    }

    private void getInputFromFields() {
        term.name = termNameField.getText().toString().trim();
        term.termStart = termStartDateField.getText().toString().trim();
        term.termEnd = termEndDateField.getText().toString().trim();
    }

    private void setupDatePickers() {
        termStartDateField.setOnClickListener(this);
        termEndDateField.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        termStartDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                termStartDateField.setText(dateFormat.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        termEndDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                termEndDateField.setText(dateFormat.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        termStartDateField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    termStartDateDialog.show();
                }
            }
        });
    }

    public void saveTermChanges(View view) {
        if (action.equals(Intent.ACTION_INSERT)) {
            term = new Term();
            getInputFromFields();

            DataManager.insertTerm(this, term.name, term.termStart, term.termEnd, term.status);
            Toast.makeText(this, term.name + " " + getString(R.string.added), Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
        }
        else if (action.equals(Intent.ACTION_EDIT)) {
            getInputFromFields();
            term.saveChanges(this);
            Toast.makeText(this, getString(R.string.update_successful), Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
        }
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view == termStartDateField) {
            termStartDateDialog.show();
        }
        if (view == termEndDateField) {
            termEndDateDialog.show();
        }
    }
}
