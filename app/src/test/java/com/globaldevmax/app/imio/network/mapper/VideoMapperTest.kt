package com.globaldevmax.app.imio.network.mapper

import com.globaldevmax.app.imio.network.dto.VideoDto
import com.globaldevmax.app.imio.network.dto.VideoLocalizationDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VideoMapperTest {

  @Test
  fun toDomain_nullLocalizations_mapsToEmptyList() {
    val video = sampleDto(localizations = null).toDomain()

    assertTrue(video.localizations.isEmpty())
  }

  @Test
  fun toDomain_localizations_mapsEntries() {
    val video = sampleDto(
      localizations = listOf(
        VideoLocalizationDto(locale = "uk", title = "Назва", description = "Опис")
      )
    ).toDomain()

    assertEquals(1, video.localizations.size)
    assertEquals("uk", video.localizations.first().locale)
    assertEquals("Назва", video.localizations.first().title)
    assertEquals("Опис", video.localizations.first().description)
  }

  private fun sampleDto(
    localizations: List<VideoLocalizationDto>?
  ): VideoDto {
    return VideoDto(
      id = "1",
      title = "Title",
      description = "Description",
      format = "hls",
      manifestUrl = "https://example.com/master.m3u8",
      durationMs = 60_000,
      previewImage = "https://example.com/preview.png",
      locale = "uk",
      isPremium = false,
      localizations = localizations
    )
  }
}
