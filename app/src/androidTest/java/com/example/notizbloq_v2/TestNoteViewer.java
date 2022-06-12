package com.example.notizbloq_v2;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestNoteViewer {
    @Rule
    public ActivityScenarioRule<NoteViewer> activityRule =
            new ActivityScenarioRule<>(NoteViewer.class);

}
