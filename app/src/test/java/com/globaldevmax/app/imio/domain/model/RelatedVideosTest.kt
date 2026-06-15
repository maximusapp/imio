package com.globaldevmax.app.imio.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RelatedVideosTest {

    @Test
    fun relatedVideosFor_withPremiumSubscription_includesPremiumVideos() {
        val current = sampleVideo(id = "current")
        val videos = listOf(
            current,
            sampleVideo(id = "free", isPremium = false),
            sampleVideo(id = "premium", isPremium = true)
        )

        val related = videos.relatedVideosFor(
            currentVideo = current,
            isPremiumActive = true
        )

        assertEquals(listOf("free", "premium"), related.map { it.id })
    }

    @Test
    fun relatedVideosFor_withoutPremiumSubscription_excludesPremiumVideos() {
        val current = sampleVideo(id = "current")
        val videos = listOf(
            current,
            sampleVideo(id = "free", isPremium = false),
            sampleVideo(id = "premium", isPremium = true)
        )

        val related = videos.relatedVideosFor(
            currentVideo = current,
            isPremiumActive = false
        )

        assertEquals(listOf("free"), related.map { it.id })
    }

    @Test
    fun relatedVideosFor_prioritizesRelatedIdsThenFillsUpToLimit() {
        val current = sampleVideo(
            id = "current",
            relatedVideoIds = listOf("related-1", "related-2")
        )
        val videos = buildList {
            add(current)
            add(sampleVideo(id = "related-1"))
            add(sampleVideo(id = "related-2"))
            repeat(30) { index ->
                add(sampleVideo(id = "extra-$index"))
            }
        }

        val related = videos.relatedVideosFor(
            currentVideo = current,
            isPremiumActive = true,
            limit = 25
        )

        assertEquals(25, related.size)
        assertEquals(listOf("related-1", "related-2"), related.take(2).map { it.id })
        assertTrue(related.drop(2).all { it.id.startsWith("extra-") })
    }

    @Test
    fun relatedVideosFor_withoutPremium_skipsPremiumRelatedIdsAndFillsWithAccessibleVideos() {
        val current = sampleVideo(
            id = "current",
            relatedVideoIds = listOf("premium-related", "free-related")
        )
        val videos = buildList {
            add(current)
            add(sampleVideo(id = "premium-related", isPremium = true))
            add(sampleVideo(id = "free-related", isPremium = false))
            repeat(30) { index ->
                add(sampleVideo(id = "free-extra-$index", isPremium = false))
            }
        }

        val related = videos.relatedVideosFor(
            currentVideo = current,
            isPremiumActive = false,
            limit = 25
        )

        assertEquals(25, related.size)
        assertEquals("free-related", related.first().id)
        assertTrue(related.none { it.isPremium })
        assertTrue(related.none { it.id == "premium-related" })
    }

    @Test
    fun relatedVideosFor_returnsFewerThanLimitWhenNotEnoughAccessibleVideos() {
        val current = sampleVideo(id = "current")
        val videos = listOf(
            current,
            sampleVideo(id = "free-1", isPremium = false),
            sampleVideo(id = "premium-1", isPremium = true)
        )

        val related = videos.relatedVideosFor(
            currentVideo = current,
            isPremiumActive = false,
            limit = 25
        )

        assertEquals(1, related.size)
        assertEquals("free-1", related.single().id)
    }

    private fun sampleVideo(
        id: String,
        isPremium: Boolean = false,
        relatedVideoIds: List<String> = emptyList()
    ): Video {
        return Video(
            id = id,
            title = "Title $id",
            format = "hls",
            manifestUrl = "https://example.com/$id/master.m3u8",
            durationMs = 60_000,
            previewImageUrl = "https://example.com/$id.png",
            locale = "uk",
            isPremium = isPremium,
            relatedVideoIds = relatedVideoIds
        )
    }
}
