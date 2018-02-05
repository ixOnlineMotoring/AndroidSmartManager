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
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class PhotoPickTest
{

    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);

    @Test
    public void photoPickTest()
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
        customEditText2.perform(scrollTo(), replaceText("indiatest"), closeSoftKeyboard());

        ViewInteraction customEditText3 = onView(
                allOf(withId(R.id.password),
                        withParent(withId(R.id.llLoginContainer))));
        customEditText3.perform(scrollTo(), click());

        ViewInteraction customEditText4 = onView(
                allOf(withId(R.id.password),
                        withParent(withId(R.id.llLoginContainer))));
        customEditText4.perform(scrollTo(), replaceText("t35t!123"), closeSoftKeyboard());

        ViewInteraction customButton = onView(
                allOf(withId(R.id.sign_in_button), withText("Log in")));
        customButton.perform(scrollTo(), click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try
        {
            Thread.sleep(15000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        ViewInteraction myLinearLayout = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.expandableHeightGridView1),
                                withParent(withId(R.id.llHome))),
                        1),
                        isDisplayed()));
        myLinearLayout.perform(click());

        ViewInteraction myLinearLayout2 = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.expandableHeightGridView1),
                                withParent(withId(R.id.llHome))),
                        0),
                        isDisplayed()));
        myLinearLayout2.perform(click());

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

        ViewInteraction customTextView = onView(
                allOf(withId(R.id.tvRegNumber), withText("Reg?"), isDisplayed()));
        customTextView.perform(replaceText("Reg? | Green | Gsg152"), closeSoftKeyboard());

        ViewInteraction customTextView2 = onView(
                allOf(withId(R.id.tvDepartment), withText("Awesome(Used)"), isDisplayed()));
        customTextView2.perform(replaceText("Awesome(Used) | 121 212 Km| 93 Days"), closeSoftKeyboard());

        ViewInteraction customTextViewLight = onView(
                allOf(withId(R.id.tvExtras), withText("Extras ✓"), isDisplayed()));
        customTextViewLight.perform(replaceText("Extras ✓| Comments ✓| Photos 0| Videos 0"), closeSoftKeyboard());

        ViewInteraction customTextView3 = onView(
                allOf(withId(R.id.tvRegNumber), withText("Reg?"), isDisplayed()));
        customTextView3.perform(replaceText("Reg? | White | 58732"), closeSoftKeyboard());

        ViewInteraction customTextView4 = onView(
                allOf(withId(R.id.tvDepartment), withText("Used"), isDisplayed()));
        customTextView4.perform(replaceText("Used | 14 Km| 114 Days"), closeSoftKeyboard());

        ViewInteraction customTextViewLight2 = onView(
                allOf(withId(R.id.tvExtras), withText("Extras ✘"), isDisplayed()));
        customTextViewLight2.perform(replaceText("Extras ✘| Comments ✘| Photos 0| Videos 0"), closeSoftKeyboard());

        ViewInteraction customTextView5 = onView(
                allOf(withId(R.id.tvRegNumber), withText("Reg?"), isDisplayed()));
        customTextView5.perform(replaceText("Reg? | White | 16272727"), closeSoftKeyboard());

        ViewInteraction customTextView6 = onView(
                allOf(withId(R.id.tvDepartment), withText("Used"), isDisplayed()));
        customTextView6.perform(replaceText("Used | 15 Km| 111 Days"), closeSoftKeyboard());

        ViewInteraction customTextViewLight3 = onView(
                allOf(withId(R.id.tvExtras), withText("Extras ✘"), isDisplayed()));
        customTextViewLight3.perform(replaceText("Extras ✘| Comments ✘| Photos 0| Videos 0"), closeSoftKeyboard());

        ViewInteraction customTextView7 = onView(
                allOf(withId(R.id.tvRegNumber), withText("Reg?"), isDisplayed()));
        customTextView7.perform(replaceText("Reg? | White | 1611551"), closeSoftKeyboard());

        ViewInteraction customTextView8 = onView(
                allOf(withId(R.id.tvDepartment), withText("Used"), isDisplayed()));
        customTextView8.perform(replaceText("Used | 1 234 Km| 111 Days"), closeSoftKeyboard());

        ViewInteraction customTextViewLight4 = onView(
                allOf(withId(R.id.tvExtras), withText("Extras ✘"), isDisplayed()));
        customTextViewLight4.perform(replaceText("Extras ✘| Comments ✘| Photos 0| Videos 0"), closeSoftKeyboard());

        ViewInteraction customTextView9 = onView(
                allOf(withId(R.id.tvRegNumber), withText("Reg?"), isDisplayed()));
        customTextView9.perform(replaceText("Reg? | White | Aghahahaha"), closeSoftKeyboard());

        ViewInteraction customTextView10 = onView(
                allOf(withId(R.id.tvDepartment), withText("Used"), isDisplayed()));
        customTextView10.perform(replaceText("Used | 1 242 Km| 106 Days"), closeSoftKeyboard());

        ViewInteraction customTextViewLight5 = onView(
                allOf(withId(R.id.tvExtras), withText("Extras ✘"), isDisplayed()));
        customTextViewLight5.perform(replaceText("Extras ✘| Comments ✘| Photos 0| Videos 0"), closeSoftKeyboard());

        ViewInteraction linearLayout = onView(
                allOf(childAtPosition(
                        withId(R.id.lvVehicleDetails),
                        1),
                        isDisplayed()));
        linearLayout.perform(click());

        ViewInteraction customTextView11 = onView(
                allOf(withId(R.id.tvRegNumber), withText("Reg?")));
        customTextView11.perform(scrollTo(), replaceText("Reg? | White | 58732"), closeSoftKeyboard());

        ViewInteraction customTextView12 = onView(
                allOf(withId(R.id.tvDepartment), withText("Used")));
        customTextView12.perform(scrollTo(), replaceText("Used | 14 Km | 114 Days"), closeSoftKeyboard());

        ViewInteraction relativeLayout = onView(
                childAtPosition(
                        allOf(withId(R.id.gvImages),
                                withParent(withId(R.id.rlImage))),
                        0));
        relativeLayout.perform(scrollTo(), click());

        ViewInteraction textView = onView(
                allOf(withId(android.R.id.text1), withText("Select from Gallery"),
                        childAtPosition(
                                allOf(withClassName(is("com.android.internal.app.AlertController$RecycleListView")),
                                        withParent(withClassName(is("android.widget.FrameLayout")))),
                                1),
                        isDisplayed()));
        textView.perform(click());

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

        ViewInteraction linearLayout2 = onView(
                allOf(childAtPosition(
                        withId(R.id.gridGallery),
                        0),
                        isDisplayed()));
        linearLayout2.perform(click());

        ViewInteraction linearLayout3 = onView(
                allOf(childAtPosition(
                        withId(R.id.gridGallery),
                        1),
                        isDisplayed()));
        linearLayout3.perform(click());

        ViewInteraction linearLayout4 = onView(
                allOf(childAtPosition(
                        withId(R.id.gridGallery),
                        2),
                        isDisplayed()));
        linearLayout4.perform(click());

        ViewInteraction button = onView(
                allOf(withId(R.id.btnGalleryOk), withText("OK"),
                        withParent(withId(R.id.llBottomContainer)),
                        isDisplayed()));
        button.perform(click());

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

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.tvDone), withText("Done"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()));
        appCompatTextView.perform(click());

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

        ViewInteraction relativeLayout2 = onView(
                childAtPosition(
                        allOf(withId(R.id.gvImages),
                                withParent(withId(R.id.rlImage))),
                        3));
        relativeLayout2.perform(scrollTo(), click());

        ViewInteraction textView2 = onView(
                allOf(withId(android.R.id.text1), withText("Take from Camera"),
                        childAtPosition(
                                allOf(withClassName(is("com.android.internal.app.AlertController$RecycleListView")),
                                        withParent(withClassName(is("android.widget.FrameLayout")))),
                                0),
                        isDisplayed()));
        textView2.perform(click());

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

        ViewInteraction appCompatTextView2 = onView(
                allOf(withId(R.id.tvDone), withText("Done"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()));
        appCompatTextView2.perform(click());

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

        ViewInteraction relativeLayout3 = onView(
                childAtPosition(
                        allOf(withId(R.id.gvImages),
                                withParent(withId(R.id.rlImage))),
                        4));
        relativeLayout3.perform(scrollTo(), click());

        ViewInteraction textView3 = onView(
                allOf(withId(android.R.id.text1), withText("Select from Gallery"),
                        childAtPosition(
                                allOf(withClassName(is("com.android.internal.app.AlertController$RecycleListView")),
                                        withParent(withClassName(is("android.widget.FrameLayout")))),
                                1),
                        isDisplayed()));
        textView3.perform(click());

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

        ViewInteraction button2 = onView(
                allOf(withId(R.id.btnGalleryOk), withText("OK"),
                        withParent(withId(R.id.llBottomContainer)),
                        isDisplayed()));
        button2.perform(click());

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

        pressBack();

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
