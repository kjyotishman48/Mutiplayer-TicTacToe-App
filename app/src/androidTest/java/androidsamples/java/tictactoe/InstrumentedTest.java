package androidsamples.java.tictactoe;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static com.google.android.gms.common.ConnectionResult.TIMEOUT;

import static org.junit.Assert.assertEquals;

import androidx.test.espresso.accessibility.AccessibilityChecks;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class InstrumentedTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    UiDevice device = UiDevice.getInstance(getInstrumentation());

    @BeforeClass
    public static void enableAccessibilityChecks() {
        AccessibilityChecks.enable();
    }

    @Test
    public void test1() throws UiObjectNotFoundException {
        //Must be initially logged out of the device
        UiObject emailBox = device.findObject(new UiSelector().clickable(true).descriptionMatches("Enter your email here"));
        UiObject passwordBox = device.findObject(new UiSelector().clickable(true).descriptionMatches("Enter your password here"));
        UiObject loginBtn = device.findObject(new UiSelector().clickable(true).descriptionMatches("Press here to register or login"));
        emailBox.waitForExists(TIMEOUT);
        passwordBox.waitForExists(TIMEOUT);
        loginBtn.waitForExists(TIMEOUT);
        assertEquals(true, emailBox.exists());
        assertEquals(true, passwordBox.exists());
        assertEquals(true, loginBtn.exists());
        emailBox.legacySetText("test1EmailID@gmail.com");
        passwordBox.legacySetText("user123");
        loginBtn.clickAndWaitForNewWindow();
    }
}


