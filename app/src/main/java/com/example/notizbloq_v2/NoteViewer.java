package com.example.notizbloq_v2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteViewer extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    EditText noteTitle, noteText; // Views in diesem Layout
    String noteFileName;
    Note loadedNote; // mitgegebene Note
    String currentPhotoPath; // Pfad inkl. Dateinamen für das aktuelle Foto.

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

                // if the note contains a picture, update the layout and show the image.
                if (loadedNote.getImageUrl() != null) {
                    showImageOnScreen(loadedNote.getImageUrl());
                }
            }
        }

        // Add onclicklistener for the take photo button.
        Button button = (Button) findViewById(R.id.btnTakePhoto);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Start take picture intent.
                dispatchTakePictureIntent();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent.
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // Create the File where the photo should go.
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File.

            }
            // Continue only if the File was successfully created.
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                if (loadedNote != null) loadedNote.setImageUrl(currentPhotoPath);
                showImageOnScreen(currentPhotoPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * [showImageOnScreen]
     * Update the ImageView on Screen with the picture linked in the note.
     * @param imageUrl: String, the Url to the image includes  filename and ending.
     */
    private void showImageOnScreen(String imageUrl) {
        ImageView imagePreview = (ImageView) findViewById(R.id.imageViewPictureThumbnail);
        try {
            imagePreview.setImageBitmap(BitmapFactory.decodeFile(imageUrl));
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_noteviewer, menu);
        return true;
    }

    public void buttonSave(View v) {
        Note note;

        if (loadedNote == null) { // Wenn eine neue Notiz gespeichert wird
            note = new Note(System.currentTimeMillis(), System.currentTimeMillis(), noteTitle.getText().toString(), noteText.getText().toString(), currentPhotoPath);
        } else { // Wenn eine vorhandene Notiz gespeichert wird, wird die Notiz mit gleichen Namen abgespeichert aber neuen Attributen
            note = new Note(loadedNote.getCreatedDtTm(), System.currentTimeMillis(), noteTitle.getText().toString(), noteText.getText().toString(), loadedNote.getImageUrl());
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