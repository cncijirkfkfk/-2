

package com.dongmanrepublic.cupfox

import com.lagradost.cloudstream3.*

class CupFoxProvider : MainAPI() {

    override var mainUrl = "https://www.libvio.app/"

    override var name = "聚合影视"

    override var lang = "zh"

    // 关闭首页，避免显示43个重复网站
    override val hasMainPage = false

    override val supportedTypes = setOf(TvType.Movie)

    // 暂时不提供搜索结果
    override suspend fun search(
        query: String
    ): List<SearchResponse> {
        return emptyList()
    }

    // 暂时停用旧的网页入口
    override suspend fun load(
        url: String
    ): LoadResponse {
        throw ErrorLoadingException("聚合影视旧网站入口已停用")
    }

    // 暂时不提供旧入口的播放链接
    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (com.lagradost.cloudstream3.utils.ExtractorLink) -> Unit
    ): Boolean {
        return false
    }
}