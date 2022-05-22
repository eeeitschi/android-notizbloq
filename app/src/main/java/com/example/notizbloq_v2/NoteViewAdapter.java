package com.example.notizbloq_v2;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class NoteViewAdapter extends ArrayAdapter<Note> {

    public NoteViewAdapter(@NonNull Context context, ArrayList<Note> arrayList) {
        super(context, 0, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // convertView which is recyclable view
        View currentItemView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.notelist_item, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        Note currentNotePosition = getItem(position);
        assert currentNotePosition != null;

        // then according to the position of the view assign the desired image for the same
        if (currentNotePosition.getImageUrl() != null) {
            ImageView noteImage = currentItemView.findViewById(R.id.item_noteImage);
            //noteImage.setImageResource(currentNotePosition.getThumbnailImageId());
            noteImage.setImageBitmap(BitmapFactory.decodeFile(currentNotePosition.getImageUrl()));
        }

        // then according to the position of the view assign the note title for the same
        TextView noteTitle = currentItemView.findViewById(R.id.item_noteTitle);
        noteTitle.setText(currentNotePosition.getNoteTitle());

        // then according to the position of the view assign the note text for the same
        TextView noteText = currentItemView.findViewById(R.id.item_noteText);
        noteText.setText(currentNotePosition.getNoteText());

        // then return the recyclable view
        return currentItemView;
    }
}
