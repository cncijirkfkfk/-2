package com.dongmanrepublic.cupfox

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.ExtractorLink
import org.jsoup.nodes.Element

/**
 * First-pass CloudStream adapter for source_001 (茶杯狐).
 * The currently verified homepage is an editorial movie-list directory.
 * This build only exposes same-site article/list links; it does NOT claim to
 * extract playable media streams.
 */
class CupFoxProvider : MainAPI() {
    override var mainUrl = "https://cupfox.app"
    override var name = "茶杯狐（目录测试版）"
    override val lang = "zh"
    override val hasMainPage = true
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries, TvType.Anime)

    override val mainPage = mainPageOf(
        "首页推荐" to "/"
    )

    override suspend fun getMainPage(
        page: Int,
        request: MainPageRequest
    ): HomePageResponse {
        if (page > 1) return newHomePageResponse(request.name, emptyList())

        val document = app.get(mainUrl).document
        val items = document.select("a[href]")
            .mapNotNull { it.toCupFoxCard() }
            .distinctBy { it.url }
            .take(40)

        return newHomePageResponse(request.name, items)
    }

    override suspend fun search(query: String): List<SearchResponse> {
        // No search endpoint has been confirmed from the supplied source bundle.
        // Avoid guessing a URL or returning fabricated search results.
        return emptyList()
    }

    override suspend fun load(url: String): LoadResponse {
        val document = app.get(fixUrl(url)).document
        val title = document.selectFirst("h1")?.text()?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: document.title().trim().takeIf { it.isNotBlank() }
            ?: "茶杯狐页面"
        val description = document.selectFirst("meta[name=description]")?.attr("content")
            ?.takeIf { it.isNotBlank() }
            ?: document.body()?.text()?.take(1200)

        return newMovieLoadResponse(title, fixUrl(url), TvType.Movie, url) {
            this.plot = description
            this.posterUrl = document.selectFirst("meta[property=og:image]")?.attr("content")
                ?.takeIf { it.isNotBlank() }?.let { fixUrl(it) }
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        // The verified homepage exposes editorial/article links, not confirmed
        // playable stream URLs. Do not mislabel page URLs as video streams.
        return false
    }

    private fun Element.toCupFoxCard(): SearchResponse? {
        val href = attr("abs:href").ifBlank { return null }
        if (!href.startsWith(mainUrl)) return null
        if (href == "$mainUrl/" || href.substringAfter(mainUrl).trim('/') in
            setOf("", "片单", "文章")) return null

        val title = text().trim().replace(Regex("\\s+"), " ")
        if (title.length < 4 || title.length > 90) return null
        if (title in setOf("更多", "首页", "片单", "文章")) return null

        val image = selectFirst("img")?.let {
            it.attr("abs:data-src").ifBlank { it.attr("abs:src") }
        }?.takeIf { it.startsWith("http") }

        return newMovieSearchResponse(title, href, TvType.Movie) {
            this.posterUrl = image
        }
    }
}
