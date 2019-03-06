package com.danielrichardson.a2kewl4skewl.activities_term;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import android.widget.Toast;

import com.danielrichardson.a2kewl4skewl.R;
import com.danielrichardson.a2kewl4skewl.activities_course.CourseListActivity;
import com.danielrichardson.a2kewl4skewl.database.DBOpenHelper;
import com.danielrichardson.a2kewl4skewl.database.DataManager;
import com.danielrichardson.a2kewl4skewl.database.DataProvider;
import com.danielrichardson.a2kewl4skewl.model.Term;

import java.util.ArrayList;

public class TermViewerActivity extends AppCompatActivity {

    private static final int TERM_EDITOR_ACTIVITY_CODE = 111;
    private static final int COURSE_LIST_ACTIVITY_CODE = 222;

    private Uri termUri;
    private Term term;
    private CursorAdapter cursorAdapter;
    private TextView titleText, startText, endText;
    private Menu menu;
    private long termId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_viewer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        termUri = intent.getParcelableExtra(DataProvider.TERM_CONTENT_TYPE);

        titleText = findViewById(R.id.tvTermViewTermTitle);
        startText = findViewById(R.id.tvTermViewStartDate);
        endText = findViewById(R.id.tvTermViewEndDate);

        loadTermData();
    }

    private void loadTermData() {
        if (termUri == null) {
            setResult(RESULT_CANCELED);
            finish();
        }
        else {
            termId = Long.parseLong(termUri.getLastPathSegment());
            term = DataManager.getTerm(this, termId);

            setTitle(term.name);
            titleText.setText(term.name + " Details");
            startText.setText(term.termStart);
            endText.setText(term.termEnd);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_term_viewer, menu);
        this.menu = menu;
        showAppropriateMenuOptions();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_term_current:
                return markTermCurrent();
            case R.id.action_edit_term:
                Intent intent = new Intent(this, TermEditorActivity.class);
                Uri uri = Uri.parse(DataProvider.TERMS_URI + "/" + term.termId);
                intent.putExtra(DataProvider.TERM_CONTENT_TYPE, uri);
                startActivityForResult(intent, TERM_EDITOR_ACTIVITY_CODE);
                break;
            case R.id.action_delete_term:
                return deleteTerm();
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private boolean markTermCurrent() {
        Cursor cursor = getContentResolver().query(DataProvider.TERMS_URI, null, null, null, null);
        ArrayList<Term> termList = new ArrayList<>();
        while (cursor.moveToNext()) {
            termList.add(DataManager.getTerm(this, cursor.getLong(cursor.getColumnIndex(DBOpenHelper.TERMS_TABLE_ID))));
        }

        for (Term term : termList) {
            term.makeInactive(this);
        }

        this.term.makeCurrent(this);
        showAppropriateMenuOptions();

        Toast.makeText(TermViewerActivity.this, getString(R.string.term_marked_current), Toast.LENGTH_LONG).show();
        return true;
    }

    private boolean deleteTerm() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int button) {
                if (button == DialogInterface.BUTTON_POSITIVE) {
                    long classCount = term.getClassCount(TermViewerActivity.this);
                    if (classCount == 0) {
                        getContentResolver().delete(DataProvider.TERMS_URI, DBOpenHelper.TERMS_TABLE_ID + " = " + termId, null);

                        Toast.makeText(TermViewerActivity.this, getString(R.string.delete_successful), Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                    else {
                        Toast.makeText(TermViewerActivity.this, getString(R.string.term_delete_fail), Toast.LENGTH_LONG).show();
                    }
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_delete_term)
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
        return true;
    }

    public void showAppropriateMenuOptions() {
        if (term.status == 1) {
            menu.findItem(R.id.action_term_current).setVisible(false);
        }
    }

    public void openCourseListActivity(View view) {
        Intent intent = new Intent(this, CourseListActivity.class);
        intent.putExtra(DataProvider.TERM_CONTENT_TYPE, termUri);
        startActivityForResult(intent, COURSE_LIST_ACTIVITY_CODE);
    }
}
