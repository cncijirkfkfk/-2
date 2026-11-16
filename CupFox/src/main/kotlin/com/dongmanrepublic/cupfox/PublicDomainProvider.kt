
package com.dongmanrepublic.cupfox

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.ExtractorLinkType
import com.lagradost.cloudstream3.utils.Qualities
import com.lagradost.cloudstream3.utils.newExtractorLink
import org.json.JSONObject
import java.net.URLEncoder

class PublicDomainProvider : MainAPI() {

    override var mainUrl = "https://archive.org"
    override var name = "公版影视（Prelinger）"
    override var lang = "en"
    override val hasMainPage = false

    override val supportedTypes = setOf(TvType.Movie)

    private val userAgent =
        "Mozilla/5.0 (Android) AppleWebKit/537.36 Chrome/120 Mobile Safari/537.36"

    private fun encode(value: String): String {
        return URLEncoder.encode(value, "UTF-8")
            .replace("+", "%20")
    }

    private fun safeTitle(value: String): String {
        return value.replace(Regex("[^\\p{L}\\p{N}\\s-]"), " ")
            .trim()
            .replace(Regex("\\s+"), " ")
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val cleaned = safeTitle(query)
        if (cleaned.isBlank()) return emptyList()

        val terms = cleaned.split(" ")
            .filter { it.isNotBlank() }

        val titleQuery = terms.joinToString(" AND ") {
            "title:${it}"
        }

        val archiveQuery =
            "collection:prelinger AND mediatype:movies " +
            "AND licenseurl:*publicdomain* AND ($titleQuery)"

        val apiUrl =
            "$mainUrl/advancedsearch.php" +
            "?q=${encode(archiveQuery)}" +
            "&fl[]=identifier" +
            "&fl[]=title" +
            "&fl[]=date" +
            "&fl[]=licenseurl" +
            "&rows=30" +
            "&page=1" +
            "&output=json"

        val response = app.get(
            apiUrl,
            headers = mapOf("User-Agent" to userAgent)
        )

        val json = JSONObject(response.text)
        val docs = json.optJSONObject("response")
            ?.optJSONArray("docs")
            ?: return emptyList()

        val results = mutableListOf<SearchResponse>()

        for (i in 0 until docs.length()) {
            val item = docs.optJSONObject(i) ?: continue

            val id = item.optString("identifier")
            val title = item.optString("title")

            if (id.isBlank() || title.isBlank()) continue

            val license = item.optString("licenseurl")
            if (!license.contains("publicdomain", ignoreCase = true)) {
                continue
            }

            results.add(
                newMovieSearchResponse(
                    title,
                    id,
                    TvType.Movie,
                    false
                ) {
                    this.posterUrl =
                        "$mainUrl/services/img/$id"

                    this.year =
                        item.optString("date")
                            .take(4)
                            .toIntOrNull()
                }
            )
        }

        return results
    }

    override suspend fun load(url: String): LoadResponse {
        val id = url.trim()

        require(
            id.matches(Regex("[A-Za-z0-9_-]{1,100}"))
        ) { "无效的 Internet Archive 影片编号" }

        val response = app.get(
            "$mainUrl/metadata/$id",
            headers = mapOf("User-Agent" to userAgent)
        )

        val root = JSONObject(response.text)
        val metadata = root.optJSONObject("metadata")
            ?: throw ErrorLoadingException("无法读取影片资料")

        val license = metadata.optString("licenseurl")

        if (!license.contains("publicdomain", ignoreCase = true)) {
            throw ErrorLoadingException(
                "该影片没有明确的 Public Domain 标记"
            )
        }

        val title = metadata.optString("title")
            .takeIf { it.isNotBlank() }
            ?: id

        val description = metadata.optString("description")
            .takeIf { it.isNotBlank() }

        val files = root.optJSONArray("files")
            ?: throw ErrorLoadingException("没有找到影片文件")

        var videoFile: String? = null

        for (i in 0 until files.length()) {
            val file = files.optJSONObject(i) ?: continue
            val fileName = file.optString("name")

            if (
                fileName.endsWith(".mp4", ignoreCase = true) &&
                file.optString("private") != "true"
            ) {
                videoFile = fileName
                break
            }
        }

        val selectedFile = videoFile
            ?: throw ErrorLoadingException("没有找到可播放的 MP4 文件")

        val videoUrl =
            "$mainUrl/download/$id/${encode(selectedFile)}"

        return newMovieLoadResponse(
            title,
            id,
            TvType.Movie,
            videoUrl
        ) {
            this.posterUrl = "$mainUrl/services/img/$id"
            this.plot = description
            this.year = metadata.optString("year")
                .toIntOrNull()
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (com.lagradost.cloudstream3.utils.ExtractorLink) -> Unit
    ): Boolean {

        if (!data.startsWith("$mainUrl/download/")) {
            return false
        }

        callback.invoke(
            newExtractorLink(
                source = name,
                name = "Public Domain MP4",
                url = data,
                type = ExtractorLinkType.VIDEO
            ) {
                quality = Qualities.P480.value
            }
        )

        return true
    }
}