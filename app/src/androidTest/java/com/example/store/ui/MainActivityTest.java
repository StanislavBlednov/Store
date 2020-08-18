package com.example.store.ui;

import android.view.MenuItem;
import android.view.View;

import com.example.store.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.action.ViewActions.click;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public final ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void navigateFromFrontToBackToEditAndBack() {
        NavController navController = Navigation.findNavController(activityTestRule.getActivity(), R.id.main__fragment);
        Espresso.onView(ViewMatchers.withId(R.id.backEndFragment)).perform(click());
        assertEquals(Objects.requireNonNull(navController.getCurrentDestination()).getId(), R.id.backEndFragment);
        Espresso.onView(ViewMatchers.withId(R.id.toolbarMenu__add)).perform(click());
        assertEquals(Objects.requireNonNull(navController.getCurrentDestination()).getId(), R.id.editProductFragment);
        Espresso.pressBack();
        assertEquals(Objects.requireNonNull(navController.getCurrentDestination()).getId(), R.id.backEndFragment);
        Espresso.pressBack();
        assertEquals(Objects.requireNonNull(navController.getCurrentDestination()).getId(), R.id.storeFrontFragment);
    }

    @Test
    public void visibilityAddButtonInToolbar() {
        Toolbar toolbar = activityTestRule.getActivity().findViewById(R.id.appBar__toolbar);
        MenuItem add = toolbar.getMenu().findItem(R.id.toolbarMenu__add);
        assertFalse(add.isVisible());
        Espresso.onView(ViewMatchers.withId(R.id.backEndFragment)).perform(click());
        assertTrue(add.isVisible());
        Espresso.onView(ViewMatchers.withId(R.id.toolbarMenu__add)).perform(click());
        assertFalse(add.isVisible());
        Espresso.pressBack();
        assertTrue(add.isVisible());
        Espresso.pressBack();
        assertFalse(add.isVisible());
    }

    @Test
    public void visibilitySaveButtonInToolbar() {
        Toolbar toolbar = activityTestRule.getActivity().findViewById(R.id.appBar__toolbar);
        MenuItem add = toolbar.getMenu().findItem(R.id.toolbarMenu__save);
        assertFalse(add.isVisible());
        Espresso.onView(ViewMatchers.withId(R.id.backEndFragment)).perform(click());
        assertFalse(add.isVisible());
        Espresso.onView(ViewMatchers.withId(R.id.toolbarMenu__add)).perform(click());
        assertTrue(add.isVisible());
        Espresso.pressBack();
        assertFalse(add.isVisible());
        Espresso.pressBack();
        assertFalse(add.isVisible());
    }

    @Test
    public void bottomNavigationLayoutVisibility() {
        ConstraintLayout layout = activityTestRule.getActivity().findViewById(R.id.main__bottomNavLayout);
        assertEquals(View.VISIBLE, layout.getVisibility());
        Espresso.onView(ViewMatchers.withId(R.id.backEndFragment)).perform(click());
        Espresso.onView(ViewMatchers.withId(R.id.toolbarMenu__add)).perform(click());
        assertEquals(View.GONE, layout.getVisibility());
        Espresso.pressBack();
        assertEquals(View.VISIBLE, layout.getVisibility());
    }

    @Test
    public void openDialogExistNow() throws InterruptedException {
        NavController navController = Navigation.findNavController(activityTestRule.getActivity(), R.id.main__fragment);
        Espresso.onView(ViewMatchers.withId(R.id.backEndFragment)).perform(click());
        Espresso.onView(ViewMatchers.withId(R.id.toolbarMenu__add)).perform(click());
        Espresso.onView(ViewMatchers.withId(R.id.editProduct__editName)).perform(ViewActions.typeText("Test"));
        Espresso.onView(ViewMatchers.withId(R.id.editProduct__editPrice)).perform(ViewActions.typeText("500"));
        Espresso.onView(ViewMatchers.withId(R.id.editProduct__editAmount)).perform(ViewActions.typeText("5"));
        Espresso.onView(ViewMatchers.withId(R.id.toolbarMenu__save)).perform(click());
        Thread.sleep(5000);
        Espresso.onView(ViewMatchers.withId(R.id.toolbarMenu__add)).perform(click());
        Espresso.onView(ViewMatchers.withId(R.id.editProduct__editName)).perform(ViewActions.typeText("Test"));
        Espresso.onView(ViewMatchers.withId(R.id.editProduct__editPrice)).perform(ViewActions.typeText("600"));
        Espresso.onView(ViewMatchers.withId(R.id.editProduct__editAmount)).perform(ViewActions.typeText("5"));
        Espresso.onView(ViewMatchers.withId(R.id.toolbarMenu__save)).perform(click());

        assertEquals(Objects.requireNonNull(navController.getCurrentDestination()).getId(), R.id.existNowDialog);
    }
}