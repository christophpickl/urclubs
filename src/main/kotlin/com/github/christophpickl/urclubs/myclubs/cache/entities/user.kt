package com.github.christophpickl.urclubs.myclubs.cache.entities

import com.github.christophpickl.urclubs.myclubs.UserMycJson
import com.github.christophpickl.urclubs.myclubs.cache.AbstractCachedSerializer
import com.github.christophpickl.urclubs.myclubs.cache.CacheCoordinates
import com.github.christophpickl.urclubs.myclubs.cache.CacheSpec
import org.ehcache.spi.copy.Copier
import java.time.Duration
import java.time.temporal.ChronoUnit

val userSpec: CacheSpec<CachedUserMycJson> = CacheSpec(
    cacheAlias = "userAlias",
    valueType = CachedUserMycJson::class.java,
    duration = Duration.of(5, ChronoUnit.DAYS),
    serializerType = CachedUserMycJsonSerializer::class.java,
    copierType = CachedUserMycJsonCopier::class.java
)

val userCoordinates = CacheCoordinates(
    cacheKey = "userKey",
    fetchModel = { it.loggedUser() },
    toModelTransformer = { it.toModel() },
    toCachedTransformer = { CachedUserMycJson.byOriginal(it) }
)

data class CachedUserMycJson(
    val id: String?,
    val email: String?,
    val firstName: String?,
    val lastName: String?
) {

    @Suppress("unused") // needed for kryo
    constructor() : this(null, null, null, null)

    companion object {
        fun byOriginal(original: UserMycJson) = CachedUserMycJson(
            id = original.id,
            email = original.email,
            firstName = original.firstName,
            lastName = original.lastName
        )
    }

    fun toModel() = UserMycJson(
        id = id!!,
        email = email!!,
        firstName = firstName!!,
        lastName = lastName!!
    )
}

class CachedUserMycJsonSerializer(loader: ClassLoader? = null) : AbstractCachedSerializer<CachedUserMycJson>(loader) {
    override val objectType = CachedUserMycJson::class.java
}

class CachedUserMycJsonCopier : Copier<CachedUserMycJson> {
    override fun copyForRead(obj: CachedUserMycJson) = obj.copy()
    override fun copyForWrite(obj: CachedUserMycJson) = obj.copy()
}
