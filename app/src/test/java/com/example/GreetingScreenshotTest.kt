package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.TurkgramIdea
import com.example.ui.components.TurkgramIdeaCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sample = TurkgramIdea(
      concept = "Güne Başlarken Türk Kahvesi",
      scenario = "Sabah gün ışığı alan çalışma masasında köpüklü kahve.",
      caption = "Günün ilk kahvesi içilmeden moda girilemiyor diyenler kimler? ☕️✨",
      hashtags = listOf("#Turkgram", "#KahveKeyfi", "#Keşfet")
    )
    composeTestRule.setContent {
      MyApplicationTheme {
        TurkgramIdeaCard(
          idea = sample,
          isSaved = false,
          onToggleSave = {},
          onCopyText = { _, _ -> }
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
