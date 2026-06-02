package com.globaldevmax.app.imio.data.repository

import com.globaldevmax.app.imio.domain.model.Video
import com.globaldevmax.app.imio.domain.repository.VideoRepository
import com.globaldevmax.app.imio.network.api.VideosApiService
import com.globaldevmax.app.imio.network.mapper.toDomain
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class VideoRepositoryImpl(
    private val videosApiService: VideosApiService
) : VideoRepository {

    @Volatile
    private var cachedVideos: List<Video> = emptyList()

    @Volatile
    private var hasShuffledThisSession: Boolean = false

    private val fetchMutex = Mutex()
    private var inFlightFetch: CompletableDeferred<Result<List<Video>>>? = null

    override suspend fun getVideos(): Result<List<Video>> {
        val (deferred, shouldFetch) = fetchMutex.withLock {
            val active = inFlightFetch?.takeIf { !it.isCompleted }
            if (active != null) {
                active to false
            } else {
                CompletableDeferred<Result<List<Video>>>().also { inFlightFetch = it } to true
            }
        }

        if (!shouldFetch) {
            return deferred.await()
        }

        val result = runCatching {
            val videos = videosApiService.getVideos().videos.map { it.toDomain() }
            updateCachedVideos(videos)
            cachedVideos
        }
        deferred.complete(result)
        fetchMutex.withLock {
            if (inFlightFetch === deferred) {
                inFlightFetch = null
            }
        }
        return result
    }

    override fun getCachedVideos(): List<Video> = cachedVideos

    override fun getCachedVideoById(id: String): Video? {
        return cachedVideos.find { it.id == id }
    }

    private fun updateCachedVideos(freshVideos: List<Video>) {
        cachedVideos = if (!hasShuffledThisSession) {
            hasShuffledThisSession = true
            freshVideos.shuffled()
        } else {
            mergePreservingOrder(cachedVideos, freshVideos)
        }
    }

    private fun mergePreservingOrder(
        previousVideos: List<Video>,
        freshVideos: List<Video>
    ): List<Video> {
        val freshById = freshVideos.associateBy { it.id }
        val preserved = previousVideos.mapNotNull { freshById[it.id] }
        val preservedIds = preserved.map { it.id }.toSet()
        val added = freshVideos.filter { it.id !in preservedIds }
        return preserved + added
    }
}
