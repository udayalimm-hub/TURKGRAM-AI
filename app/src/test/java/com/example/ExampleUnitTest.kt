package com.example

import com.example.data.model.TurkgramContentParser
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testTurkgramContentParser() {
    val sample = """
      Konsept: "Güne Başlarken Türk Kahvesi & Minimalist Carousel"

      Görsel Senaryosu: Sabah gün ışığı alan ahşap masada, yanında not defteri ve köpüklü kahve olan 4:5 estetik kare.

      Açıklama Metni:
      "Günün ilk kahvesi içilmeden moda girilemiyor diyenler kimler? ☕️✨ Instagram akışına biraz sabah huzuru bırakıyorum."

      Hashtag'ler: #Instagram #KahveKeyfi #GününKaresi #Keşfet #Reels
    """.trimIndent()

    val idea = TurkgramContentParser.parse(sample)
    assertNotNull(idea)
    assertEquals("Güne Başlarken Türk Kahvesi & Minimalist Carousel", idea?.concept)
    assertTrue(idea?.scenario?.contains("Sabah gün ışığı alan") == true)
    assertTrue(idea?.caption?.contains("Günün ilk kahvesi") == true)
    assertTrue(idea?.hashtags?.contains("#Instagram") == true)
    assertTrue(idea?.hashtags?.contains("#Keşfet") == true)
  }
}
