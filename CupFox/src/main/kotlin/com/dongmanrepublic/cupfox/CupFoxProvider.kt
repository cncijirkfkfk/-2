
package com.dongmanrepublic.cupfox

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.ExtractorLink
import org.jsoup.nodes.Element

private data class CupFoxSource(
    val name: String,
    val url: String,
    val enabled: Boolean = true
)

class CupFoxProvider : MainAPI() {

    override var mainUrl = "https://cupfox.app"
    override var name = "动漫共和国 · 44个网站"
    override var lang = "zh"
    override val hasMainPage = true

    override val supportedTypes = setOf(
        TvType.Movie,
        TvType.TvSeries,
        TvType.Anime
    )

    private val sources = listOf(
        CupFoxSource("茶杯狐", "https://cupfox.app/"),
        CupFoxSource("追影猫", "https://zhuiyingmiao2.com/"),
        CupFoxSource("影猫仓库", "https://www.ymck.pro/"),
        CupFoxSource("影路", "https://yingluu.com/#directory"),
        CupFoxSource("搜剧AI", "https://souju.ai/"),
        CupFoxSource("懂片帝AI", "https://dongpian17.com/"),
        CupFoxSource("看客影视", "https://www.seesee.sbs/"),
        CupFoxSource("高清点播", "https://hqvod.top/"),
        CupFoxSource("大马猴", "https://dmhys.cc/"),
        CupFoxSource("网飞猫", "https://www.ncat22.com/"),
        CupFoxSource("LIBVIO", "https://www.libvio.app/"),
        CupFoxSource("凡客影视", "https://fktv.me/"),
        CupFoxSource("多多影视", "https://dduotv01.top/"),
        CupFoxSource("唐人街影院", "https://trjyy.com/"),
        CupFoxSource("大师兄影视", "https://www.ruiding3bu.com/"),
        CupFoxSource("奴娜TV", "https://www.nntv.uk/"),
        CupFoxSource("播剧网", "https://ysxq.cc/"),
        CupFoxSource("搜TV啦", "https://www.sotvla3.cc"),
        CupFoxSource("闪看影视", "https://www.shankankan.cc/"),
        CupFoxSource("影视工厂", "https://www.iysgc.tv/"),
        CupFoxSource("可可影视", "https://103.51.147.112:51120/"),
        CupFoxSource("金牌影视", "https://www.x8kb9k8.com/"),
        CupFoxSource("爱电影", "https://7ighz2.com/"),
        CupFoxSource("小宝影院", "https://xiaoheimi.cc/"),
        CupFoxSource("蚂蚁影视", "https://www.mayi520.org/"),
        CupFoxSource("片吧影视", "https://www.pianbas.com/"),
        CupFoxSource("麦田影院", "https://mtyy2.cc/"),
        CupFoxSource("两个BT影视", "https://www.bttwo.life/"),
        CupFoxSource("影视猫", "https://ysmao.cc/"),
        CupFoxSource("4K在线", "https://www.bbibib.top/"),
        CupFoxSource("APP影院", "https://www.appmovie.art/"),
        CupFoxSource("PPnix", "https://www.ppnix.com/cn/"),
        CupFoxSource("注视影视", "https://gaze.red/"),
        CupFoxSource("嘀嗒影视", "https://www.didahd.xyz/"),
        CupFoxSource("秒看影视", "https://miaokan.cc/"),
        CupFoxSource("看戏影视", "https://www.kanxiya.com/"),
        CupFoxSource("看片狂人", "https://kpkuang.us/"),
        CupFoxSource("光速影视", "https://4kzaixian.top/"),
        CupFoxSource("七味网", "https://www.qncool.com/"),
        CupFoxSource("极光影视", "https://www.zhenbowang.com/"),
        CupFoxSource("修罗影视", "https://www.bdys.dpdns.org/"),
        CupFoxSource("猪猪影视", "https://www.ixinxiu.com/"),
        CupFoxSource("厂长资源", "https://www.4kcz.com/"),
        CupFoxSource("爱看机器人", "https://www1.ikanbot.com/")
    )

    override val mainPage = mainPageOf(
        "44个网站入口" to "/"
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

        val poster = document
            .selectFirst("meta[property=og:image]")
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