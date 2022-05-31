package com.example.notizbloq_v2;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

public class NoteViewer extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    EditText noteTitle, noteText; // Views in diesem Layout
    SeekBar seekBar;
    Handler handler = new Handler();
    String noteFileName;
    Note loadedNote; // mitgegebene Note
    String currentPhotoPath; // Pfad inkl. Dateinamen für das aktuelle Foto.
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    String currentRecordingPath;
    HashSet<String> noteTags;

    @SuppressLint("ClickableViewAccessibility")
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
                currentPhotoPath = loadedNote.getImageUrl();
                currentRecordingPath = loadedNote.getAudioUrl();

                // if the note contains a picture, update the layout and show the image.
                if (loadedNote.getImageUrl() != null) {
                    updateImageView(loadedNote.getImageUrl());
                }

                // Initialisiert die Audio Buttons
                if (loadedNote.getAudioUrl() != null) {
                    Button playAudioButton = findViewById(R.id.btnPlayRecording);
                    playAudioButton.setEnabled(true);
                }
            }
        }

        // Add onclicklistener for the take photo button.
        Button takePhotoBtn = (Button) findViewById(R.id.btnTakePhoto);
        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Start take picture intent.
                dispatchTakePictureIntent();
            }
        });

        // Add onclicklistener for the deletion of a photo.
        Button deletePhotoBtn = (Button) findViewById(R.id.btnDeleteImage);
        deletePhotoBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Reset the Url and update the view.
                currentPhotoPath = null;
                updateImageView(null);
            }
        });

        // Add onTouchListener for starting a recording.
        Button audioRecording = (Button) findViewById(R.id.btnAudioRecording);

        // Add onTouchListener for recording Audios
        audioRecording.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.S)
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i("Recording", "Recording button pressed.");
                        audioRecording.setBackgroundColor(Color.RED);
                        try {
                            startRecording();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i("Recording", "Recording button released.");
                        audioRecording.setBackgroundColor(getResources().getColor(R.color.primary));
                        stopRecording();
                        // Enable Play Button
                        Button playAudioButton = findViewById(R.id.btnPlayRecording);
                        playAudioButton.setEnabled(true);
                        break;
                }
                return true;
            }
        });

        // Play Recording
        Button playRecording = (Button) findViewById(R.id.btnPlayRecording);
        playRecording.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playAudio();
            }
        });

        // Seekbar
        seekBar = findViewById(R.id.seekBar);

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent.
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY)) {
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
                updateImageView(currentPhotoPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Update the ImageView on Screen with the picture linked in the note and enables the button
     * to delete the picture again.
     * @param imageUrl: String, the Url to the image includes  filename and ending.
     */
    private void updateImageView(String imageUrl) {
        ImageView imagePreview = (ImageView) findViewById(R.id.imageViewPictureThumbnail);
        Button deleteImageButton = (Button) findViewById(R.id.btnDeleteImage);
        if (imageUrl != null) {
            try {
                imagePreview.setImageBitmap(BitmapFactory.decodeFile(imageUrl));
                deleteImageButton.setEnabled(true);
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        } else {
            imagePreview.setImageResource(0);
            deleteImageButton.setEnabled(false);
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

    @RequiresApi(api = Build.VERSION_CODES.S)
    private File createAudioFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String audioFileName = "Recording_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File audioFile = File.createTempFile(
                audioFileName,  /* prefix */
                ".3gp",   /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentRecordingPath = audioFile.getAbsolutePath();
        return audioFile;
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void startRecording() throws IOException {
        if (CheckPermissions()) {
            File audioFile = createAudioFile();
            Log.i("Recording", "New Recording file: " + audioFile.getAbsolutePath());
            // below method is used to initialize the media recorder class
            mRecorder = new MediaRecorder();

            // below method is used to set the audio source which we are using a mic.
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            // below method is used to set the output format of the audio.
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            // below method is used to set the audio encoder for our recorded audio.
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            // below method is used to set the output file location for our recorded audio
            mRecorder.setOutputFile(audioFile.getAbsolutePath());
            try {
                // below method will prepare our audio recorder class
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e("TAG", "prepare() failed");
                System.out.println("" + e);    //to display the error
            }
            // start method will start the audio recording.
            mRecorder.start();
            Log.i("Recording", "Recording started.");
        } else {
            RequestPermissions();
        }
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        Log.i("Recording", "Recording stopped.");
    }

    public void playAudio() {
        //for playing our recorded audio we are using media player class.
        mPlayer = new MediaPlayer();
        try {
            //below method is used to set the data source which will be our file name
            mPlayer.setDataSource(currentRecordingPath);
            //below method will prepare our media player
            mPlayer.prepare();
            //below method will start our media player.
            mPlayer.start();
            seekBar.setMax(mPlayer.getDuration());
            UpdateSeekBar updateSeekBar = new UpdateSeekBar();
            handler.post(updateSeekBar);
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
        } catch (IOException e) {
            Log.e("TAG", "prepare() failed");
        }
    }

    public void pausePlaying() {
        //this method will release the media player class and pause the playing of our recorded audio.
        mPlayer.release();
        mPlayer = null;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // this method is called when user will grant the permission for audio recording.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean CheckPermissions() {
        //this method is used to check permission
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        // this method is used to request the permission for audio recording and storage.
        ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_noteviewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_deleteNote:
            case R.id.menu_deleteImage:
                //your action
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * Abspeichern der Notiz. Ist die Notiz bereits angelegt, wird die bestehende Datei ersetzt.
     * @param v: View, der Save-Button benötigt für die Referienzierung die View.
     */
    public void buttonSave(View v) {
        Note note;
        noteTags = Utilities.parseTagsFromText(noteText.getText().toString());

        if (loadedNote == null) { // Wenn eine neue Notiz gespeichert wird
            note = new Note(System.currentTimeMillis()
                    , System.currentTimeMillis()
                    , noteTitle.getText().toString()
                    , noteText.getText().toString()
                    , currentPhotoPath
                    , currentRecordingPath
                    , noteTags);
        } else { // Wenn eine vorhandene Notiz gespeichert wird, wird die Notiz mit gleichen Namen abgespeichert aber neuen Attributen
            note = new Note(loadedNote.getCreatedDtTm()
                    , System.currentTimeMillis()
                    , noteTitle.getText().toString()
                    , noteText.getText().toString()
                    , currentPhotoPath
                    , currentRecordingPath
                    , noteTags);
        }
        if (Utilities.saveNote(this, note)) {
            Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
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

    public class UpdateSeekBar implements Runnable {
        @Override
        public void run() {
            seekBar.setProgress(mPlayer.getCurrentPosition());
            handler.postDelayed(this, 100);
        }
    }
}