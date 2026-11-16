

package com.dongmanrepublic.cupfox

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin

@CloudstreamPlugin
class CupFoxPlugin : Plugin() {

    override fun load() {
        // 原来的 43 个网站入口
        registerMainAPI(CupFoxProvider())

        // 新增：公开领域影视搜索和播放
        registerMainAPI(PublicDomainProvider())
    }
}