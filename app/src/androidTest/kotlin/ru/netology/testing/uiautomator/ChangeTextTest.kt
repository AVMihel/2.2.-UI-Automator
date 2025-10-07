package ru.netology.testing.uiautomator

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

const val SETTINGS_PACKAGE = "com.android.settings"
const val MODEL_PACKAGE = "ru.netology.testing.uiautomator"

const val TIMEOUT = 5000L

@RunWith(AndroidJUnit4::class)
class ChangeTextTest {

    private lateinit var device: UiDevice
    private val textToSet = "Netology"

    private fun waitForPackage(packageName: String) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(packageName)), TIMEOUT)
    }

    @Before
    fun beforeEachTest() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressHome()
        val launcherPackage = device.launcherPackageName
        device.wait(Until.hasObject(By.pkg(launcherPackage)), TIMEOUT)
    }

    @Test
    fun testInternetSettings() {
        waitForPackage(SETTINGS_PACKAGE)
        device.findObject(
            UiSelector().resourceId("android:id/title").instance(1)
        ).click()
    }

    @Test
    fun testChangeText() {
        waitForPackage(MODEL_PACKAGE)

        device.findObject(By.res(MODEL_PACKAGE, "userInput")).text = textToSet
        device.findObject(By.res(MODEL_PACKAGE, "buttonChange")).click()

        val result = device.findObject(By.res(MODEL_PACKAGE, "textToBeChanged")).text
        assertEquals(result, textToSet)
    }

    // Тест 1: Попытка установки пустой строки (включая пробельные символы)
    @Test
    fun testEmptyTextShouldNotChangeOriginalText() {
        waitForPackage(MODEL_PACKAGE)

        // Получаем исходный текст для сравнения
        val originalText = device.findObject(By.res(MODEL_PACKAGE, "textToBeChanged")).text

        // Пытаемся установить пустую строку
        device.findObject(By.res(MODEL_PACKAGE, "userInput")).text = ""
        device.findObject(By.res(MODEL_PACKAGE, "buttonChange")).click()

        // Проверяем, что текст не изменился
        val resultText = device.findObject(By.res(MODEL_PACKAGE, "textToBeChanged")).text
        assertEquals("Текст должен остаться прежним при вводе пустой строки", originalText, resultText)
    }

    // Тест 2: Открытие текста в новой Activity
    @Test
    fun testOpenTextInNewActivity() {
        waitForPackage(MODEL_PACKAGE)

        val testText = "Text for New Activity"

        // Вводим текст и нажимаем кнопку для открытия новой Activity
        device.findObject(By.res(MODEL_PACKAGE, "userInput")).text = testText
        device.findObject(By.res(MODEL_PACKAGE, "buttonActivity")).click()

        // Ждем появления новой Activity и TextView с ID "text"
        device.wait(Until.hasObject(By.res(MODEL_PACKAGE, "text")), TIMEOUT)

        // Получаем текст из TextView во второй Activity
        val resultText = device.findObject(By.res(MODEL_PACKAGE, "text")).text

        // Проверяем, что текст совпадает с введенным
        assertEquals("Текст в новой Activity должен совпадать с введенным", testText, resultText)
    }
}



