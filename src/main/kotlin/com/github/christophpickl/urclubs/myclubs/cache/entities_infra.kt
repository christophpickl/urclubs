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
abstract class AbstractCachedSerializer<T>(
    @Suppress("UNUSED_PARAMETER") loader: ClassLoader? = null,
    private val bufferSize: Int = 1024 * 10
) : Serializer<T> {

    private val kryo = Kryo()

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

fun <CACHED : ToModelable<MODEL>, MODEL : ToCacheable<CACHED>> buildCacheCoordinatesBySuperModel(
    cacheKey: String, fetchModel: (MyClubsApi) -> MODEL
) = CacheCoordinates(
    cacheKey = cacheKey,
    fetchModel = fetchModel,
    toModelTransformer = { it.toModel() },
    toCachedTransformer = { it.toCache() }
)

data class CacheSpec<CACHED>(
    val cacheAlias: String,
    val valueType: Class<CACHED>,
    val duration: Duration,
    val serializerType: Class<out Serializer<CACHED>>,
    val copierType: Class<out Copier<CACHED>>
)

data class CacheCoordinates<CACHED, MODEL>(
    val cacheKey: String,
    val toModelTransformer: (CACHED) -> MODEL,
    val toCachedTransformer: (MODEL) -> CACHED,
    val fetchModel: (MyClubsApi) -> MODEL
)

interface ToModelable <M> {
    fun toModel(): M
}

interface ToCacheable<C> {
    fun toCache(): C
}
