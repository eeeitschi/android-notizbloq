package com.example.notizbloq_v2;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class NoteViewer extends AppCompatActivity {

    EditText noteTitle, noteText; // Views in diesem Layout
    String noteFileName;
    Note loadedNote; // mitgegebene Note
    static final int REQUEST_IMAGE_CAPTURE = 1;

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
        if (noteFileName != null && !noteFileName.isEmpty()) {
            loadedNote = Utilities.getNoteByName(this, noteFileName);

            if (loadedNote != null) {
                noteTitle.setText(loadedNote.getNoteTitle());
                noteText.setText(loadedNote.getNoteText());
            }
        }

        // Add onclicklistener for the take photo button
        Button button = (Button) findViewById(R.id.btnTakePhoto);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // start take picture intent.
                dispatchTakePictureIntent();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView imageView = findViewById(R.id.imageViewPictureThumbnail);
            imageView.setImageBitmap(imageBitmap);
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