

package com.dongmanrepublic.cupfox

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.ExtractorLink

private data class MovieSource(
    val id: String,
    val name: String,
    val url: String,
    val enabled: Boolean = true
)

class CupFoxProvider : MainAPI() {

    override var mainUrl = "https://www.libvio.app/"
    override var name = "聚合影视"
    override var lang = "zh"
    override val hasMainPage = true

    override val supportedTypes = setOf(
        TvType.Movie,
        TvType.TvSeries,
        TvType.Anime
    )

    private val sources = listOf(
        MovieSource("source_002", "追影猫", "https://zhuiyingmiao2.com/"),
        MovieSource("source_003", "影猫仓库", "https://www.ymck.pro/"),
        MovieSource("source_004", "影路", "https://yingluu.com/#directory"),
        MovieSource("source_005", "搜剧AI", "https://souju.ai/"),
        MovieSource("source_006", "懂片帝AI", "https://dongpian17.com/"),
        MovieSource("source_007", "看客影视", "https://www.seesee.sbs/"),
        MovieSource("source_008", "高清点播", "https://hqvod.top/"),
        MovieSource("source_009", "大马猴", "https://dmhys.cc/"),
        MovieSource("source_010", "网飞猫", "https://www.ncat22.com/"),
        MovieSource("source_011", "LIBVIO", "https://www.libvio.app/"),
        MovieSource("source_012", "凡客影视", "https://fktv.me/"),
        MovieSource("source_013", "多多影视", "https://dduotv01.top/"),
        MovieSource("source_014", "唐人街影院", "https://trjyy.com/"),
        MovieSource("source_015", "大师兄影视", "https://www.ruiding3bu.com/"),
        MovieSource("source_016", "奴娜TV", "https://www.nntv.uk/"),
        MovieSource("source_017", "播剧网", "https://ysxq.cc/"),
        MovieSource("source_018", "搜TV啦", "https://www.sotvla3.cc"),
        MovieSource("source_019", "闪看影视", "https://www.shankankan.cc/"),
        MovieSource("source_020", "影视工厂", "https://www.iysgc.tv/"),
        MovieSource("source_021", "可可影视", "https://103.51.147.112:51120/"),
        MovieSource("source_022", "金牌影视", "https://www.x8kb9k8.com/"),
        MovieSource("source_023", "爱电影", "https://7ighz2.com/"),
        MovieSource("source_024", "小宝影院", "https://xiaoheimi.cc/"),
        MovieSource("source_025", "蚂蚁影视", "https://www.mayi520.org/"),
        MovieSource("source_026", "片吧影视", "https://www.pianbas.com/"),
        MovieSource("source_027", "麦田影院", "https://mtyy2.cc/"),
        MovieSource("source_028", "两个BT影视", "https://www.bttwo.life/"),
        MovieSource("source_029", "影视猫", "https://ysmao.cc/"),
        MovieSource("source_030", "4K在线", "https://www.bbibib.top/"),
        MovieSource("source_031", "APP影院", "https://www.appmovie.art/"),
        MovieSource("source_032", "PPnix", "https://www.ppnix.com/cn/"),
        MovieSource("source_033", "注视影视", "https://gaze.red/"),
        MovieSource("source_034", "嘀嗒影视", "https://www.didahd.xyz/"),
        MovieSource("source_035", "秒看影视", "https://miaokan.cc/"),
        MovieSource("source_036", "看戏影视", "https://www.kanxiya.com/"),
        MovieSource("source_037", "看片狂人", "https://kpkuang.us/"),
        MovieSource("source_038", "光速影视", "https://4kzaixian.top/"),
        MovieSource("source_039", "七味网", "https://www.qncool.com/"),
        MovieSource("source_040", "极光影视", "https://www.zhenbowang.com/"),
        MovieSource("source_041", "修罗影视", "https://www.bdys.dpdns.org/"),
        MovieSource("source_042", "猪猪影视", "https://www.ixinxiu.com/"),
        MovieSource("source_043", "厂长资源", "https://www.4kcz.com/"),
        MovieSource("source_044", "爱看机器人", "https://www1.ikanbot.com/")
    )

    override val mainPage = mainPageOf(
        "43个影视网站" to "/"
    )

    override suspend fun getMainPage(
        page: Int,
        request: MainPageRequest
    ): HomePageResponse {

        val items = sources
            .filter { it.enabled }
            .map { source ->
                newMovieSearchResponse(
                    source.name,
                    source.url,
                    TvType.Movie
                )
            }

        return newHomePageResponse(
            request.name,
            items
        )
    }

    override suspend fun search(
        query: String
    ): List<SearchResponse> {
        return emptyList()
    }

    override suspend fun load(
        url: String
    ): LoadResponse {

        val document = app.get(url).document

        val title = document.selectFirst("h1")
            ?.text()
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: document.title().trim()
                .takeIf { it.isNotBlank() }
            ?: "网站页面"

        val description =
            document.selectFirst("meta[name=description]")
                ?.attr("content")
                ?.takeIf { it.isNotBlank() }
                ?: document.body()?.text()?.take(1200)

        val poster =
            document.selectFirst("meta[property=og:image]")
                ?.attr("content")
                ?.takeIf { it.isNotBlank() }

        return newMovieLoadResponse(
            title,
            url,
            TvType.Movie,
            url
        ) {
            this.plot = description
            this.posterUrl = poster
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        return false
    }
}