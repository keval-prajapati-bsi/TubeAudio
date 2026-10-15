package com.vibesync.app.extractor

import com.vibesync.app.data.db.TrackEntity
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.search.SearchInfo
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeSearchQueryHandlerFactory
import org.schabi.newpipe.extractor.stream.StreamInfoItem

data class SearchResult(
    val videoId: String,
    val url: String,
    val title: String,
    val uploaderName: String?,
    val thumbnailUrl: String?,
    val durationSeconds: Long,
    val viewCount: Long,
    /** Null when YouTube's search response didn't include a structured date for this item --
     *  happens sometimes for lightweight search-result items (a full per-video fetch would be
     *  needed to guarantee one, which isn't done here -- too many extra network calls for a
     *  results list). Verified via `StreamInfoItem.getUploadDate(): DateWrapper?` /
     *  `DateWrapper.getInstant()` on the actual NewPipeExtractor jar before relying on it. */
    val uploadDateEpochMillis: Long?
)

/** A not-yet-saved [TrackEntity] built straight from search-result metadata -- for playing/
 *  queuing a result directly without adding it to the library first (see
 *  [com.vibesync.app.ui.MainViewModel.playSearchResultDirectly]). */
fun SearchResult.toTransientTrackEntity(): TrackEntity = TrackEntity(
    videoId = videoId,
    url = url,
    title = title,
    thumbnailUrl = thumbnailUrl,
    durationSeconds = durationSeconds,
    addedAtEpochMillis = System.currentTimeMillis()
)

class SearchException(message: String, cause: Throwable? = null) : Exception(message, cause)

/** How a [TrendingFilter]'s raw search results get ordered -- YouTube's own search relevance
 *  ranking doesn't actually correspond to "newest" or "most viewed", so a filter that's meant to
 *  represent either of those re-sorts client-side using the real metadata NewPipeExtractor
 *  reports, rather than trusting search-result order to mean what its label says. */
enum class TrendingSort { RELEVANCE, LATEST, MOST_VIEWED }

/** A Trending-tab quick filter. [query] drives the underlying song search (see
 *  [YouTubeSearcher.trending]) -- there's no real "charts" API available here, so each filter
 *  is an approximation via a representative search query rather than a true ranked chart; [sort]
 *  is what makes the *ordering* within that approximation actually match the filter's name. */
data class TrendingFilter(val label: String, val query: String, val sort: TrendingSort = TrendingSort.RELEVANCE)

/** Computed live (not hardcoded) so "Top Hits" always tracks the current year without needing
 *  a code change every January. */
private val currentYear: Int = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)

val TrendingFilters = listOf(
    TrendingFilter("Top 50 India", "top 50 india songs", TrendingSort.MOST_VIEWED),
    TrendingFilter("Viral 50", "viral songs india", TrendingSort.MOST_VIEWED),
    TrendingFilter("Top Hits $currentYear", "top hits $currentYear india", TrendingSort.LATEST),
    TrendingFilter("Hip-Hop India", "hip hop india songs", TrendingSort.MOST_VIEWED)
)

/** Searches YouTube by title via NewPipeExtractor -- same "no official API" approach as
 *  [AudioStreamResolver], just for discovery instead of resolving a link you already have. */
object YouTubeSearcher {

    /** [contentFilter] defaults to the "Songs" category (music-only results, this app's original
     *  and still most common use case); [podcastSearch] passes the general "Videos" filter
     *  instead, since podcast episodes aren't music and the Songs filter would exclude them. */
    fun search(query: String, contentFilter: String = YoutubeSearchQueryHandlerFactory.MUSIC_SONGS): List<SearchResult> {
        if (query.isBlank()) return emptyList()
        try {
            val handler = ServiceList.YouTube.searchQHFactory
                .fromQuery(query, listOf(contentFilter), null)
            val info = SearchInfo.getInfo(ServiceList.YouTube, handler)
            return info.relatedItems.toSearchResults()
        } catch (e: Exception) {
            throw SearchException("Search failed (${e.message ?: e::class.simpleName}).", e)
        }
    }

    /** Trending tab's "Top charts" listing for a specific filter card. YouTube's general
     *  "Trending" kiosk mixes in every content category (gaming, news, movie trailers...), not
     *  just music, and kiosk items don't reliably carry a duration -- so instead of that kiosk,
     *  this reuses the exact same [search] path (already filtered to the "Songs" category,
     *  already returning real durations) with the filter's representative query, then re-sorts
     *  client-side per [TrendingFilter.sort] so "Top Hits" is actually ordered by upload date and
     *  the popularity-oriented filters are actually ordered by view count, instead of both just
     *  reflecting YouTube's generic search-relevance order. A result missing upload-date metadata
     *  (see [SearchResult.uploadDateEpochMillis]) sorts to the back for [TrendingSort.LATEST],
     *  rather than being dropped -- still worth showing, just without a reliable position. */
    fun trending(filter: TrendingFilter): List<SearchResult> {
        val results = search(filter.query)
        return when (filter.sort) {
            TrendingSort.RELEVANCE -> results
            TrendingSort.LATEST -> results.sortedByDescending { it.uploadDateEpochMillis ?: 0L }
            TrendingSort.MOST_VIEWED -> results.sortedByDescending { it.viewCount }
        }
    }

    /** The Trending tab's main, always-on "Trending songs" list -- not affected by which "Top
     *  charts" card was tapped (tapping one now navigates into its own filtered list instead of
     *  changing this one). Same representative-query approximation as [trending]. */
    fun allTrending(): List<SearchResult> = search("trending songs india")

    /** Podcast search -- the general "Videos" filter (not "Songs"), sorted newest-first since
     *  "the latest podcasts, date-wise" was the actual ask, not YouTube's generic relevance order.
     *  A blank [query] falls back to a canned "podcast" search for the Podcasts tab's default
     *  listing, same "representative query, not a real category API" approximation this app
     *  already uses for Trending -- there's no dedicated "podcasts" search filter in
     *  NewPipeExtractor/YouTube's own search API, verified via `YoutubeSearchQueryHandlerFactory`'s
     *  actual available content filters before assuming one existed. */
    fun podcastSearch(query: String): List<SearchResult> {
        val effectiveQuery = query.ifBlank { "podcast" }
        val results = search(effectiveQuery, YoutubeSearchQueryHandlerFactory.VIDEOS)
        return results.sortedByDescending { it.uploadDateEpochMillis ?: 0L }
    }

    private fun List<org.schabi.newpipe.extractor.InfoItem>.toSearchResults(): List<SearchResult> =
        filterIsInstance<StreamInfoItem>()
            .mapNotNull { item ->
                val videoId = AudioStreamResolver.extractVideoId(item.url) ?: return@mapNotNull null
                SearchResult(
                    videoId = videoId,
                    url = "https://www.youtube.com/watch?v=$videoId",
                    title = item.name,
                    uploaderName = item.uploaderName,
                    thumbnailUrl = item.thumbnails.maxByOrNull { it.height }?.url,
                    durationSeconds = item.duration.coerceAtLeast(0),
                    viewCount = item.viewCount.coerceAtLeast(0),
                    uploadDateEpochMillis = item.uploadDate?.instant?.toEpochMilli()
                )
            }
}
