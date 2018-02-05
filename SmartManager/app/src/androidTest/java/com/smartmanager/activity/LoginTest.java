package com.smartmanager.activity;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.smartmanager.android.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LoginTest
{

    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);

    @Test
    public void loginTest()
    {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try
        {
            Thread.sleep(5000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        ViewInteraction customEditText = onView(
                allOf(withId(R.id.email),
                        withParent(withId(R.id.llLoginContainer))));
        customEditText.perform(scrollTo(), click());

        ViewInteraction customEditText2 = onView(
                allOf(withId(R.id.email),
                        withParent(withId(R.id.llLoginContainer))));
        customEditText2.perform(scrollTo(), replaceText("inditest"), closeSoftKeyboard());

        ViewInteraction customEditText3 = onView(
                allOf(withId(R.id.email), withText("inditest"),
                        withParent(withId(R.id.llLoginContainer))));
        customEditText3.perform(scrollTo(), click());

        ViewInteraction customEditText4 = onView(
                allOf(withId(R.id.email), withText("inditest"),
                        withParent(withId(R.id.llLoginContainer))));
        customEditText4.perform(scrollTo(), click());

        ViewInteraction customEditText5 = onView(
                allOf(withId(R.id.email), withText("inditest"),
                        withParent(withId(R.id.llLoginContainer))));
        customEditText5.perform(scrollTo(), click());

        ViewInteraction customEditText6 = onView(
                allOf(withId(R.id.email), withText("inditest"),
                        withParent(withId(R.id.llLoginContainer))));
        customEditText6.perform(scrollTo(), replaceText("indiatest"), closeSoftKeyboard());

        ViewInteraction customEditText7 = onView(
                allOf(withId(R.id.password),
                        withParent(withId(R.id.llLoginContainer))));
        customEditText7.perform(scrollTo(), replaceText("t35t!123"), closeSoftKeyboard());

        ViewInteraction customButton = onView(
                allOf(withId(R.id.sign_in_button), withText("Log in")));
        customButton.perform(scrollTo(), click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try
        {
            Thread.sleep(5000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        ViewInteraction myLinearLayout = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.expandableHeightGridView1),
                                withParent(withId(R.id.llHome))),
                        0),
                        isDisplayed()));
        myLinearLayout.perform(click());

        ViewInteraction customTextView = onView(
                allOf(withId(R.id.ivHome), withText("Back"),
                        withParent(allOf(withId(R.id.tableRow1),
                                withParent(withId(R.id.llBottomMenu)))),
                        isDisplayed()));
        customTextView.perform(click());

        ViewInteraction myLinearLayout2 = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.expandableHeightGridView1),
                                withParent(withId(R.id.llHome))),
                        6),
                        isDisplayed()));
        myLinearLayout2.perform(click());

        ViewInteraction myLinearLayout3 = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.expandableHeightGridView1),
                                withParent(withId(R.id.llHome))),
                        2),
                        isDisplayed()));
        myLinearLayout3.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try
        {
            Thread.sleep(2000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        ViewInteraction customEditText8 = onView(
                allOf(withId(R.id.edYear), withText("2017")));
        customEditText8.perform(scrollTo(), click());

        ViewInteraction customTextViewLight = onView(
                allOf(withId(R.id.tvText), withText("2016"),
                        childAtPosition(
                                withId(R.id.lvDialog),
                                1),
                        isDisplayed()));
        customTextViewLight.perform(click());

        ViewInteraction customEditText9 = onView(
                withId(R.id.edMMCode));
        customEditText9.perform(scrollTo(), click());

        ViewInteraction customEditText10 = onView(
                withId(R.id.edMMCode));
        customEditText10.perform(scrollTo(), replaceText("04030518"), closeSoftKeyboard());

        ViewInteraction customEditText11 = onView(
                withId(R.id.edKilometers));
        customEditText11.perform(scrollTo(), replaceText("345"), closeSoftKeyboard());

        ViewInteraction customButton2 = onView(
                allOf(withId(R.id.btnSynopsisSummary), withText("Continue to Synopsis Summary"),
                        withParent(withId(R.id.addLayout))));
        customButton2.perform(scrollTo(), click());

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position)
    {

        return new TypeSafeMatcher<View>()
        {
            @Override
            public void describeTo(Description description)
            {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view)
            {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
