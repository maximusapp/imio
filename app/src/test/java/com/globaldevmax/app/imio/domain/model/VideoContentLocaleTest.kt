package com.globaldevmax.app.imio.domain.model

import com.globaldevmax.app.imio.core.preferences.VideoContentLocale
import com.globaldevmax.app.imio.ui.screen.home.forContentLocale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VideoContentLocaleTest {

    @Test
    fun forContentLocale_matchesUkVideosForUkUser() {
        val videos = listOf(
            sampleVideo(id = "1", locale = "uk"),
            sampleVideo(id = "2", locale = "en")
        )

        val filtered = videos.forContentLocale(VideoContentLocale.UK)

        assertEquals(listOf("1"), filtered.map { it.id })
    }

    @Test
    fun forContentLocale_includesAllLocaleVideosForAnyUserLanguage() {
        val videos = listOf(
            sampleVideo(id = "1", locale = "uk"),
            sampleVideo(id = "2", locale = VideoContentLocale.ALL),
            sampleVideo(id = "3", locale = "en")
        )

        assertEquals(
            listOf("1", "2"),
            videos.forContentLocale(VideoContentLocale.UK).map { it.id }
        )
        assertEquals(
            listOf("2", "3"),
            videos.forContentLocale(VideoContentLocale.EN).map { it.id }
        )
    }

    @Test
    fun displayTitle_usesLocalizationWhenAvailable() {
        val video = sampleVideo(
            locale = VideoContentLocale.ALL,
            title = "Fallback title",
            localizations = listOf(
                VideoLocalization(locale = "uk", title = "Українська назва"),
                VideoLocalization(locale = "en", title = "English title")
            )
        )

        assertEquals("Українська назва", video.displayTitle("uk"))
        assertEquals("English title", video.displayTitle("en"))
    }

    @Test
    fun displayTitle_fallsBackToRootTitleWhenLocalizationMissing() {
        val video = sampleVideo(
            locale = VideoContentLocale.ALL,
            title = "Fallback title",
            localizations = listOf(
                VideoLocalization(locale = "en", title = "English title")
            )
        )

        assertEquals("Fallback title", video.displayTitle("uk"))
        assertEquals("English title", video.displayTitle("en"))
    }

    @Test
    fun displayDescription_fallsBackToRootDescription() {
        val video = sampleVideo(
            locale = VideoContentLocale.ALL,
            description = "Fallback description",
            localizations = listOf(
                VideoLocalization(
                    locale = "uk",
                    title = "Назва",
                    description = "Опис українською"
                )
            )
        )

        assertEquals("Опис українською", video.displayDescription("uk"))
        assertEquals("Fallback description", video.displayDescription("en"))
    }

    @Test
    fun matchesContentLocale_returnsTrueForAllAndExactLocale() {
        val universal = sampleVideo(locale = VideoContentLocale.ALL)
        val ukrainian = sampleVideo(locale = "uk")

        assertTrue(universal.matchesContentLocale("uk"))
        assertTrue(universal.matchesContentLocale("en"))
        assertTrue(ukrainian.matchesContentLocale("uk"))
        assertFalse(ukrainian.matchesContentLocale("en"))
    }

    private fun sampleVideo(
        id: String = "1",
        locale: String = "uk",
        title: String = "Title",
        description: String = "Description",
        localizations: List<VideoLocalization> = emptyList()
    ): Video {
        return Video(
            id = id,
            title = title,
            description = description,
            format = "hls",
            manifestUrl = "https://example.com/master.m3u8",
            durationMs = 60_000,
            previewImageUrl = "https://example.com/preview.png",
            locale = locale,
            isPremium = false,
            localizations = localizations
        )
    }
}
