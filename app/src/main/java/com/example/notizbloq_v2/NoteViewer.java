package com.example.notizbloq_v2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class NoteViewer extends AppCompatActivity {

    EditText noteTitle, noteText; // Views in diesem Layout
    String noteFileName;
    Note loadedNote; // mitgegebene Note

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_viewer);

        // Views als Objekte initialisieren, damit nacher auf sie zugegriffen werden kann
        noteTitle = (EditText) findViewById(R.id.noteTitle);
        noteText = (EditText) findViewById(R.id.noteText);

        // Den Intent abfragen
        Intent intent = getIntent();
        noteFileName = intent.getStringExtra("NOTE_FILE");

        // Wenn eine Note mitgegeben wurde, die entsprechende Notiz aus dem Speicher holen und laden
        if (noteFileName != null && !noteFileName.isEmpty() ) {
            loadedNote = Utilities.getNoteByName(this, noteFileName);

            if(loadedNote != null) {
                noteTitle.setText(loadedNote.getNoteTitle());
                noteText.setText(loadedNote.getNoteText());
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_noteviewer, menu);
        return true;
    }

    public void buttonSave(View v) {
        Note note;

        if (loadedNote == null) { // Wenn eine neue Notiz gespeichert wird
            note = new Note(System.currentTimeMillis(), System.currentTimeMillis(), noteTitle.getText().toString(), noteText.getText().toString());
        } else { // Wenn eine vorhandene Notiz gespeichert wird, wird die Notiz mit gleichen Namen abgespeichert aber neuen Attributen
            note = new Note(loadedNote.getCreatedDtTm(), System.currentTimeMillis(), noteTitle.getText().toString(), noteText.getText().toString());
        }
        if (Utilities.saveNote(this, note)) {
            Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
            //HomeScreen.noteViewAdapter.notifyDataSetChanged();
            finish(); // beendet die Activity und kehrt zum HomeScreen zurück
        }

    }

    public void deleteNote(View view) {
        if (loadedNote == null) {
            finish();
        } else {
            // Dialog zur Abfrage des Löschens
            AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                    .setTitle("Delete")
                    .setMessage("You are about to delete this note. Are you sure?")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Utilities.deleteNote(getApplicationContext(), loadedNote.getCreatedDtTm() + Utilities.FILE_EXTENSION);
                            Toast.makeText(getApplicationContext(), "note deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .setNegativeButton("no", null)
                    .setCancelable(false);
            dialog.show();
        }
    }
}