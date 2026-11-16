
package com.dongmanrepublic.cupfox

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.ExtractorLink
import org.jsoup.nodes.Element

/**
 * First-pass CloudStream adapter for source_001 (茶杯狐).
 * This version reads same-site homepage links.
 * Search and playable video extraction are not yet implemented.
 */
class CupFoxProvider : MainAPI() {
    override var mainUrl = "https://cupfox.app"
    override var name = "茶杯狐（目录测试版）"
    override var lang = "zh"
    override val hasMainPage = true
    override val supportedTypes = setOf(
        TvType.Movie,
        TvType.TvSeries,
        TvType.Anime
    )

    override val mainPage = mainPageOf(
        "首页推荐" to "/"
    )

    override suspend fun getMainPage(
        page: Int,
        request: MainPageRequest
    ): HomePageResponse {
        if (page > 1) {
            return newHomePageResponse(request.name, emptyList())
        }

        val document = app.get(mainUrl).document

        val items = document.select("a[href]")
            .mapNotNull { it.toCupFoxCard() }
            .distinctBy { it.url }
            .take(40)

        return newHomePageResponse(request.name, items)
    }

    override suspend fun search(query: String): List<SearchResponse> {
        // Search endpoint has not been verified.
        return emptyList()
    }

    override suspend fun load(url: String): LoadResponse {
        val document = app.get(fixUrl(url)).document

        val title = document.selectFirst("h1")?.text()?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: document.title().trim()
                .takeIf { it.isNotBlank() }
            ?: "茶杯狐页面"

        val description =
            document.selectFirst("meta[name=description]")
                ?.attr("content")
                ?.takeIf { it.isNotBlank() }
                ?: document.body()?.text()?.take(1200)

        return newMovieLoadResponse(
            title,
            fixUrl(url),
            TvType.Movie,
            url
        ) {
            this.plot = description
            this.posterUrl =
                document.selectFirst("meta[property=og:image]")
                    ?.attr("content")
                    ?.takeIf { it.isNotBlank() }
                    ?.let { fixUrl(it) }
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        // No verified playable stream URL yet.
        return false
    }

    private fun Element.toCupFoxCard(): SearchResponse? {
        val href = attr("abs:href").ifBlank {
            return null
        }

        if (!href.startsWith(mainUrl)) return null

        if (
            href == "$mainUrl/" ||
            href.substringAfter(mainUrl).trim('/') in
            setOf("", "片单", "文章")
        ) return null

        val title = text().trim()
            .replace(Regex("\\s+"), " ")

        if (title.length < 4 || title.length > 90) return null

        if (title in setOf("更多", "首页", "片单", "文章")) {
            return null
        }

        val image = selectFirst("img")?.let {
            it.attr("abs:data-src")
                .ifBlank { it.attr("abs:src") }
        }?.takeIf { it.startsWith("http") }

        return newMovieSearchResponse(
            title,
            href,
            TvType.Movie
        ) {
            this.posterUrl = image
        }
    }
}