package com.github.christophpickl.urclubs.myclubs.cache

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.ByteBufferInputStream
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import org.ehcache.spi.copy.Copier
import org.ehcache.spi.serialization.Serializer
import java.nio.ByteBuffer
import java.time.Duration

// http://www.ehcache.org/blog/2016/05/12/ehcache3-serializers.html#third-party-serializers
abstract class AbstractCachedSerializer<T>(@Suppress("UNUSED_PARAMETER") loader: ClassLoader? = null) : Serializer<T> {

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

fun <CACHED : ToModelable<MODEL>, MODEL : ToCacheable<CACHED>, REQUEST> keyedCoordinates(
    cacheKey: String, request: REQUEST, withDelegate: (MyClubsApi) -> MODEL
) = KeyedCacheCoordinates<CACHED, MODEL, REQUEST>(
    cacheKey = cacheKey,
    request = request,
    withDelegate = withDelegate,
    toModel = { it.toModel() },
    toCache = { it.toCache() }
)

data class CacheSpec<CACHED, MODEL>(
    val cacheAlias: String,
    val valueType: Class<CACHED>,
    val duration: Duration,
    val serializerType: Class<out Serializer<CACHED>>,
    val copierType: Class<out Copier<CACHED>>
) {
    val keyType = String::class.java
}

data class SingleCacheCoordinates<CACHED, MODEL>(
    val staticKey: String,
    val transToModel: (CACHED) -> MODEL,
    val fetch: (MyClubsApi) -> MODEL,
    val transToCache: (MODEL) -> CACHED
)

data class KeyedCacheCoordinates<CACHED, MODEL, REQUEST>(
    val cacheKey: String,
    val request: REQUEST,
    val toModel: (CACHED) -> MODEL,
    val toCache: (MODEL) -> CACHED,
    val withDelegate: (MyClubsApi) -> MODEL
)

interface ToModelable <M> {
    fun toModel(): M
}

interface ToCacheable<C> {
    fun toCache(): C
}
