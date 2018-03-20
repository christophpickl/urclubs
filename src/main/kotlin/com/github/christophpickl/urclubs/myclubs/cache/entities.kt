package com.github.christophpickl.urclubs.myclubs.cache

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.ByteBufferInputStream
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.github.christophpickl.urclubs.myclubs.UserMycJson
import org.ehcache.spi.copy.Copier
import org.ehcache.spi.serialization.Serializer
import java.nio.ByteBuffer

// http://www.ehcache.org/blog/2016/05/12/ehcache3-serializers.html#third-party-serializers
abstract class AbstractCachedSerializer<T>(@Suppress("UNUSED_PARAMETER") loader: ClassLoader) : Serializer<T> {

    private val kryo = Kryo()
    private val bufferSize = 4096

    override fun serialize(obj: T): ByteBuffer {
        val output = Output(bufferSize)
        kryo.writeObject(output, obj)
        return ByteBuffer.wrap(output.buffer)
    }

    override fun read(binary: ByteBuffer): T {
        val input = Input(ByteBufferInputStream(binary))
        return kryo.readObject(input, objectType)
    }

    override fun equals(obj: T, binary: ByteBuffer) = obj == read(binary)

    protected abstract val objectType: Class<T>
}

data class CachedUserMycJson(
        val id: String?,
        val email: String?,
        val firstName: String?,
        val lastName: String?

) {

    constructor() : this(null, null, null, null) // needed for kryo

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

class CachedUserMycJsonSerializer(loader: ClassLoader) : AbstractCachedSerializer<CachedUserMycJson>(loader) {
    override val objectType = CachedUserMycJson::class.java
}

class CachedUserMycJsonCopier : Copier<CachedUserMycJson> {
    override fun copyForRead(obj: CachedUserMycJson) = obj.copy()
    override fun copyForWrite(obj: CachedUserMycJson) = obj.copy()
}
