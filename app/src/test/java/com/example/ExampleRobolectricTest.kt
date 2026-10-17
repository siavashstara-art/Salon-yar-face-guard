package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Banoot Bridal & Fashion", appName)
  }

  @Test
  fun `verify bridal dress catalog and engine initialization`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = com.example.data.BanootRepository(context)
    val dresses = repo.dresses
    org.junit.Assert.assertTrue("Bridal catalog should not be empty", dresses.isNotEmpty())
    
    val engine = com.example.engine.BanootCoreEngine(context)
    org.junit.Assert.assertNotNull(engine)
  }
}
