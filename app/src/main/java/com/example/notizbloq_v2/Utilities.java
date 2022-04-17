package com.example.notizbloq_v2;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;

// https://www.youtube.com/watch?v=ysEeCph0GPA

public class Utilities {
    public static final String FILE_EXTENSION = ".blq";

    // Speichern einer Note
    public static boolean saveNote(Context context, Note note) {
        String fileName = note.getFileName();

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {
            fos = context.openFileOutput(fileName, context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(note);

        } catch (IOException e) {
            e.printStackTrace();
            return false;

        } finally { // Streams schliessen
            try {
                oos.close();
                fos.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    // Alle gespeicherten Notizen ausgeben
    public static ArrayList<Note> getAllSavedNotes(Context context) {
        ArrayList<Note> notes = new ArrayList<>();

        File filesDir = context.getFilesDir();
        ArrayList<String> noteFiles = new ArrayList<>();

        for (String file : filesDir.list()) {
            if (file.endsWith(FILE_EXTENSION)) {
                noteFiles.add(file);
            }
        }

        FileInputStream fis = null;
        ObjectInputStream ois = null;

        for(int i = 0; i < noteFiles.size(); i++) {
            try {
                fis = context.openFileInput(noteFiles.get(i));
                ois = new ObjectInputStream(fis);

                notes.add((Note) ois.readObject());

            } catch (IOException | ClassNotFoundException e ) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    fis.close();
                    ois.close();
                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
        return notes;
    }

    // Eine Notiz anhand des Filenamens holen
    public static Note getNoteByName(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        Note note = null;

        if (file.exists()) {
            FileInputStream fis = null;
            ObjectInputStream ois = null;

            try {
                fis = context.openFileInput(fileName);
                ois = new ObjectInputStream(fis);

                note = (Note) ois.readObject();

            } catch (IOException | ClassNotFoundException e ) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    fis.close();
                    ois.close();
                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
        return note;
    }

    // Eine Notiz löschen
    public static void deleteNote(Context context, String fileName) {
        File dir = context.getFilesDir(); // gibt das aktuelle Verzeichnes der App zrück
        File file = new File(dir, fileName);

        if (file.exists()) {
            file.delete();
        }
    }

}
