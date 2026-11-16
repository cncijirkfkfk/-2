

package com.dongmanrepublic.cupfox

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin

@CloudstreamPlugin
class CupFoxPlugin : Plugin() {
    override fun load() {
        registerMainAPI(CupFoxProvider())
    }
}