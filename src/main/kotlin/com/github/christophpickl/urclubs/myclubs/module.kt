package com.github.christophpickl.urclubs.myclubs

import com.github.christophpickl.urclubs.UrclubsConfiguration
import com.github.christophpickl.urclubs.myclubs.cache.CacheFile
import com.github.christophpickl.urclubs.myclubs.cache.MyClubsCacheManager
import com.github.christophpickl.urclubs.myclubs.cache.MyClubsCachedApi
import com.github.christophpickl.urclubs.myclubs.parser.ActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.CourseHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.FinishedActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.PartnerDetailHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.PartnerHtmlModel
import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Scopes
import com.google.inject.Singleton
import java.time.LocalDateTime

class MyclubsModule : AbstractModule() {
    override fun configure() {
        if (UrclubsConfiguration.USE_STUBBED_MYCLUBS) {
            bind(MyClubsApi::class.java).toInstance(StubbedMyClubsApi)
        } else {
            bind(Http::class.java).to(HttpImpl::class.java)
            bind(MyclubsUtil::class.java).asEagerSingleton()
            bind(MyClubsApi::class.java).annotatedWith(HttpApi::class.java).to(MyClubsHttpApi::class.java).`in`(Scopes.SINGLETON)
            bind(MyClubsCachedApi::class.java).`in`(Scopes.SINGLETON)
            bind(MyClubsApi::class.java).to(MyClubsCachedApi::class.java)
            bind(MyClubsCacheManager::class.java).to(MyClubsCachedApi::class.java)
        }
    }

    @Provides
    @Singleton
    @CacheFile
    fun provideCacheDirectory() = UrclubsConfiguration.CACHE_DIRECTORY

}
