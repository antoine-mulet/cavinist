package com.amulet.cavinist.web.context

import com.amulet.cavinist.web.query.*
import org.springframework.context.*
import org.springframework.stereotype.Component

@Component
class WineQueryContext : ApplicationContextAware {

    companion object Companion {
        private lateinit var context: ApplicationContext

        fun getWineryQuery(): WineryQuery = context.getBean(WineryQuery::class.java)
        fun getRegionQuery(): RegionQuery = context.getBean(RegionQuery::class.java)
    }


    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }
}