package com.github.christophpickl.urclubs.myclubs.cache.entities

import com.github.christophpickl.urclubs.myclubs.UserMycJson
import org.ehcache.spi.copy.Copier

data class CachedUserMycJson(
    val id: String?,
    val email: String?,
    val firstName: String?,
    val lastName: String?
) {

    @Suppress("unused") // needed for kryo
    constructor() : this(null, null, null, null)

    constructor(original: UserMycJson) : this(
        id = original.id,
        email = original.email,
        firstName = original.firstName,
        lastName = original.lastName
    )

    fun toUserMycJson() = UserMycJson(
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
