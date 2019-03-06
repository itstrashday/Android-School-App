package com.danielrichardson.a2kewl4skewl.activities_assessment;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.danielrichardson.a2kewl4skewl.R;
import com.danielrichardson.a2kewl4skewl.database.DBOpenHelper;
import com.danielrichardson.a2kewl4skewl.database.DataProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class AssessmentNoteListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ASSESSMENT_NOTE_EDITOR_ACTIVITY_CODE = 111;
    private static final int ASSESSMENT_NOTE_VIEWER_ACTIVITY_CODE = 222;

    private long assessmentId;
    private Uri assessmentUri;
    private CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_note_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        assessmentUri = getIntent().getParcelableExtra(DataProvider.ASSESSMENT_CONTENT_TYPE);
        assessmentId = Long.parseLong(assessmentUri.getLastPathSegment());
        bindAssessmentNoteList();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AssessmentNoteListActivity.this, AssessmentNoteEditorActivity.class);
                intent.putExtra(DataProvider.ASSESSMENT_CONTENT_TYPE, assessmentUri);
                startActivityForResult(intent, ASSESSMENT_NOTE_EDITOR_ACTIVITY_CODE);
            }
        });
        getLoaderManager().initLoader(0, null, this);
    }

    private void bindAssessmentNoteList() {
        String[] from = {DBOpenHelper.ASSESSMENT_NOTE_TEXT};
        int[] to = {R.id.tvAssessmentNoteText};
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.assessment_note_list_item, null, from, to, 0);
        DataProvider database = new DataProvider();

        ListView list = findViewById(R.id.assessmentNoteListView);
        list.setAdapter(cursorAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AssessmentNoteListActivity.this, AssessmentNoteViewerActivity.class);
                Uri uri = Uri.parse(DataProvider.ASSESSMENT_NOTES_URI + "/" + id);
                intent.putExtra(DataProvider.ASSESSMENT_NOTE_CONTENT_TYPE, uri);
                startActivityForResult(intent, ASSESSMENT_NOTE_VIEWER_ACTIVITY_CODE);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, DataProvider.ASSESSMENT_NOTES_URI, DBOpenHelper.ASSESSMENT_NOTES_COLUMNS, DBOpenHelper.ASSESSMENT_NOTE_ASSESSMENT_ID + " = " + this.assessmentId, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        restartLoader();
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }
}
