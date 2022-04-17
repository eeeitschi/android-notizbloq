package com.example.notizbloq_v2;

import android.content.Context;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class Note implements Serializable {
    private long createdDtTm, modifiedDtTm;
    private String noteTitle, noteText;
    private int imageId = R.drawable.ic_launcher_background; // TODO: Change

    public Note(long createdDtTm, long modifiedDtTm, String title, String text) {
        this.createdDtTm = createdDtTm;
        this.modifiedDtTm = modifiedDtTm;
        this.noteTitle = title;
        this.noteText = text;
    }

    public void setNoteTitle(String newTitle) {
        this.noteTitle = newTitle;
    }

    public void setNoteText(String newText) {
        this.noteText = newText;
    }

    public void setImageId(int newImageId) {
        this.imageId = newImageId;
    }

    public void setModifiedDtTm(long modifiedDtTm) { this.modifiedDtTm = modifiedDtTm; }

    public String getNoteText() {
        return this.noteText;
    }

    public String getNoteTitle() {
        return this.noteTitle;
    }

    public int getImageId() {
        return imageId;
    }

    public long getCreatedDtTm() {
        return createdDtTm;
    }

    public String getFileName() {
        return createdDtTm + Utilities.FILE_EXTENSION;
    }

    // Zum Formatieren der Zeitstempel
    public static String getDtTmFormatted(Context context, long dttm) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", context.getResources().getConfiguration().locale);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date(dttm));
    }
}
