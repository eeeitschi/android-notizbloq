package com.example.notizbloq_v2;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("ConstantConditions")
public class Utilities {
    public static final String FILE_EXTENSION = ".blq";
    public static final String TAGS_REGEX = "#([a-zA-Z0-9]*)";

    // Speichern einer Note
    public static boolean saveNote(Context context, Note note) {
        String fileName = note.getFileName();

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            Log.i("Saving", "Files Directory: " + context.getFilesDir().getAbsolutePath());
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

        for (int i = 0; i < noteFiles.size(); i++) {
            try {
                fis = context.openFileInput(noteFiles.get(i));
                ois = new ObjectInputStream(fis);

                notes.add((Note) ois.readObject());

            } catch (IOException | ClassNotFoundException e) {
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

            } catch (IOException | ClassNotFoundException e) {
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

    // Alle Tags aus einer Notiz parsen
    public static HashSet<String> parseTagsFromText(String text) {
        HashSet<String> tagSet = new HashSet<>();
        Pattern p = Pattern.compile(TAGS_REGEX);
        Matcher m = p.matcher(text);
        while (m.find()) {
            tagSet.add(m.group(1));
        }
        return tagSet;
    }

    // Eine Notiz löschen
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void deleteNote(Context context, String fileName) {
        File dir = context.getFilesDir(); // gibt das aktuelle Verzeichnes der App zrück
        File file = new File(dir, fileName);

        if (file.exists()) {
            file.delete();
        }
    }

}
