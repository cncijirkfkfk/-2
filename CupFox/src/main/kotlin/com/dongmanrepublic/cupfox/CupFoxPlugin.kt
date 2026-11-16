
package com.dongmanrepublic.cupfox

import android.content.Context
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin

@CloudstreamPlugin
class CupFoxPlugin : Plugin() {
    override fun load(context: Context) {
        registerMainAPI(CupFoxProvider())
    }
}