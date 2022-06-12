package com.example.notizbloq_v2;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;

import android.view.View;
import android.widget.ListView;

import androidx.annotation.IdRes;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestNewNote {

    @Rule
    public ActivityScenarioRule<HomeScreen> activityRule =
            new ActivityScenarioRule<>(HomeScreen.class);

    @Test
    public void CreateNewNoteTest() {
        // get length of actual note list
        int listCount = CountHelper.getCountFromListUsingTypeSafeMatcher(R.id.listView);

        // create new note
        onView(withId(R.id.floatingActionButton)).perform(click());

        // set note title
        onView(withId(R.id.noteTitle)).perform(typeText("Instrumented Test 1"));

        // close keyboard that the save button will be visible
        onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard());

        // save the newly created note
        onView(withId(R.id.btnSaveNote)).perform(click());

        // check if the length of the notelist has increased by 1.
        onView(withId(R.id.listView)).check(matches(Matchers.withListSize(listCount + 1)));
    }

    @Test
    public void DeleteNoteTest() {
        //set noteTitle
        String noteTitle = "Instrumented Test: ";

        // get length of actual note list
        int listCount = CountHelper.getCountFromListUsingTypeSafeMatcher(R.id.listView);

        // create new note
        onView(withId(R.id.floatingActionButton)).perform(click());

        // set note title
        onView(withId(R.id.noteTitle)).perform(typeText(noteTitle + listCount));

        // close keyboard that the save button will be visible
        onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard());

        // save the newly created note
        onView(withId(R.id.btnSaveNote)).perform(click());

        // check if the length of the notelist has increased by 1.
        onView(withId(R.id.listView)).check(matches(Matchers.withListSize(listCount + 1)));

        // open latest note in listview
        onData(anything())
                .inAdapterView(allOf(withId(R.id.listView), isCompletelyDisplayed()))
                .atPosition(listCount).perform(click());

        // check if newly created note title matches with title
        onView(withId(R.id.noteTitle)).check(matches(withText(noteTitle + listCount)));

        // click to delete note
        onView(withId(R.id.btnDelete)).perform(click());

        // click on the ok button to delete the note
        onView(withId(android.R.id.button1)).perform(click());

        // check if the length of the notelist has the initial length.
        onView(withId(R.id.listView)).check(matches(Matchers.withListSize(listCount)));
    }
}


/**
 * Matcherclass that compares List size with profided Value.
 */
class Matchers {
    public static Matcher<View> withListSize(final int size) {
        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(final View view) {
                return ((ListView) view).getCount() == size;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("ListView should have " + size + " items");
            }
        };
    }
}

/**
 * Counthelperclass that returns the Count from the current list.
 */
class CountHelper {
    private static int count;

    public static int getCountFromListUsingTypeSafeMatcher(@IdRes int listViewId) {
        count = 0;

        Matcher<View> matcher = new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                count = ((ListView) item).getCount();
                return true;
            }

            @Override
            public void describeTo(Description description) {
            }
        };

        onView(withId(listViewId)).check(matches(matcher));
        int result = count;
        count = 0;
        return result;
    }
}