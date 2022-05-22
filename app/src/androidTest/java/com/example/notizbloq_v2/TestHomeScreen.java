package com.example.notizbloq_v2;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import static org.hamcrest.CoreMatchers.anything;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest

public class TestHomeScreen {
    @Rule
    public ActivityScenarioRule<HomeScreen> activityRule =
            new ActivityScenarioRule<>(HomeScreen.class);

    @Test
    public void TestIfFirstNoteOpens() {
        onData(anything())
                .inAdapterView(withId(R.id.listView))
                .atPosition(0)
                .perform(click());
        onView(withId(R.id.noteTitle))
                .check(matches(isDisplayed()));
    }


}
